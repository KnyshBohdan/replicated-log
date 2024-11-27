package com.replog.secondary.services.impl;

import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import com.replog.secondary.config.SecondaryServerConfig;
import com.replog.secondary.processor.SecondaryMessageProcessor;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

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

        long masterTimestamp = request.getMasterTimestamp();
        double msgIDReal = request.getMsgIDReal();
        double msgIDImg = request.getMsgIDImg();

        try {
            // generate a random wait time between 500ms and 5000ms
            int waitTime = new Random().nextInt(4501) + 500;
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted", e);
        }

        // randomly decide whether to send the Ack or not
        double ackProbability = 0.5; // 50% chance of sending the Ack
        if (Math.random() < ackProbability) {
            messageProcessor.addMessage(request);

            // build the Ack message
            MessageProto.Ack ack = MessageProto.Ack.newBuilder()
                    .setSlaveID(config.getSlaveID())
                    .setMasterTimestamp(masterTimestamp)
                    .setMsgIDReal(msgIDReal)
                    .setMsgIDImg(msgIDImg)
                    .setSuccess(true)
                    .build();

            logger.info("Replication completed, sending ACK");
            responseObserver.onNext(ack);
        } else {
            logger.info("Replication completed, not sending ACK");
            responseObserver.onError(Status.UNAVAILABLE.withDescription("Simulated packet loss").asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void sendHeartbeat(MessageProto.Heartbeat request, StreamObserver<MessageProto.Ack> responseObserver) {
        logger.info("Received Heartbeat from SlaveID: {}", request.getSlaveID());

        // Process the heartbeat as needed (e.g., update server status, log, etc.)

        // Prepare acknowledgment
        MessageProto.Ack ack = MessageProto.Ack.newBuilder()
                .setSlaveID(request.getSlaveID())
                .setMasterTimestamp(System.nanoTime())
                .setSuccess(true)
                .build();

        // Send acknowledgment
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
