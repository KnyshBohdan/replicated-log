package com.replog.secondary;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondaryServer {

    private static final Logger logger = LoggerFactory.getLogger(SecondaryServer.class);
    private Server server;

    private void start(int port) throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new MessageServiceImpl())
                .build()
                .start();
        logger.info("Secondary server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            SecondaryServer.this.stop();
            logger.info("Server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 50051; // Default port
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        final SecondaryServer server = new SecondaryServer();
        server.start(port);
        server.blockUntilShutdown();
    }
}
