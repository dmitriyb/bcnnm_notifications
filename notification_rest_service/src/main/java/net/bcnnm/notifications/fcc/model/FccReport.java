package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FccReport implements Payload {
    //region Fields
    @SlackBold
    @SlackName("FCC ID")
    private final String fccId;

    @SlackName("Experiment ID")
    private final UUID experimentId;

    @SlackName("Timestamp")
    private final Date timestamp;

    @SlackName("Experiment results")
    private final List<String> info;


    //endregion


    //region Constructors
    public FccReport(String fccId, UUID experimentId, Date timestamp, List<String> info) {
        this.fccId = fccId;
        this.experimentId = experimentId;
        this.timestamp = timestamp;
        this.info = info;
    }
    //endregion


    //region Getters and Setters

    public String getFccId() {
        return fccId;
    }

    public UUID getExperimentId() {
        return experimentId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getInfo() {
        return info;
    }

    //endregion


    //region Public Methods

    //endregion


    //region Private Methods

    //endregion

}
