package com.replog.secondary.processor;

import com.replog.secondary.model.Message;
import com.replog.proto.MessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SecondaryMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryMessageProcessor.class);

    private final List<MessageProto.Message> messages = new ArrayList<>();

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
}

