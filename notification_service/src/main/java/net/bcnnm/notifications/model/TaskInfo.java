package net.bcnnm.notifications.model;

public class TaskInfo {
    private final String experimentId;

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
