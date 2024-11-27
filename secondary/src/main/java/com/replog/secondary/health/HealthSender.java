package com.replog.secondary.health;

import com.replog.common.model.Heartbeat;
import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import com.replog.secondary.config.SecondaryServerConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthSender {

    private static final Logger logger = LoggerFactory.getLogger(HealthSender.class);

    private final SecondaryServerConfig config;
    private final String masterHost;
    private final String slaveGRPCHost;
    private final int masterHttpPort;

    private final RestTemplate restTemplate;

    @Autowired
    public HealthSender(
            SecondaryServerConfig config,
            @Value("${master.host}") String masterHost,
            @Value("${master.port}") int masterHttpPort,
            @Value("${grpc.host}") String slaveGRPCHost) {

        this.config = config;
        this.masterHost = masterHost;
        this.masterHttpPort = masterHttpPort;
        this.slaveGRPCHost = slaveGRPCHost;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 5000) // Send every 5 seconds
    public void sendHeartbeat() {
        long slaveTimestamp = System.nanoTime();
        String status = "WORKING";


        String url = String.format("http://%s:%d/health", masterHost, masterHttpPort);

        try {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.setSlaveID(config.getSlaveID());
            heartbeat.setSlaveTimestamp(slaveTimestamp);
            heartbeat.setSlaveGRPCHost(slaveGRPCHost);
            heartbeat.setStatus(status);

            ResponseEntity<String> response = restTemplate.postForEntity(url, heartbeat, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Heartbeat sent successfully to Master.");
            } else {
                logger.warn("Heartbeat not acknowledged by Master. Status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error while sending heartbeat to Master: ", e);
        }
    }

    // Shutdown method remains the same
    @PreDestroy
    public void shutdown() {
        // No resources to close for RestTemplate
    }
}
