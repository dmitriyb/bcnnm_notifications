package net.bcnnm.notifications.model;

import net.bcnnm.notifications.fcc.model.Payload;
import net.bcnnm.notifications.slack.format.SlackBold;
import net.bcnnm.notifications.slack.format.SlackFormatted;
import net.bcnnm.notifications.slack.format.SlackName;

@SlackFormatted
public class AgentInfo implements Payload{
    @SlackBold
    @SlackName("Agent ID")
    private String id;

    @SlackBold
    @SlackName("Agent IP")
    private String agentIp;

    @SlackBold
    @SlackName("Status")
    private AgentStatus agentStatus;

    @SlackName("Current tick")
    private Long currentTick;

    @SlackBold
    @SlackName("Current task")
    private TaskInfo currentTask;

    public AgentInfo(String id, String agentIp, AgentStatus agentStatus, Long currentTick, TaskInfo currentTask) {
        this.id = id;
        this.agentIp = agentIp;
        this.agentStatus = agentStatus;
        this.currentTick = currentTick;
        this.currentTask = currentTask;
    }

    public String getId() {
        return id;
    }

    public String getAgentIp() {
        return agentIp;
    }

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public Long getCurrentTick() {
        return currentTick;
    }

    public TaskInfo getCurrentTask() {
        return currentTask;
    }

    @Override
    public String toString() {
        return "AgentInfo{" +
                "id='" + id + '\'' +
                ", agentIp='" + agentIp + '\'' +
                ", agentStatus=" + agentStatus +
                ", currentTick=" + currentTick +
                ", currentTask=" + currentTask +
                '}';
    }
}
