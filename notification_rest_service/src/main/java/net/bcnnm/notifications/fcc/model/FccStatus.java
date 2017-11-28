package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackName;

import java.util.List;

public class FccStatus implements Payload{

    @SlackBold
    @SlackName("FCC ID")
    private final String fccId;

    @SlackName("Current alive agents IDs")
    private final List<String> agentsAlive;

    public FccStatus(String fccId, List<String> agentsAlive) {
        this.fccId = fccId;
        this.agentsAlive = agentsAlive;
    }

    public String getFccId() {
        return fccId;
    }

    public List<String> getAgentsAlive() {
        return agentsAlive;
    }


}
