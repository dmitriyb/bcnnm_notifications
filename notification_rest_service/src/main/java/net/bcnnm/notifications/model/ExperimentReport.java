package net.bcnnm.notifications.model;

import net.bcnnm.notifications.fcc.model.Payload;
import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackIgnore;
import net.bcnnm.notifications.slack.format.SlackName;
import org.springframework.data.annotation.Id;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExperimentReport implements Payload {
    //region Fields
    @Id
    @SlackIgnore
    private String id;

    @SlackBold
    @SlackName("Experiment ID")
    private String experimentId;

    @SlackBold
    @SlackName("Modeling time")
    private long modelingTime;

    @SlackBold
    @SlackName("Status")
    private TaskStatus status;

    @SlackName("Progress")
    private int progress;

    @SlackName("Info message")
    private List<String> info;

    //endregion


    //region Constructors

    public ExperimentReport(String id, String experimentId, long modelingTime, TaskStatus status, int progress, List<String> info) {
        this.id = id;
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

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public long getModelingTime() {
        return modelingTime;
    }

    public void setModelingTime(long modelingTime) {
        this.modelingTime = modelingTime;
    }

    public String getFormattedModelingTime() {
        Date mTime = new Date(modelingTime);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        return dateFormat.format(mTime);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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
                //", progress=" + progress +
                "info: \n" + info;
    }

    //endregion


    //region Private Methods

    //endregion

}
