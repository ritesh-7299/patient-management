package com.pm.patientservice.grpc;

import billing.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort
    ) {
        log.info("\nConnecting to grpc service at {}:{}", serverAddress, serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setEmail(email)
                .setName(name)
                .build();

        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("\nCreateBillingAccount: Received response from billing service via GRPC \n{}", response);
        return response;
    }

    public DeleteBillingAccountResponse deleteBillingAccount(String id) {
        DeleteBillingAccountRequest request = DeleteBillingAccountRequest
                .newBuilder()
                .setAccountId(id)
                .build();

        DeleteBillingAccountResponse response = blockingStub.deleteBillingAccount(request);
        log.info("\nDeleteBillingAccount: Received response from billing service via GRPC \n{}", response);

        return response;
    }
}
