package net.bcnnm.notifications.slack;

import javafx.util.Pair;
import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.File;
import me.ramswaroop.jbot.core.slack.models.Message;
import net.bcnnm.notifications.fcc.NotificationServer;
import net.bcnnm.notifications.fcc.model.FccStatus;
import net.bcnnm.notifications.model.AgentInfo;
import net.bcnnm.notifications.model.CommandType;
import net.bcnnm.notifications.model.ExperimentReport;
import net.bcnnm.notifications.model.Subtype;
import net.bcnnm.notifications.slack.format.SlackFormatter;
import net.bcnnm.notifications.slack.format.SlackFormatterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.bcnnm.notifications.calcs.ReportsAggregator.CURRENTLY_SUPPORTED_CALCS;

@Component
public class SlackBot extends Bot{
    private static final String SOME_ERROR_OCCURRED = "Some error occurred! See the logs for details.";
    private static final String REMOTE_COMMAND_WAS_SENT = "Remote command was sent.";
    private static final String GENERAL_HELP = "[INFO / CALC / ASK / REMOTE] %params% -- please use \"HELP %command%\" for command-specific help";
    private static final Map<String, String> COMMAND_HELP = new HashMap<String, String>() {
        {
            put("INFO", "INFO [AGENT/EXPERIMENT] %uuid% -- detailed info on entity by uuid");
            put("CALC", "CALC %function% %key% %prefix% -- aggregated values on %key% using %function% for experiments with %prefix%, " +
                    String.format("currently supported functions: %s", CURRENTLY_SUPPORTED_CALCS));
            put("ASK", "ASK [AGENT/EXPERIMENT] -- general info on entity");
            put("REMOTE", "REMOTE [AGENT SHUTDOWN / EXPERIMENT [START/STOP/PAUSE]] %uuid% -- send remote control command");
        }
    };

    private final String token;
    private final String defaultChannel;
    private WebSocketSession defaultSession;

    private Queue<Pair<WebSocketSession, Event>> askQueue;

    private final NotificationServer notificationServer;

    @Autowired
    public SlackBot(NotificationServer notificationServer) {
        token = System.getProperty("token");
        defaultChannel = System.getProperty("channel");
        this.askQueue = new ArrayBlockingQueue<>(1000);
        this.notificationServer = notificationServer;
    }

    @Override
    public String getSlackToken() {
        return token;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        super.afterConnectionEstablished(session);
        this.defaultSession = session;
    }

    @Controller(events = {EventType.FILE_SHARED})
    public void onUpload(WebSocketSession session, Event event) {
        File file = event.getFile();
        notificationServer.uploadFile(file);

        reply(session, event, new Message("File was sent to FCC"));
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
            reply(session, event, new Message(GENERAL_HELP));
            return;
        }

        String params = textSplit.length >= 3 ? textSplit[2] : "";
        try {
            switch (command) {
                case INFO:
                    handleInfo(params, session, event);
                    break;
                case CALC:
                    handleCalc(params, session, event);
                    break;
                case ASK:
                    handleAsk(params, session, event);
                    break;
                case REMOTE:
                    handleRemote(params, session, event);
                    break;
                case NS:
                    handleNs(params, session, event);
                    break;
                case HELP:
                    handleHelp(params, session, event);
                    break;
                default:
                    reply(session, event, new Message(String.format("Unknown command: %s", command)));
                    reply(session, event, new Message(COMMAND_HELP.get(command.name())));
            }
        } catch (Exception e) {
            // todo: log properly
            e.printStackTrace();

            reply(session, event, new Message("Some error occurred. Please try again."));
            reply(session, event, new Message(COMMAND_HELP.get(command.name())));
        }
    }

    private void handleHelp(String paramsString, WebSocketSession session, Event event) {
        if (paramsString.isEmpty()) {
            reply(session, event, new Message(GENERAL_HELP));
        }
        else {
            Command command = Command.valueOf(paramsString);

            String specificHelp = COMMAND_HELP.get(command.name());
            if (specificHelp != null) {
                reply(session, event, new Message(specificHelp));
            } else {
                reply(session, event, new Message(GENERAL_HELP));
            }
        }
    }

    private void handleRemote(String paramsString, WebSocketSession session, Event event) {
        String[] params = paramsString.split(" ", 3);
        Subtype subtype = Subtype.valueOf(params[0]);

        CommandType commandType;

        switch (subtype) {
            case EXPERIMENT:
                commandType = CommandType.valueOf(params[1]);
                String experimentId = params[2];

                switch (commandType) {
                    case START:
                        notificationServer.startExperiment(experimentId);
                        reply(session, event, new Message(REMOTE_COMMAND_WAS_SENT));
                        break;
                    case STOP:
                        notificationServer.stopExperiment(experimentId);
                        reply(session, event, new Message(REMOTE_COMMAND_WAS_SENT));
                        break;
                    case PAUSE:
                        notificationServer.pauseExperiment(experimentId);
                        reply(session, event, new Message(REMOTE_COMMAND_WAS_SENT));
                        break;
                    default:
                        String response = String.format("Unknown command type: %s", commandType);
                        System.out.println(response);
                        reply(session, event, new Message(response));
                        break;
                }

                break;
            case AGENT:
                commandType = CommandType.valueOf(params[1]);
                String agentId = params[2];

                switch (commandType) {
                    case SHUTDOWN:
                        notificationServer.shutdownAgent(agentId);
                        break;
                    default:
                        String response = String.format("Unknown command type: %s", commandType);
                        System.out.println(response);
                        reply(session, event, new Message(response));
                        break;
                }

                break;
        }
    }

    private void handleAsk(String params, WebSocketSession session, Event event) {
        askQueue.add(new Pair<>(session, event));
        String[] split = params.split(" ", 2);
        Subtype subtype = Subtype.valueOf(split[0]);
        switch (subtype) {
            case AGENT:
                notificationServer.askFccAgentsStatus();
                break;
            case EXPERIMENT:
                notificationServer.askFccExperimentsStatus();
                break;
        }
        reply(session, event, new Message("Asked FCC for status"));
    }

    private void handleNs(String params, WebSocketSession session, Event event) {
        ServerSocketChannel serverSocketChannel = notificationServer.getServerSocketChannel();
        String localIP = serverSocketChannel.socket().toString();
        String remoteIP = serverSocketChannel.socket().getInetAddress().toString();
        String port = String.valueOf(serverSocketChannel.socket().getLocalPort());
        reply(session, event, new Message("NS data: local IP: " + localIP + " remoteIP: " + remoteIP + " port: " + port));
    }

    private void handleCalc(String paramsString, WebSocketSession session, Event event) {

        Matcher matcher = Pattern.compile("\\(?([a-zA-Z,\\s]*)\\)? (.*) (.*)").matcher(paramsString);

        matcher.matches();
        String[] functions = matcher.group(1).split(",\\s*");
        String key = matcher.group(2);
        String prefix = matcher.group(3);

        ArrayList<String> calculations = new ArrayList<>();
        for (String function : functions) {
            String response = notificationServer.calculate(function, key, prefix);
            reply(session, event, new Message(response));
        }
    }

    private void handleInfo(String paramsString, WebSocketSession session, Event event) {
        String[] params = paramsString.split(" ", 2);
        Subtype subtype = Subtype.valueOf(params[0]);
        String uuid = params[1];

        try {
            String response = null;
            switch (subtype) {
                case AGENT:
                    askQueue.add(new Pair<>(session, event));
                    notificationServer.askAgentReport(uuid);

                    response = String.format("Asked FCC for agent id=%s info", uuid);
                    break;
                case EXPERIMENT:
                    ExperimentReport experimentReport = notificationServer.getExperimentReport(uuid);

                    response = SlackFormatter.format(experimentReport);
                    break;
            }

            reply(session, event, new Message(response));
        } catch (SlackFormatterException e) {
            e.printStackTrace();
            reply(session, event, new Message(SOME_ERROR_OCCURRED));
        }
    }

    public void replyWithStatus(FccStatus fccStatus) {
        Pair<WebSocketSession, Event> asked = askQueue.poll();
        WebSocketSession session = asked.getKey();
        Event event = asked.getValue();

        try {
            reply(session, event, new Message(SlackFormatter.format(fccStatus)));
        } catch (SlackFormatterException e) {
            e.printStackTrace();
            reply(session, event, new Message(SOME_ERROR_OCCURRED));
        }
    }

    public void replyWithAgentInfo(AgentInfo agentInfo) {
        Pair<WebSocketSession, Event> asked = askQueue.poll();
        WebSocketSession session = asked.getKey();
        Event event = asked.getValue();

        try {
            reply(session, event, new Message(SlackFormatter.format(agentInfo)));
        } catch (SlackFormatterException e) {
            e.printStackTrace();
            reply(session, event, new Message(SOME_ERROR_OCCURRED));
        }
    }

    public void sendToDefaultChannel(Object obj) {
        try {
            Message message = new Message(SlackFormatter.format(obj));
            message.setType(EventType.MESSAGE.name().toLowerCase());
            message.setChannel(defaultChannel);
            defaultSession.sendMessage(new TextMessage(message.toJSONString()));
        } catch (SlackFormatterException | IOException e) {
            e.printStackTrace();
        }
    }
}
