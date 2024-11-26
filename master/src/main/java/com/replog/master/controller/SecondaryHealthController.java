package com.replog.master.controller;

import com.replog.master.model.SecondaryHealthStatus;
import com.replog.common.model.Heartbeat;

import java.util.HashMap;
import java.util.Map;

public class SecondaryHealthController {
    private Map<String, SecondaryHealthStatus> healthStatuses;
    private Map<String, Long> lastHeartbeatTimestamps;

    public SecondaryHealthController() {
        healthStatuses = new HashMap<>();
        lastHeartbeatTimestamps = new HashMap<>();
    }

    public void process(Heartbeat heartbeat) {
        String slaveGRPCHost = heartbeat.getSlaveGRPCHost();
        long currentTimestamp = System.currentTimeMillis();

        if (!healthStatuses.containsKey(slaveGRPCHost)) {
            healthStatuses.put(slaveGRPCHost, SecondaryHealthStatus.GOOD);
        }

        lastHeartbeatTimestamps.put(slaveGRPCHost, currentTimestamp);
    }

    public Map<String, SecondaryHealthStatus> getHealthStatuses() {
        long currentTimestamp = System.currentTimeMillis();

        for (Map.Entry<String, Long> entry : lastHeartbeatTimestamps.entrySet()) {
            String slaveGRPCHost = entry.getKey();
            long lastHeartbeatTimestamp = entry.getValue();
            long timeSinceLastHeartbeat = currentTimestamp - lastHeartbeatTimestamp;

            if (timeSinceLastHeartbeat >= 2000) {
                healthStatuses.put(slaveGRPCHost, SecondaryHealthStatus.POOR);
            } else if (timeSinceLastHeartbeat >= 500) {
                healthStatuses.put(slaveGRPCHost, SecondaryHealthStatus.FAIR);
            } else {
                healthStatuses.put(slaveGRPCHost, SecondaryHealthStatus.GOOD);
            }
        }

        return healthStatuses;
    }
}
