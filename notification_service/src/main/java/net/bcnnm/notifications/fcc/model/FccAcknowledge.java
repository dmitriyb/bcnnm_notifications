package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackName;

public class FccAcknowledge implements Payload{
    public enum Status {
        OK,
        FAILED
    }

    @SlackBold
    @SlackName("Acknowledge status")
    private final Status status;

    @SlackBold
    @SlackName("Additional details")
    private final String details;

    public FccAcknowledge(Status status, String details) {
        this.status = status;
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}
