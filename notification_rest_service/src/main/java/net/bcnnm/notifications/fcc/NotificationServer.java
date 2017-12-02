package net.bcnnm.notifications.fcc;

import net.bcnnm.notifications.AgentReportDao;
import net.bcnnm.notifications.fcc.model.*;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.slack.SlackBot;
import net.bcnnm.notifications.stats.AggregationException;
import net.bcnnm.notifications.stats.ReportsAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationServer {
    private final AgentReportDao reportDao;
    private final List<ReportsAggregator> reportsAggregators;

    @Autowired
    private SlackBot slackBot;
    private SocketChannel fccSocketChannel;

    @Autowired
    public NotificationServer(AgentReportDao reportDao, List<ReportsAggregator> reportsAggregators) {
        this.reportDao = reportDao;
        this.reportsAggregators = reportsAggregators;
    }

    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();

            String hostname = "127.0.0.1";
            final int serverPort = 9000;
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

    private void handleIncoming(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            int read = socketChannel.read(byteBuffer);
            if (read == -1) {
                System.out.println("Client unexpectedly disconected..");
                socketChannel.close();
            }
            else {
                byteBuffer.flip();

                Message incomingMessage = Encoder.decode(byteBuffer.array());
                byteBuffer.clear();
                System.out.println("Received incoming message: " + incomingMessage.getMessageType());

                switch (incomingMessage.getMessageType()) {
                    case FCC_HELLO:
                        System.out.println("Responding with AUTH message..");

                        socketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccAuthMessage())));
                        break;
                    case FCC_STATUS:
                        System.out.println("Received status..");
                        FccStatusMessage fccStatusMessage = (FccStatusMessage) incomingMessage;

                        slackBot.replyWithStatus(fccStatusMessage.getPayload());
                        break;
                    case FCC_REPORT:
                        System.out.println("Received report..");
                        FccReportMessage fccReportMessage = (FccReportMessage) incomingMessage;
                        AgentReport receivedReport = fccReportMessage.getPayload();

                        reportDao.saveReport(receivedReport);
                        slackBot.sendToDefaultChannel(receivedReport);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            socketChannel.close();
            e.printStackTrace();
        }
    }

    public void askFccForStatus() {
        try {
            System.out.println("Asking FCC for status..");
            fccSocketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccAskMessage())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStat(String function, String key, String prefix) {
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

    public AgentReport getReport(String taskId) {
        return reportDao.getReport(taskId);
    }
}
