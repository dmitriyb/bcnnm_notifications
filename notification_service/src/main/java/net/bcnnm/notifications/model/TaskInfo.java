package net.bcnnm.notifications.model;

import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackFormatted;

@SlackFormatted
public class TaskInfo {
    private final String experimentId;

    @SlackBold
    private final String taskId;

    private final TaskStatus taskStatus;

    public TaskInfo(String experimentId, String taskId, TaskStatus taskStatus) {
        this.experimentId = experimentId;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
}
