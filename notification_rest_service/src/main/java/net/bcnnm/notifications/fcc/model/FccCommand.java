package net.bcnnm.notifications.fcc.model;

import net.bcnnm.notifications.model.CommandType;

public class FccCommand implements Payload{
    private final CommandType commandType;
    private final String details;

    public FccCommand(CommandType commandType, String details) {
        this.commandType = commandType;
        this.details = details;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getDetails() {
        return details;
    }
}
