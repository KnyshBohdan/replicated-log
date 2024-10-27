package com.replog.secondary.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class SecondaryServerConfig {

    private String slaveID;

    @PostConstruct
    public void init() {
        this.slaveID = generateUniqueID();
    }

    private String generateUniqueID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    public String getSlaveID() {
        return slaveID;
    }
}
