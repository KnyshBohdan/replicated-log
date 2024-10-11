package com.replog.secondary;

import com.replog.secondary.server.SecondaryServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Autowired
    private SecondaryServer secondaryServer;

    @Value("${grpc.port:50051}")
    private int grpcPort;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public void startSecondaryServer() throws Exception {
        new Thread(() -> {
            try {
                secondaryServer.start(grpcPort);
                secondaryServer.blockUntilShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
