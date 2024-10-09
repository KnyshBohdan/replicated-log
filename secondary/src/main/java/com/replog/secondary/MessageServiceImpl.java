package com.replog.secondary;

import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageServiceImpl extends MessageServiceGrpc.MessageServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public void replicateMessage(MessageProto.Message request, StreamObserver<MessageProto.Ack> responseObserver) {
        logger.info("Received message to replicate: {}", request.getContent());

        try {
            Thread.sleep(5000); // Sleep for 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted", e);
        }

        // Acknowledge the replication
        MessageProto.Ack ack = MessageProto.Ack.newBuilder()
                .setSuccess(true)
                .build();

        logger.info("Replication completed, sending ACK");
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
