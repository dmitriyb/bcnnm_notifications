package net.bcnnm.notifications.fcc.model;


public class FccCommandMessage extends Message {

    public FccCommandMessage(FccCommand command) {
        super(MessageType.FCC_COMMAND, command);
    }

    @Override
    public FccCommand getPayload() {
        return (FccCommand) super.getPayload();
    }
}
