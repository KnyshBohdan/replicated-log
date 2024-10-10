package com.replog.master.model;

public class SecondaryServer {
    private String host;
    private int port;

    public SecondaryServer(String host, int port) {
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
