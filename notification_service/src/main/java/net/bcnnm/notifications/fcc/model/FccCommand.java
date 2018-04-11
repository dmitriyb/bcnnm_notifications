package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.CommandType;

public class FccCommand implements Payload{
    private final CommandType commandType;
    private final byte[] details;

    public FccCommand(CommandType commandType, byte[] details) {
        this.commandType = commandType;
        this.details = details;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public byte[] getDetails() {
        return details;
    }
}