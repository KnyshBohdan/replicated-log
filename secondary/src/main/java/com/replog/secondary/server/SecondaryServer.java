package com.replog.secondary.server;

import com.replog.secondary.config.SecondaryServerConfig;
import com.replog.secondary.processor.SecondaryMessageProcessor;
import com.replog.secondary.services.impl.MessageServiceImpl;
import com.replog.secondary.health.HealthSender;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SecondaryServer {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryServer.class);
    private Server server;

    @Value("${grpc.port}")
    private int grpcPort;

    @Autowired
    private SecondaryServerConfig config;

    @Autowired
    private SecondaryMessageProcessor messageProcessor;

    @Autowired
    private HealthSender healthSender; // Injected HealthSender

    public void start() throws Exception {
        server = ServerBuilder.forPort(grpcPort)
                .addService(new MessageServiceImpl(messageProcessor, config))
                .build()
                .start();
        logger.info("Secondary server started with ID {}, listening on port {}", config.getSlaveID(), grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            SecondaryServer.this.stop();
            logger.info("Server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
        // Optionally, shutdown the HealthSender
        healthSender.shutdown();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}