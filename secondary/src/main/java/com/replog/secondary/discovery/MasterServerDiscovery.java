package com.replog.secondary.discovery;

import com.replog.secondary.server.MasterServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Component
public class MasterServerDiscovery {
    @Value("${master.host}")
    private String masterHost;

    @Value("${master.port}")
    private int masterPort;

    private MasterServer masterServer;

    @PostConstruct
    public void init() {
        masterServer = new MasterServer(masterHost, masterPort);
    }

    public MasterServer getMasterServer() {
        return masterServer;
    }
}
