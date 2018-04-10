package net.bcnnm.notifications.fcc;

import net.bcnnm.notifications.fcc.model.*;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static net.bcnnm.notifications.fcc.ProtocolCommunication.readFromSocket;
import static net.bcnnm.notifications.fcc.ProtocolCommunication.writeToSocket;

public class FccControlCenterStub {
    public static void main(String[] args){
        try {
            SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9001));
            System.out.println("Connected to server.. ");

            System.out.println("Sending HELLO message.");
            writeToSocket(sc, Encoder.encode(new FccHelloMessage()));

            Message response = Encoder.decode(readFromSocket(sc));
            System.out.println("Server responded with: " + response.getMessageType());

            if (response.getMessageType() == MessageType.FCC_AUTH) {
                System.out.println("Run standby mode, ready for requests");

                Timer timer = new Timer();
                List<AgentReport> agentReports = new ArrayList<>(Arrays.asList(
                        new AgentReport("Task One", "127.0.0.1", new Date(),
                                TaskStatus.STARTED, 0, Collections.singletonList("First task info")),
                        new AgentReport("Task Two", "127.0.0.1", new Date(),
                                TaskStatus.STARTED, 0, Collections.singletonList("Second task info")),
                        new AgentReport("Task One", "127.0.0.1", new Date(),
                                TaskStatus.IN_PROGRESS, 65, Collections.singletonList("Progressig first task"))
                ));

                TimerTask sendReport = new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Timer Task: Sending report..");
                        /*try {
                            //sc.write(ByteBuffer.wrap(Encoder.encode(new FccReportMessage(agentReports.get(0)))));
                            agentReports.remove(0);
                            if (agentReports.isEmpty()) {
                                this.cancel();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                };
                timer.schedule(sendReport, 10000, 10000);

                run_standby(sc);
            }

            sc.close();
            System.out.println("Closed connection.. ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void run_standby(SocketChannel sc) throws IOException {
        Selector selector = Selector.open();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        while (true) {
            selector.select();
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                iterator.remove();

                if (selectionKey.isReadable()) {
                    handleIncoming(selectionKey);
                }
            }
        }
    }

    private static void handleIncoming(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] message = readFromSocket(socketChannel);
        if (message.length == 0) {
            System.out.println("Server unexpectedly disconnected..");
            return;
        }

        Message incomingMessage = Encoder.decode(message);
        System.out.println("Received incoming message: " + incomingMessage.getMessageType());

        switch (incomingMessage.getMessageType()) {
            case FCC_HELLO:
                break;
            case FCC_AUTH:
                break;
            case FCC_ASK:
                System.out.println("Responding with STATUS message..");

                //FccStatus fccStatus = new FccStatus("FCC Stub", Arrays.asList("Agent One", "Agent Two"), experiments);

                //socketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccStatusMessage(fccStatus))));
                break;
            case FCC_STATUS:
                break;
            case FCC_REPORT:
                break;
            case FCC_COMMAND:
                System.out.println("Responding with ACKNOWLEDGE message..");

                FccAcknowledge acknowledge = buildAcknowledge((FccCommandMessage) incomingMessage);

                writeToSocket(socketChannel, Encoder.encode(new FccAcknowledgeMessage(acknowledge)));
                break;
            default:
                System.out.println("Unsupported message type");
        }

    }

    private static FccAcknowledge buildAcknowledge(FccCommandMessage incomingMessage) {
        FccCommand fccCommand = incomingMessage.getPayload();

        switch (fccCommand.getCommandType()) {
            case START:

            case STOP:
                String experimentId = new String(fccCommand.getDetails());
                if (!experimentId.endsWith("TOFAIL")) {
                    String ackDetails = String.format("%s, %s",
                            fccCommand.getCommandType(), experimentId);

                    return new FccAcknowledge(FccAcknowledge.Status.OK, ackDetails);
                }
                else {
                    String ackDetails = String.format("%s, %s",
                            fccCommand.getCommandType(), experimentId);

                    return new FccAcknowledge(FccAcknowledge.Status.FAILED, ackDetails);
                }
            case UPLOAD:
                byte[] fileBytes = fccCommand.getDetails();

                experimentId = "FccStubExperimentId";
                try {
                    FileUtils.writeByteArrayToFile(new File("config_received.zip"), fileBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String ackDetails = String.format("Configuration uploaded, experimentId: %s", experimentId);
                return new FccAcknowledge(FccAcknowledge.Status.OK, ackDetails);

            default:
                System.out.println("Unsupported command type");
                return new FccAcknowledge(FccAcknowledge.Status.FAILED, "Unsupported command type");
        }
    }
}
