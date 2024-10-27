package com.replog.secondary.server;

import com.replog.secondary.config.SecondaryServerConfig;
import com.replog.secondary.processor.SecondaryMessageProcessor;
import com.replog.secondary.services.impl.MessageServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecondaryServer {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryServer.class);
    private Server server;

    @Autowired
    private SecondaryServerConfig config;

    @Autowired
    private SecondaryMessageProcessor messageProcessor;

    public void start(int port) throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new MessageServiceImpl(messageProcessor, config))
                .build()
                .start();
        logger.info("Secondary server started with ID {}, listening on port {}", config.getSlaveID(), port);

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
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
