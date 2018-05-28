package net.bcnnm.notifications.fcc.model;

public class FccAgentAskMessage extends Message {
    public FccAgentAskMessage(String agentId) {
        super(MessageType.FCC_AGENT_ASK, new AgentId(agentId));
    }
}
