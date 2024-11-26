package com.replog.master.controller;

import com.replog.common.model.Message;
import com.replog.master.discovery.SecondaryServerDiscovery;
import com.replog.master.model.SecondaryServer;
import com.replog.master.model.SecondaryServers;
import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final SecondaryServerDiscovery secondaryServerDiscovery;
    private final String masterID;
    private final ExecutorService executorService;

    public MessageSender(SecondaryServerDiscovery secondaryServerDiscovery,
                         String masterID) {
        this.secondaryServerDiscovery = secondaryServerDiscovery;
        this.masterID = masterID;
        this.executorService = Executors.newCachedThreadPool();
    }

    public SecondaryServerDiscovery getSecondaryServerDiscovery() {
        return secondaryServerDiscovery;
    }

    public List<String> replicateToSecondaries(Integer writeConcern, Message message) {
        logger.info("Replicating message to secondaries: {}", message.getContent());

        // get current timestamp and message IDs
        long masterTimestamp = System.nanoTime();

        SecondaryServers secondaryServers = secondaryServerDiscovery.getSecondaryServers();
        List<Future<String>> futures = new ArrayList<>();
        for (SecondaryServer secondaryServer : secondaryServers.getServers()) {
            SendingThread sendingThread = new SendingThread(secondaryServer, message, masterID, masterTimestamp);
            futures.add(executorService.submit(sendingThread));
        }

        List<String> failedServers = new ArrayList<>();
        if (writeConcern == 1) {
            // don't wait for any replication, make all asynchronous
            logger.info("Replication to secondaries initiated asynchronously");
        } else if (writeConcern == 2) {
            // wait for only one, fastest replication
            try {
                String result = futures.get(0).get();
                if (result != null) {
                    failedServers.add(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error waiting for fastest replication", e);
            }
            logger.info("Replication to fastest secondary completed");
        } else if (writeConcern == 3) {
            // wait for all replications
            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    if (result != null) {
                        failedServers.add(result);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error waiting for replication", e);
                }
            }
            logger.info("Replication to all secondaries completed");
        }

        return failedServers;
    }

    private static class SendingThread implements Callable<String> {
        private final SecondaryServer secondaryServer;
        private final Message message;
        private final String masterID;
        private final long masterTimestamp;

        public SendingThread(SecondaryServer secondaryServer, Message message, String masterID, long masterTimestamp) {
            System.out.println("Sending to server with health status: " + secondaryServer.getSecondaryHealthStatus());
            this.secondaryServer = secondaryServer;
            this.message = message;
            this.masterID = masterID;
            this.masterTimestamp = masterTimestamp;
        }

        @Override
        public String call() {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(secondaryServer.getHost(), secondaryServer.getPort())
                    .usePlaintext()
                    .build();

            try {
                MessageServiceGrpc.MessageServiceBlockingStub stub = MessageServiceGrpc.newBlockingStub(channel);

                MessageProto.Message grpcMessage = MessageProto.Message.newBuilder()
                        .setMasterID(masterID)
                        .setMasterTimestamp(masterTimestamp)
                        .setMsgIDReal(message.getEndlessCounterState().getReal())
                        .setMsgIDImg(message.getEndlessCounterState().getImaginary())
                        .setContent(message.getContent())
                        .build();

                MessageProto.Ack ack = stub.replicateMessage(grpcMessage);

                if (ack.getSuccess()) {
                    logger.info("Received ACK from secondary at {}:{}", secondaryServer.getHost(), secondaryServer.getPort());
                    return null;
                } else {
                    logger.warn("Secondary at {}:{} failed to replicate message", secondaryServer.getHost(), secondaryServer.getPort());
                    return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                }
            } catch (Exception e) {
                logger.error("Error during replication to secondary at {}:{}", secondaryServer.getHost(), secondaryServer.getPort(), e);
                return secondaryServer.getHost() + ":" + secondaryServer.getPort();
            } finally {
                channel.shutdown();
            }
        }
    }
}