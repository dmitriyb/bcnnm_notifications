package net.bcnnm.notifications.fcc.model;

public class AgentId implements Payload {
    private String id;

    public AgentId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
