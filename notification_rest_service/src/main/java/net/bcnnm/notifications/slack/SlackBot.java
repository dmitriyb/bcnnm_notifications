package net.bcnnm.notifications.slack;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import net.bcnnm.notifications.AgentReportDao;
import net.bcnnm.notifications.fcc.NotificationServer;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.slack.format.SlackFormatter;
import net.bcnnm.notifications.slack.format.SlackFormatterException;
import net.bcnnm.notifications.stats.AggregationException;
import net.bcnnm.notifications.stats.ReportsAggregator;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SlackBot extends Bot{
    private final AgentReportDao reportDao;

    @Autowired
    private List<ReportsAggregator> reportsAggregators;

    @Autowired
    private NotificationServer notificationServer;

    private final String token;

    public SlackBot(AgentReportDao reportDao) {
        this.reportDao = reportDao;
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
            case STAT:
                response = handleStat(params);
                reply(session, event, new Message(response));
                break;
            case ASK:
                notificationServer.askFccForStatus(session, event);
//                reply(session, event, new Message("Asked FCC for status"));
                break;
            default:
                reply(session, event, new Message(String.format("Unknown command: %s", command)));
        }
    }

    private String handleStat(String paramsString) {
        String[] params = paramsString.split(" ", 4);
        String function = params[0];
        String key = params[1];
        String prefix = params[2];

        List<AgentReport> filteredReports = getFilteredByTaskPrefix(reportDao.getReportList(), prefix);

        for (ReportsAggregator reportsAggregator : reportsAggregators) {
            if (reportsAggregator.getName().equals(function)) {
                try {
                    return reportsAggregator.aggregate(filteredReports, key);
                } catch (AggregationException e) {
                    return String.format("Unable to aggregate by specified key: %s", key);
                }
            }
        }

        return String.format("Unknown function for aggregation: %s", function);
    }

    private List<AgentReport> getFilteredByTaskPrefix(List<AgentReport> reportList, String prefix) {
        return reportList.stream()
                .filter(report -> report.getTaskId().startsWith(prefix))
                .collect(Collectors.toList());
    }

    private String handleRequest(String taskId) {
        AgentReport lastReport = reportDao.getReport(taskId);
        if (lastReport == null) {
            return String.format("Agent host is unknown! No data found in database for task: %s", taskId);
        }
        String agentHost = lastReport.getAgentIp();

        UriBuilder builder = new JerseyUriBuilder();
        URI uri = builder.scheme("http").host(agentHost).port(8080).path("request").path(taskId).build();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);

        if (target.request().get().getStatus() == 200) {
            return "Request was successfully sent.";
        }
        else {
            return String.format("Error during requesting task: %s", target.request().get().getStatusInfo().toString());
        }
    }

    public void replyWithObject(WebSocketSession session, Event event, Object replyObject) {
        try {
            reply(session, event, new Message(SlackFormatter.format(replyObject, replyObject.getClass())));
        } catch (SlackFormatterException e) {
            System.out.println("Failed to reply with object");
            e.printStackTrace();
        }
    }
}
