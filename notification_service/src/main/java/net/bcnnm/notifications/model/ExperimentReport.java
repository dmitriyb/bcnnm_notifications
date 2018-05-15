package net.bcnnm.notifications.model;

import net.bcnnm.notifications.fcc.model.Payload;
import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackFormatted;
import net.bcnnm.notifications.slack.format.SlackIgnore;
import net.bcnnm.notifications.slack.format.SlackName;
import org.springframework.data.annotation.Id;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SlackFormatted
public class ExperimentReport implements Payload {
    //region Fields
    @Id
    @SlackIgnore
    private String id;

    @SlackBold
    @SlackName("FCC ID")
    private final String fccId;

    @SlackBold
    @SlackName("Experiment ID")
    private final String experimentId;

    @SlackBold
    @SlackName("Modeling time")
    private long modelingTime;

    @SlackBold
    @SlackName("Status")
    private final TaskStatus status;

    @SlackName("Progress")
    private int progress;

    @SlackName("Info message")
    private final List<String> info;

    //endregion


    //region Constructors

    public ExperimentReport(String fccId, String experimentId, long modelingTime, TaskStatus status, int progress, List<String> info) {
        this.fccId = fccId;
        this.experimentId = experimentId;
        this.modelingTime = modelingTime;
        this.status = status;
        this.progress = progress;
        this.info = info;
    }

    //endregion


    //region Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFccId() {
        return fccId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public long getModelingTime() {
        return modelingTime;
    }

    private String getFormattedModelingTime() {
        Date mTime = new Date(modelingTime);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        return dateFormat.format(mTime);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getProgress() {
        return progress;
    }

    public List<String> getInfo() {
        return info;
    }

    //endregion


    //region Public Methods

    @Override
    public String toString() {
        return "Experiment Report: " + id + "\n" +
                "modelingTime=" + getFormattedModelingTime() +
                ", status=" + status + "\n" +
                "info: \n" + info;
    }

    //endregion


    //region Private Methods

    //endregion

}
