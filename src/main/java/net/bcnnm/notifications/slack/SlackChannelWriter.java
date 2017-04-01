package net.bcnnm.notifications.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackChannelWriter {
    private final String USERNAME = "model-notifier";
    private final String TOKEN = "xoxb-29794513911-Gbu0b8usJxQmM0Di56xkl2It";

    private SlackWebApiClient slackClient;
    private final String channel;

    public SlackChannelWriter(String channel) {
        this.channel = channel;
        slackClient = SlackClientFactory.createWebApiClient(TOKEN);
        slackClient.auth();
    }

    public String writeMessage(String message) {
         return slackClient.postMessage(channel, message, USERNAME, true);
    }
}
