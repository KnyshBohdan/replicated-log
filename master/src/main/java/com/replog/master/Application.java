package com.replog.master;

import com.replog.master.server.MasterServer;
import org.springframework.boot.SpringApplication;


public class Application {
    public static void main(String[] args) {
        SpringApplication.run(MasterServer.class, args);
    }
}
