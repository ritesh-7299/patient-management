package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import billing.DeleteBillingAccountRequest;
import billing.DeleteBillingAccountResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseStreamObserver) {
        log.info("\ncreteBilling account message received:\n{}", billingRequest.toString());

        //Business logic e.g insert a record in database

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteBillingAccount(
            DeleteBillingAccountRequest request,
            StreamObserver<DeleteBillingAccountResponse> streamObserver
    ) {
        log.info("\ndelete billing account message received:\n{}", request.toString());

        DeleteBillingAccountResponse resp = DeleteBillingAccountResponse
                .newBuilder()
                .setIsDeleted(true)
                .setMessage(request.getAccountId() + " is deleted successfully...")
                .build();
        streamObserver.onNext(resp);
        streamObserver.onCompleted();
    }
}
