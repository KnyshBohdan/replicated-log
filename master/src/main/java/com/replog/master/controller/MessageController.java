package com.replog.master.controller;

import com.replog.master.model.Message;
import com.replog.master.discovery.SecondaryServerDiscovery;
import com.replog.master.model.SecondaryServer;
import com.replog.master.model.SecondaryServers;
import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private List<Message> messages = new ArrayList<>();

    private final SecondaryServerDiscovery secondaryServerDiscovery;

    public MessageController(SecondaryServerDiscovery secondaryServerDiscovery) {
        this.secondaryServerDiscovery = secondaryServerDiscovery;
    }

    @PostMapping("/messages")
    public ResponseEntity<String> addMessage(@RequestBody Message message) {
        logger.info("Received POST request with message: {}", message.getContent());

        List<String> failedServers = replicateToSecondaries(message);

        if (failedServers.isEmpty()) {
            messages.add(message);
            return ResponseEntity.ok("Message received and replicated successfully.\n");
        } else {
            logger.error("Failed to replicate message to the following secondary servers: {}", failedServers);
            String errorMessage = "Failed to replicate message to the following secondary servers: " + String.join(", ", failedServers) + "\n";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/messages")
    public List<Message> getMessages() {
        logger.info("Received GET request for all messages");
        return messages;
    }

    private List<String> replicateToSecondaries(Message message) {
        List<String> failedServers = new ArrayList<>();

        logger.info("Replicating message to secondaries: {}", message.getContent());

        SecondaryServers secondaryServers = secondaryServerDiscovery.getSecondaryServers();
        for (SecondaryServer secondaryServer : secondaryServers.getServers()) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(secondaryServer.getHost(), secondaryServer.getPort())
                    .usePlaintext()
                    .build();

            try {
                MessageServiceGrpc.MessageServiceBlockingStub stub = MessageServiceGrpc.newBlockingStub(channel);

                MessageProto.Message grpcMessage = MessageProto.Message.newBuilder()
                        .setContent(message.getContent())
                        .build();

                MessageProto.Ack ack = stub.replicateMessage(grpcMessage);

                if (ack.getSuccess()) {
                    logger.info("Received ACK from secondary at {}:{}", secondaryServer.getHost(), secondaryServer.getPort());
                } else {
                    logger.warn("Secondary at {}:{} failed to replicate message", secondaryServer.getHost(), secondaryServer.getPort());
                    failedServers.add(secondaryServer.getHost() + ":" + secondaryServer.getPort());
                }
            } catch (Exception e) {
                logger.error("Error during replication to secondary at {}:{}", secondaryServer.getHost(), secondaryServer.getPort(), e);
                failedServers.add(secondaryServer.getHost() + ":" + secondaryServer.getPort());
            } finally {
                channel.shutdown();
            }
        }

        logger.info("Replication to secondaries completed");
        return failedServers;
    }
}
