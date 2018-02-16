package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackName;

import java.util.List;
import java.util.UUID;

public class FccStatus implements Payload{

    @SlackBold
    @SlackName("FCC ID")
    private final String fccId;

    @SlackName("Current alive agents IDs")
    private final List<UUID> agentsAlive;

    @SlackName("Current available experiments")
    private final List<UUID> experiments;

    public FccStatus(String fccId, List<UUID> agentsAlive, List<UUID> experiments) {
        this.fccId = fccId;
        this.agentsAlive = agentsAlive;
        this.experiments = experiments;
    }

    public String getFccId() {
        return fccId;
    }

    public List<UUID> getAgentsAlive() {
        return agentsAlive;
    }

    public List<UUID> getExperiments() {
        return experiments;
    }
}
