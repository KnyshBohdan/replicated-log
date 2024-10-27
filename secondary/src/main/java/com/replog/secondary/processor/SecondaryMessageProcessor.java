package com.replog.secondary.processor;

import com.replog.common.model.Message;
import com.replog.proto.MessageProto;
import com.replog.secondary.config.SecondaryServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import for mapping protobuf message to HTTP response
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SecondaryMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryMessageProcessor.class);

    private final List<MessageProto.Message> messages = new ArrayList<>();

    @Autowired
    private SecondaryServerConfig config;

    @GetMapping("/messages")
    public List<Message> getMessages() {
        logger.info("Received GET request for all messages");

        List<Message> responseMessages = new ArrayList<>();
        for (MessageProto.Message protoMessage : messages) {
            responseMessages.add(new Message(protoMessage.getContent()));
        }
        return responseMessages;
    }

    public void addMessage(MessageProto.Message message) {
        messages.add(message);
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageProto.Heartbeat getHealth() {
        logger.info("Received GET request for health");

        long slaveTimestamp = System.nanoTime();
        String status = "WORKING";

        // Build the Heartbeat message
        MessageProto.Heartbeat heartbeat = MessageProto.Heartbeat.newBuilder()
                .setSlaveID(config.getSlaveID())
                .setSlaveTimestamp(slaveTimestamp)
                .setStatus(status)
                .build();

        return heartbeat;
    }
}
