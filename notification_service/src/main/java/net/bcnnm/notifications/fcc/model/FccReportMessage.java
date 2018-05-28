package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.ExperimentReport;

public class FccReportMessage extends Message {
    public FccReportMessage(ExperimentReport report) {
        super(MessageType.FCC_REPORT, report);
    }

    @Override
    public ExperimentReport getPayload() {
        return (ExperimentReport) super.getPayload();
    }
}
