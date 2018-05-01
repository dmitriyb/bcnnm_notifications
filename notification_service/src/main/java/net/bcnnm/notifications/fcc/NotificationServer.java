package net.bcnnm.notifications.fcc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import me.ramswaroop.jbot.core.slack.models.File;
import net.bcnnm.notifications.AgentReportDao;
import net.bcnnm.notifications.fcc.model.FccAcknowledge;
import net.bcnnm.notifications.fcc.model.FccAcknowledgeMessage;
import net.bcnnm.notifications.fcc.model.FccAskMessage;
import net.bcnnm.notifications.fcc.model.FccAuthMessage;
import net.bcnnm.notifications.fcc.model.FccCommand;
import net.bcnnm.notifications.fcc.model.FccCommandMessage;
import net.bcnnm.notifications.fcc.model.FccReportMessage;
import net.bcnnm.notifications.fcc.model.FccStatusMessage;
import net.bcnnm.notifications.fcc.model.Message;
import net.bcnnm.notifications.model.CommandType;
import net.bcnnm.notifications.model.ExperimentReport;
import net.bcnnm.notifications.model.Subtype;
import net.bcnnm.notifications.slack.SlackBot;
import net.bcnnm.notifications.stats.AggregationException;
import net.bcnnm.notifications.stats.ReportsAggregator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static net.bcnnm.notifications.fcc.ProtocolCommunication.readFromSocket;
import static net.bcnnm.notifications.fcc.ProtocolCommunication.writeToSocket;

@Component
public class NotificationServer {
    private final AgentReportDao reportDao;
    private final List<ReportsAggregator> reportsAggregators;
    @Value("${hostname}")
    private String hostname;


    @Autowired
    private SlackBot slackBot;
    private SocketChannel fccSocketChannel;
    private ServerSocketChannel serverSocketChannel;

    @Autowired
    public NotificationServer(AgentReportDao reportDao, List<ReportsAggregator> reportsAggregators) {
        this.reportDao = reportDao;
        this.reportsAggregators = reportsAggregators;
    }

    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();

            final int serverPort = 9001;
            serverSocketChannel.bind(new InetSocketAddress(hostname, serverPort));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Started server..");

            while (true) {
                try {
                    selector.select();
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = (SelectionKey) iterator.next();
                        iterator.remove();

                        if (selectionKey.isAcceptable()) {
                            fccSocketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                            fccSocketChannel.configureBlocking(false);
                            fccSocketChannel.register(selector, SelectionKey.OP_READ);

                            System.out.println("Connection accepted..");
                        }
                        if (selectionKey.isReadable()) {
                            handleIncoming(selectionKey);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Autowired
    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    private void handleIncoming(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] message = readFromSocket(socketChannel);
        if (message.length == 0) {
            System.out.println("Client unexpectedly disconnected..");
            return;
        }
        Message incomingMessage = Encoder.decode(message);
        System.out.println("Received incoming message: " + incomingMessage.getMessageType());

        switch (incomingMessage.getMessageType()) {
            case FCC_HELLO:
                System.out.println("Responding with AUTH message..");

                writeToSocket(socketChannel, Encoder.encode(new FccAuthMessage()));
                break;
            case FCC_STATUS:
                System.out.println("Received status..");
                FccStatusMessage fccStatusMessage = (FccStatusMessage) incomingMessage;

                slackBot.replyWithStatus(fccStatusMessage.getPayload());
                break;
            case FCC_REPORT:
                System.out.println("Received report..");
                FccReportMessage fccReportMessage = (FccReportMessage) incomingMessage;
                ExperimentReport receivedReport = fccReportMessage.getPayload();

                reportDao.saveReport(receivedReport);
                slackBot.sendToDefaultChannel(receivedReport);
                break;
            case FCC_ACKNOWLEDGE:
                System.out.println("Recieved acknowledge..");
                FccAcknowledgeMessage fccAcknowledgeMessage = (FccAcknowledgeMessage) incomingMessage;
                FccAcknowledge acknowledge = fccAcknowledgeMessage.getPayload();

                slackBot.sendToDefaultChannel(acknowledge);
                break;
            default:
                break;
        }

    }

    public void askFccAgentsStatus() {
        System.out.println("Asking FCC for agents status..");
        writeToSocket(fccSocketChannel, Encoder.encode(new FccAskMessage(Subtype.AGENT)));
    }

    public void askFccExperimentsStatus() {
        System.out.println("Asking FCC for experiments status..");
        writeToSocket(fccSocketChannel, Encoder.encode(new FccAskMessage(Subtype.EXPERIMENT)));
    }

    public String getStat(String function, String key, String prefix) {
        List<ExperimentReport> filteredReports = getFilteredByExperimentIdPrefix(reportDao.getReportList(), prefix);

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

    private List<ExperimentReport> getFilteredByExperimentIdPrefix(List<ExperimentReport> reportList, String prefix) {
        return reportList.stream()
                .filter(report -> report.getExperimentId().startsWith(prefix))
                .collect(Collectors.toList());
    }

    public ExperimentReport getReport(String experimentId) {
        return reportDao.getReport(experimentId);
    }

    public void startExperiment(String experimentId) {
        System.out.println("Sending START command..");
        FccCommand startCommand = new FccCommand(CommandType.START, experimentId.getBytes());
        writeToSocket(fccSocketChannel, Encoder.encode(new FccCommandMessage(startCommand)));
    }

    public void stopExperiment(String experimentId) {
        System.out.println("Sending STOP command..");
        FccCommand stopCommand = new FccCommand(CommandType.STOP, experimentId.getBytes());
        writeToSocket(fccSocketChannel, Encoder.encode(new FccCommandMessage(stopCommand)));
    }

    public void uploadFile(File file) {
        try {
            System.out.println("Sending UPLOAD command..");

            byte[] fileBytes = fetchFile(file);
            FccCommand uploadCommand = new FccCommand(CommandType.UPLOAD, fileBytes);

            writeToSocket(fccSocketChannel,Encoder.encode(new FccCommandMessage(uploadCommand)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] fetchFile(File file) throws IOException {
        Client client = Client.create();
        WebResource resource = client.resource(file.getUrlPrivate());
        ClientResponse response = resource
                .header("Authorization", String.format("Bearer %s", slackBot.getSlackToken()))
                .get(ClientResponse.class);

        InputStream is = response.getEntity(InputStream.class);

        return IOUtils.toByteArray(is);
    }
}
