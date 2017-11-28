package net.bcnnm.notifications.fcc.model;

// TODO: to be replaced with Message class after integration
public abstract class Message {
    private final MessageType messageType;

    private final Payload payload;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.payload = null;
    }

    public Message(MessageType messageType, Payload payload) {
        this.messageType = messageType;
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Payload getPayload() {
        return payload;
    }
}
