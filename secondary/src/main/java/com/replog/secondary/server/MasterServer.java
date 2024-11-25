package com.replog.secondary.server;

public class MasterServer {
    private String host;
    private int port;

    public MasterServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
