package net.bcnnm.notifications.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackChannelWriter {
    private final String USERNAME = "test-notifier";

    private SlackWebApiClient slackClient;
    private final String channel;

    public SlackChannelWriter(String channel) {
        this.channel = channel;
        String token = System.getProperty("token");
        slackClient = SlackClientFactory.createWebApiClient(token);
        slackClient.auth();
    }

    public String writeMessage(String message) {
         return slackClient.postMessage(channel, message, USERNAME, true);
    }
}
