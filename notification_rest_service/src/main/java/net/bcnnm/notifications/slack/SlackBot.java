package net.bcnnm.notifications.slack;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@Component
public class SlackBot extends Bot{
    private final String token;

    @Value("${reporting.service.url}")
    private String REPORTING_SERVICE_URL;

    public SlackBot() {
        token = System.getProperty("token");
    }

    @Override
    public String getSlackToken() {
        return token;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = {EventType.DIRECT_MENTION})
    public void onMention(WebSocketSession session, Event event) {
        String text = event.getText();
        String[] textSplit = text.split(" ", 3);

        Command command;
        try {
            command = Command.valueOf(textSplit[1].toUpperCase());
        }
        catch (IllegalArgumentException e) {
            reply(session, event, new Message(String.format("Unknown command: %s", textSplit[1])));
            return;
        }

        String params = textSplit[2];
        switch (command) {
            case REQUEST:
                String response = handleRequest(params);
                reply(session, event, new Message(response));
                break;
            default:
                reply(session, event, new Message(String.format("Unknown command: %s", command)));
        }
    }

    private String handleRequest(String taskId) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(REPORTING_SERVICE_URL).path("request").path(taskId);

        if (target.request().get().getStatus() == 200) {
            return "Request was successfully sent.";
        }
        else {
            return String.format("Error during requesting task: %s", target.request().get().getStatusInfo().toString());
        }
    }

}
