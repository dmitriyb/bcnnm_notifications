package net.bcnnm.notifications.fcc.model;

public class FccReportMessage extends Message {
    public FccReportMessage(FccReport report) {
        super(MessageType.FCC_REPORT, report);
    }

    @Override
    public FccReport getPayload() {
        return (FccReport) super.getPayload();
    }
}
