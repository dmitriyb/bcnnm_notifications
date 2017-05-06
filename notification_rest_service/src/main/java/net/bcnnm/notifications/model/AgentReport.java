package net.bcnnm.notifications.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackIgnore;
import net.bcnnm.notifications.slack.format.SlackName;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class AgentReport {
    @Id
    @SlackIgnore
    private String id;

    @SlackBold
    @SlackName("Task ID")
    private String taskId;

    @SlackBold
    @SlackName("Agent IP")
    private String agentIp;

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

    public AgentReport(String taskId, String agentIp, Date timestamp, TaskStatus status, int progress, List<String> info) {
        this.taskId = taskId;
        this.agentIp = agentIp;
        this.timestamp = timestamp;
        this.status = status;
        this.progress = progress;
        this.info = info;
    }

    public AgentReport(){

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAgentIp() {
        return agentIp;
    }

    public void setAgentIp(String agentIp) {
        this.agentIp = agentIp;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "AgentReport{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", agentIp='" + agentIp + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", progress=" + progress +
                ", info='" + info + '\'' +
                '}';
    }
}
