package com.replog.master.controller;

import com.replog.common.model.Message;
import com.replog.master.discovery.SecondaryServerDiscovery;
import com.replog.master.model.SecondaryServer;
import com.replog.master.model.SecondaryServers;
import com.replog.master.model.SecondaryHealthStatus;
import com.replog.proto.MessageProto;
import com.replog.proto.MessageServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
        ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
        int taskCount = 0;
        for (SecondaryServer secondaryServer : secondaryServers.getServers()) {
            SendingThread sendingThread = new SendingThread(secondaryServer, new Message(message), masterID, masterTimestamp);
            completionService.submit(sendingThread);
            taskCount++;
        }

        List<String> failedServers = new ArrayList<>();
        if (writeConcern == 1) {
            // don't wait for any replication, make all asynchronous
            logger.info("Replication to secondaries initiated asynchronously");
        } else if (writeConcern == 2) {
            // wait for only one, fastest replication
            try {
                Future<String> future = completionService.take(); // waits for first completed task
                String result = future.get();
                if (result != null) {
                    failedServers.add(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error waiting for fastest replication", e);
            }
            logger.info("Replication to fastest secondary completed");
        } else if (writeConcern == 3) {
            // wait for all replications
            for (int i = 0; i < taskCount; i++) {
                try {
                    Future<String> future = completionService.take();
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
            int retryCount = 0;
            int maxRetries = 200; // adjust as needed
            while (retryCount < maxRetries) {
                SecondaryHealthStatus healthStatus = secondaryServer.getSecondaryHealthStatus();
                if (healthStatus == SecondaryHealthStatus.POOR) {
                    // wait until health improves
                    try {
                        logger.info("Secondary server at {}:{} is in POOR health. Waiting...",
                                secondaryServer.getHost(), secondaryServer.getPort());
                        Thread.sleep(500); // wait for 1 second before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Thread interrupted while waiting for secondary health to improve", e);
                        return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                    }
                    continue;
                }

                ManagedChannel channel = null;
                try {
                    channel = ManagedChannelBuilder.forAddress(secondaryServer.getHost(), secondaryServer.getPort())
                            .usePlaintext()
                            .build();

                    MessageServiceGrpc.MessageServiceBlockingStub stub = MessageServiceGrpc.newBlockingStub(channel);

                    // set a deadline
                    stub = stub.withDeadlineAfter(6, TimeUnit.SECONDS);

                    MessageProto.Message grpcMessage = MessageProto.Message.newBuilder()
                            .setMasterID(masterID)
                            .setMasterTimestamp(masterTimestamp)
                            .setMsgIDReal(message.getEndlessCounterState().getReal())
                            .setMsgIDImg(message.getEndlessCounterState().getImaginary())
                            .setContent(message.getContent())
                            .build();

                    MessageProto.Ack ack = stub.replicateMessage(grpcMessage);

                    if (ack.getSuccess()) {
                        logger.info("Received ACK from secondary at {}:{}",
                                secondaryServer.getHost(), secondaryServer.getPort());
                        return null; // Success
                    } else {
                        logger.warn("Secondary at {}:{} failed to replicate message",
                                secondaryServer.getHost(), secondaryServer.getPort());
                        // handle the failure based on health status
                        if (healthStatus == SecondaryHealthStatus.GOOD) {
                            retryCount++;
                            logger.info("Retrying to replicate message to secondary at {}:{} (attempt {}/{})",
                                    secondaryServer.getHost(), secondaryServer.getPort(), retryCount, maxRetries);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                logger.error("Thread interrupted during sleep between retries", ie);
                                return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                            }
                            continue;
                        } else {
                            return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                        }
                    }
                } catch (StatusRuntimeException e) {
                    logger.error("Status exception during replication to secondary at {}:{}",
                            secondaryServer.getHost(), secondaryServer.getPort(), e);
                    // interpret specific gRPC status codes if needed
                    if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                        logger.info("Simulated packet loss detected.");
                        continue;
                    } else {
                        // handle other exceptions
                        logger.error("Unhandled exception status: {}", e.getStatus());
                        return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                    }
                } catch (Exception e) {
                    logger.error("Error during replication to secondary at {}:{}",
                            secondaryServer.getHost(), secondaryServer.getPort(), e);
                    // handle other exceptions
                    return secondaryServer.getHost() + ":" + secondaryServer.getPort();
                } finally {
                    if (channel != null) {
                        channel.shutdown();
                    }
                }
            }
            // retries exhausted
            logger.warn("Retries exhausted for secondary at {}:{}",
                    secondaryServer.getHost(), secondaryServer.getPort());
            return secondaryServer.getHost() + ":" + secondaryServer.getPort();
        }
    }
}