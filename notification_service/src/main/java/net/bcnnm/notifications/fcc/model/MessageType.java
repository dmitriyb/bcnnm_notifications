package net.bcnnm.notifications.fcc.model;

public enum MessageType {
    FCC_HELLO(8),
    FCC_AUTH(9),
    FCC_ASK(10),
    FCC_STATUS(11),
//    FCC_ADD_EXPERIMENT(12),
//    FCC_KILL(13),
    FCC_REPORT(14),
    FCC_COMMAND(15),
    FCC_ACKNOWLEDGE(16),
    FCC_AGENT_ASK(17),
    FCC_AGENT_INFO(18);

    private final int id;

    MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MessageType getByID(int id) {
        for (MessageType messageType : values()) {
            if (messageType.getId() == id)
                return messageType;
        }
        throw new IllegalArgumentException(String.format("Unknown ID: %s", id));
    }
}
