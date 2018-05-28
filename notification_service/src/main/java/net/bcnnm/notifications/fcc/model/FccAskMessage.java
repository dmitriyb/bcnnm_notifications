package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.Subtype;

public class FccAskMessage extends Message {
    public FccAskMessage(Subtype subtype) {
        super(MessageType.FCC_ASK, subtype);
    }

    @Override
    public Subtype getPayload() {
        return (Subtype) super.getPayload();
    }
}
