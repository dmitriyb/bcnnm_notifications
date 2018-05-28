package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.Subtype;
import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackName;

import java.util.Map;
import java.util.UUID;

public class FccStatus implements Payload {
    @SlackBold
    @SlackName("FCC ID")
    private final String fccId;

    @SlackName("Subtype")
    private final Subtype subtype;

    @SlackName("Entity info")
    private final Map<UUID, String> entityInfo;

    public FccStatus(String fccId, Subtype subtype, Map<UUID, String> entityInfo) {
        this.fccId = fccId;
        this.subtype = subtype;
        this.entityInfo = entityInfo;
    }

    public String getFccId() {
        return fccId;
    }

    public Subtype getSubtype() {
        return subtype;
    }

    public Map<UUID, String> getEntityInfo() {
        return entityInfo;
    }
}
