package com.replog.master.server;

import com.replog.master.controller.MessageController;
import com.replog.master.discovery.SecondaryServerDiscovery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {MessageController.class, SecondaryServerDiscovery.class})
public class MasterServer {

    public static void main(String[] args) {
        SpringApplication.run(MasterServer.class, args);
    }
}
