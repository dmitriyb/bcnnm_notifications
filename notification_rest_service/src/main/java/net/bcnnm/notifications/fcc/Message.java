package net.bcnnm.notifications.fcc;

// TODO: to be replaced with Message class after integration
public abstract class Message {
    private MessageType messageType;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
