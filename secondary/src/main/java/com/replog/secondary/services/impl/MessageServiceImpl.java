package com.replog.secondary.services.impl;

import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import com.replog.secondary.config.SecondaryServerConfig;
import com.replog.secondary.processor.SecondaryMessageProcessor;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends MessageServiceGrpc.MessageServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final SecondaryMessageProcessor messageProcessor;
    private final SecondaryServerConfig config;

    public MessageServiceImpl(SecondaryMessageProcessor messageProcessor, SecondaryServerConfig config) {
        this.messageProcessor = messageProcessor;
        this.config = config;
    }

    @Override
    public void replicateMessage(MessageProto.Message request, StreamObserver<MessageProto.Ack> responseObserver) {
        logger.info("Received message to replicate: {}", request.getContent());

        // Extract necessary fields from the request
        long masterTimestamp = request.getMasterTimestamp();
        double msgIDReal = request.getMsgIDReal();
        double msgIDImg = request.getMsgIDImg();

        try {
            // Simulate processing delay
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted", e);
        }

        // Store the message
        messageProcessor.addMessage(request);

        // Build the Ack message
        MessageProto.Ack ack = MessageProto.Ack.newBuilder()
                .setSlaveID(config.getSlaveID())
                .setMasterTimestamp(masterTimestamp)
                .setMsgIDReal(msgIDReal)
                .setMsgIDImg(msgIDImg)
                .setSuccess(true)
                .build();

        logger.info("Replication completed, sending ACK");
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
