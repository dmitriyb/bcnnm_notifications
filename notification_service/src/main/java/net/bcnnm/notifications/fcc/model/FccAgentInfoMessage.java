package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.AgentInfo;

public class FccAgentInfoMessage extends Message {
    public FccAgentInfoMessage(AgentInfo agentInfo) {
        super(MessageType.FCC_AGENT_INFO, agentInfo);
    }

    @Override
    public AgentInfo getPayload() {
        return (AgentInfo) super.getPayload();
    }
}
