package com.replog.secondary;

import com.replog.secondary.health.HealthSender;
import com.replog.secondary.server.SecondaryServer;
import com.replog.secondary.config.SecondaryServerConfig;
import com.replog.secondary.processor.SecondaryMessageProcessor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackageClasses = {SecondaryServer.class, HealthSender.class,
        SecondaryServerConfig.class, SecondaryMessageProcessor.class})
public class Application {
    @Autowired
    private SecondaryServer secondaryServer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public void startSecondaryServer() throws Exception {
        new Thread(() -> {
            try {
                secondaryServer.start();
                secondaryServer.blockUntilShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
