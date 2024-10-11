package com.replog.master.model;

import java.util.List;

public class SecondaryServers {
    private List<SecondaryServer> servers;

    public SecondaryServers(List<SecondaryServer> servers) {
        this.servers = servers;
    }

    public List<SecondaryServer> getServers() {
        return servers;
    }
}
