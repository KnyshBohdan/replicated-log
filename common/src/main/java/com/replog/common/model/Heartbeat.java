package com.replog.common.model;

public class Heartbeat {
    private String slaveID;
    private long slaveTimestamp;
    private String slaveGRPCHost;
    private String status;

    public Heartbeat() {}

    public String getStatus() {return status;}
    public long getTimestamp() {return slaveTimestamp;}
    public String getSlaveGRPCHost() {return slaveGRPCHost;}
    public String getSlaveID() {return slaveID;}

    public void setSlaveID(String slaveID) {
        this.slaveID = slaveID;
    }

    public void setSlaveTimestamp(long slaveTimestamp) {
        this.slaveTimestamp = slaveTimestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSlaveGRPCHost(String slaveGRPCHost) {
        this.slaveGRPCHost = slaveGRPCHost;
    }
}
