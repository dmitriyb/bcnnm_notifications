package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.AgentReport;

public class FccReportMessage extends Message {
    public FccReportMessage(AgentReport report) {
        super(MessageType.FCC_REPORT, report);
    }

    @Override
    public AgentReport getPayload() {
        return (AgentReport) super.getPayload();
    }
}
