package net.bcnnm.notifications.fcc.model;

public class FccAcknowledgeMessage extends Message{

    public FccAcknowledgeMessage(FccAcknowledge acknowledge) {
        super(MessageType.FCC_ACKNOWLEDGE, acknowledge);
    }

    @Override
    public FccAcknowledge getPayload() {
        return (FccAcknowledge) super.getPayload();
    }
}
