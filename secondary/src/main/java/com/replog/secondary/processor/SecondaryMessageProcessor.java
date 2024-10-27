package com.replog.secondary.processor;

import com.replog.common.model.EndlessCounterState;
import com.replog.common.model.MessageBuffer;
import com.replog.common.model.Message;
import com.replog.proto.MessageProto;
import com.replog.secondary.config.SecondaryServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SecondaryMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryMessageProcessor.class);
    private final MessageBuffer messageBuffer = new MessageBuffer();

    @Autowired
    private SecondaryServerConfig config;

    @GetMapping("/messages")
    public String getMessages() {
        logger.info("Received GET request for all messages");

        String replay = "Secondary processed messages: \n";
        int counter = 1;

        for (Message protoMessage : messageBuffer.getMessages()) {
            replay = replay + counter + ": " + protoMessage.getContent() + "\n";
            counter += 1;
        }
        replay = replay + "============================ \n";
        return replay;
    }

    public void addMessage(MessageProto.Message message) {
        EndlessCounterState counterState = new EndlessCounterState();
        counterState.setImaginary(message.getMsgIDImg());
        counterState.setReal(message.getMsgIDReal());

        Message newMessage = new Message();
        newMessage.setContent(message.getContent());
        newMessage.setEndlessCounterState(counterState);

        messageBuffer.add(newMessage);
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
