package com.pm.stack;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App stack, final String id, final StackProps stackProps) {
        super(stack, id, stackProps);
        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase("PatientServiceDB", "patient-service-db");

        CfnHealthCheck authServiceDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");
        CfnHealthCheck patientServiceDbHealthCheck = createDbHealthCheck(patientServiceDb, "PatientServiceDBHealthCheck");

        CfnCluster mskCluster = createMskCluster();
        this.ecsCluster = createEcsCluster();

        FargateService authService = createService(
                "AuthService",
                "auth-service",
                authServiceDb,
                List.of(4005),
                Map.of("JWT_SECRET", "1x5mP2jQ8bS7nY0vT4hW9dC3rF6kZ8lJ0sM2aQ7tU4fN9rG6pC8zK5vW3eH1bL2x"));

        authService.getNode().addDependency(authServiceDb);
        authService.getNode().addDependency(authServiceDbHealthCheck);

        FargateService billingService = createService(
                "BillingService",
                "billing-service",
                null,
                List.of(4001, 9001),
                null
        );

        FargateService analyticsService = createService(
                "AnalyticsService",
                "analytics-service",
                null,
                List.of(4002),
                null
        );
        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createService(
                "PatientService",
                "patient-service",
                patientServiceDb,
                List.of(4000),
                Map.of("BILLING_SERVICE_ADDRESS", "host.docker.internal:4001",
                        "BILLING_SERVICE_GRPC_PORT", "9001")
        );

        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientServiceDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);

        createApiGateWayService();
    }

    //create a vpc (virtual private cloud) network
    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientManagementVpc")
                .vpcName("PatientManagementVpc")
                .maxAzs(2)
                .build();
    }

    //create databases (postgres) on aws
    private DatabaseInstance createDatabase(String id, String name) {
        return DatabaseInstance.Builder
                .create(this, id)
                .vpc(vpc)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps
                                .builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()
                ))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(name)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    //create health checks for databases
    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(
                        CfnHealthCheck.HealthCheckConfigProperty.builder()
                                .type("TCP")
                                .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                                .ipAddress(db.getDbInstanceEndpointAddress())
                                .requestInterval(30)
                                .failureThreshold(3)
                                .build()
                )
                .build();
    }

    //create a kafka cluster
    private CfnCluster createMskCluster() {
        return CfnCluster.Builder
                .create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(2)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(
                                vpc.getPrivateSubnets().stream()
                                        .map(ISubnet::getSubnetId)
                                        .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build()
                ).build();

    }

    //create an ecs cluster where our microservices live
    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
//                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
//                        .name("patient-management.local")
//                        .build())
                .build();
    }

    //create a task for ecs cluster
    private FargateService createService(String id, String imageName, DatabaseInstance db, List<Integer> ports, Map<String, String> additionalEnvVars) {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream()
                        .map(port -> PortMapping.builder()
                                .containerPort(port)
                                .hostPort(port)
                                .protocol(Protocol.TCP)
                                .build())
                        .toList())
                .logging(LogDriver.awsLogs(
                        AwsLogDriverProps.builder().logGroup(LogGroup.Builder
                                        .create(this, id + "LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build()
                                ).streamPrefix(imageName)
                                .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510,localhost.localstack.cloud:4511,localhost.localstack.cloud:4512");

        if (additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }

        if (db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "600000");
        }

        containerOptions.environment(envVars);

        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        return FargateService.Builder
                .create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .desiredCount(1)
                .build();
    }

    //create api gateway with load balancer
    private void createApiGateWayService() {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, "APIGatewayTaskDefinition")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("api-gateway"))
                .environment(Map.of(
                        "SPRING_PROFILES_ACTIVE", "prod",
                        "JWT_SECRET", "1x5mP2jQ8bS7nY0vT4hW9dC3rF6kZ8lJ0sM2aQ7tU4fN9rG6pC8zK5vW3eH1bL2x"
                ))
                .portMappings(Stream.of(4004)
                        .map(port ->
                                PortMapping.builder()
                                        .protocol(Protocol.TCP)
                                        .hostPort(port)
                                        .containerPort(port)
                                        .build()).toList())
                .logging(LogDriver.awsLogs(
                        AwsLogDriverProps.builder()
                                .streamPrefix("api-gateway")
                                .logGroup(LogGroup.Builder
                                        .create(this, "LogGroup")
                                        .retention(RetentionDays.ONE_DAY)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .logGroupName("/ecs/api-gateway")
                                        .build()
                                )
                                .build()
                ))
                .build();

        taskDefinition.addContainer("APIGatewayContainer", containerDefinitionOptions);

        ApplicationLoadBalancedFargateService.Builder
                .create(this, "APIGatewayService")
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .serviceName("api-gateway")
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();
    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps stackProps = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "pm", stackProps);
        app.synth();
        System.out.println("App synthesising in progress...");
    }
}
