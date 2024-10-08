package com.replog.master.controller;

import com.replog.master.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private List<Message> messages = new ArrayList<>();

    @PostMapping("/messages")
    public ResponseEntity<String> addMessage(@RequestBody Message message) {
        logger.info("Received POST request with message: {}", message.getContent());
        messages.add(message);

        // Simulate replication to secondaries and wait for ACKs
        replicateToSecondaries(message);

        return ResponseEntity.ok("Message received and replicated");
    }

    @GetMapping("/messages")
    public List<Message> getMessages() {
        logger.info("Received GET request for all messages");
        return messages;
    }

    private void replicateToSecondaries(Message message) {
        // TODO: Implement the actual replication logic

        logger.info("Replicating message to secondaries: {}", message.getContent());

        // Simulate delay to test blocking replication
        try {
            Thread.sleep(1000); // Simulate delay (1 second)
        } catch (InterruptedException e) {
            logger.error("Error during replication delay", e);
            Thread.currentThread().interrupt();
        }

        // Simulate receiving ACKs from secondaries
        logger.info("Received ACKs from all secondaries");
    }
}
