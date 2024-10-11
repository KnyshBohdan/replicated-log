package com.replog.secondary.services.impl;

import com.replog.secondary.processor.SecondaryMessageProcessor;
import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends MessageServiceGrpc.MessageServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final SecondaryMessageProcessor messageProcessor;

    public MessageServiceImpl(SecondaryMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void replicateMessage(MessageProto.Message request, StreamObserver<MessageProto.Ack> responseObserver) {
        logger.info("Received message to replicate: {}", request.getContent());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted", e);
        }

        messageProcessor.addMessage(request);

        MessageProto.Ack ack = MessageProto.Ack.newBuilder()
                .setSuccess(true)
                .build();

        logger.info("Replication completed, sending ACK");
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
