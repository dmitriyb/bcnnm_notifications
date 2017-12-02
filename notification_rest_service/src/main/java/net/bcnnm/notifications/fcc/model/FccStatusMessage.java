package net.bcnnm.notifications.fcc.model;

public class FccStatusMessage extends Message {

    public FccStatusMessage(FccStatus fccStatus) {
        super(MessageType.FCC_STATUS, fccStatus);
    }

    @Override
    public FccStatus getPayload() {
        return (FccStatus) super.getPayload();
    }
}
