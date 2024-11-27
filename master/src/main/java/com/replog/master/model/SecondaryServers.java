package com.replog.master.model;

import com.replog.master.model.SecondaryHealthStatus;
import java.util.List;
import java.util.Map;

public class SecondaryServers {
    private List<SecondaryServer> servers;

    public SecondaryServers(List<SecondaryServer> servers) {
        this.servers = servers;
    }

    public void updateSecondaryHealthStatuses(Map<String, SecondaryHealthStatus> statuses) {
        for (SecondaryServer server : servers) {
            boolean isHealthSet = false;
            for (Map.Entry<String, SecondaryHealthStatus> entry : statuses.entrySet()) {
                String host = entry.getKey();
                SecondaryHealthStatus healthStatus = entry.getValue();

                if (server.getHost().equals(host)) {
                    server.setSecondaryHealthStatus(healthStatus);
                    isHealthSet = true;
                    break;
                }
            }

            if (!isHealthSet) {
                server.setSecondaryHealthStatus(SecondaryHealthStatus.POOR);
            }
        }
    }

    public List<SecondaryServer> getServers() {
        return servers;
    }
}
