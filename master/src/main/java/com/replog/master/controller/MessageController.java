package com.replog.master.controller;

import com.replog.common.counter.EndlessCounter;
import com.replog.common.model.EndlessCounterState;
import com.replog.common.model.Message;
import com.replog.common.model.MessageBuffer;
import com.replog.master.model.InputMessage;
import com.replog.common.model.Heartbeat;
import com.replog.master.controller.SecondaryHealthController;
import com.replog.master.discovery.SecondaryServerDiscovery;
import com.replog.master.model.SecondaryServer;
import com.replog.master.model.SecondaryServers;
import com.replog.master.controller.MessageSender;
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
import java.util.UUID;

@RestController
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageBuffer messagesBuffer = new MessageBuffer();

    private final SecondaryHealthController healthController = new SecondaryHealthController();
    private final MessageSender messageSender;

    private final String masterID = generateUniqueID();
    private final EndlessCounter endlessCounter = new EndlessCounter();
    private final EndlessCounterState endlessCounterState = new EndlessCounterState();

    private final List<MessageProto.Heartbeat> heartbeats = new ArrayList<>();

    public MessageController(SecondaryServerDiscovery secondaryServerDiscovery) {
        this.messageSender = new MessageSender(secondaryServerDiscovery, masterID);
    }

    @PostMapping("/messages")
    public ResponseEntity<String> addMessage(@RequestBody InputMessage message) {
        logger.info("Received POST request with message: {}", message.getContent());

        if(message.getWriteConcern() != 1 && message.getWriteConcern() != 2 && message.getWriteConcern() != 3){
            logger.info("Wrong write concern");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong write concern \n");
        }

        Message newMessage = new Message(message.getContent());
        EndlessCounterState msgCounterState = new EndlessCounterState(endlessCounterState);
        newMessage.setEndlessCounterState(msgCounterState);

        List<String> failedServers = messageSender.replicateToSecondaries(message.getWriteConcern(), newMessage);

        if (failedServers.isEmpty()) {
            messagesBuffer.add(new Message(newMessage));

            // increment the EndlessCounterState for new message IDs
            synchronized (endlessCounterState) {
                endlessCounter.increment(endlessCounterState);
            }

            return ResponseEntity.ok("Message received and replicated successfully.\n");
        } else {
            logger.error("Failed to replicate message to the following secondary servers: {}", failedServers);
            String errorMessage = "Failed to replicate message to the following secondary servers: " + String.join(", ", failedServers) + "\n";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/messages")
    public String getMessages() {
        logger.info("Received GET request for all messages");

        StringBuilder replay = new StringBuilder("Secondary processed messages: \n");
        int counter = 1;
        replay.append("Size of the buffer: ").append(messagesBuffer.getSize()).append("\n");

        if (messagesBuffer != null && messagesBuffer.getMessages() != null) {
            for (Message protoMessage : messagesBuffer.getMessages()) {
                if (protoMessage != null) {
                    replay.append(counter).append(": ").append(protoMessage.getContent()).append("\n");
                    counter += 1;
                }
                else{
                    logger.info("Some messages are null in GET");
                }
            }
        }

        replay.append("============================ \n");
        return replay.toString();
    }

    @PostMapping("/health")
    public ResponseEntity<String> receiveHealth(@RequestBody Heartbeat heartbeat) {
        logger.info("Received Heartbeat from Host: {}", heartbeat.getSlaveGRPCHost());
        healthController.process(heartbeat);
        messageSender.getSecondaryServerDiscovery().getSecondaryServers().updateSecondaryHealthStatuses(healthController.getHealthStatuses());
        return ResponseEntity.ok("Heartbeat received.\n");
    }

    private String generateUniqueID() {
        // Generate a random 6-character alphanumeric string
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
