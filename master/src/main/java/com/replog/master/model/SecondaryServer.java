package com.replog.master.model;

import com.replog.master.model.SecondaryHealthStatus;

public class SecondaryServer {
    private SecondaryHealthStatus secondaryHealthStatus;
    private String host;
    private int port;

    public SecondaryServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.secondaryHealthStatus = SecondaryHealthStatus.POOR;
    }

    public void setSecondaryHealthStatus(SecondaryHealthStatus secondaryHealthStatus) {
        this.secondaryHealthStatus = secondaryHealthStatus;
    }

    public SecondaryHealthStatus getSecondaryHealthStatus() {
        return secondaryHealthStatus;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
