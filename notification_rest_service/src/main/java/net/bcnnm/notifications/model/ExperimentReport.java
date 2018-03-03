package net.bcnnm.notifications.model;

import com.synstorm.common.Utils.EvolutionUtils.Score.ScoreTable;
import net.bcnnm.notifications.fcc.model.Payload;
import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackIgnore;
import net.bcnnm.notifications.slack.format.SlackName;
import org.springframework.data.annotation.Id;

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
    @SlackName("Timestamp")
    private Date timestamp;

    @SlackBold
    @SlackName("Status")
    private TaskStatus status;

    @SlackName("Progress")
    private int progress;

    @SlackName("Info message")
    private List<String> info;

    //endregion


    //region Constructors

    public ExperimentReport(String id, String experimentId, Date timestamp, TaskStatus status, int progress, List<String> info) {
        this.id = id;
        this.experimentId = experimentId;
        this.timestamp = timestamp;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
                "timestamp=" + timestamp +
                ", status=" + status + "\n" +
                //", progress=" + progress +
                "info: \n" + info;
    }

    //endregion


    //region Private Methods

    //endregion

}
