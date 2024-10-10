package com.replog.master.discovery;

import com.replog.master.model.SecondaryServer;
import com.replog.master.model.SecondaryServers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class SecondaryServerDiscovery {

    @Value("${secondary.hosts}")
    private String[] secondaryHosts;

    @Value("${secondary.ports}")
    private int[] secondaryPorts;

    private SecondaryServers secondaryServers;

    @PostConstruct
    public void init() {
        List<SecondaryServer> servers = new ArrayList<>();
        for (int i = 0; i < secondaryHosts.length; i++) {
            String host = secondaryHosts[i];
            int port = secondaryPorts[i];
            servers.add(new SecondaryServer(host, port));
        }
        secondaryServers = new SecondaryServers(servers);
    }

    public SecondaryServers getSecondaryServers() {
        return secondaryServers;
    }
}
