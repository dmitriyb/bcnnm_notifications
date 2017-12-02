package net.bcnnm.notifications.fcc;

import net.bcnnm.notifications.fcc.model.*;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.TaskStatus;

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

public class FccControlCenterStub {
    public static void main(String[] args){
        try {
            SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
            System.out.println("Connected to server.. ");

            ByteBuffer buffer = ByteBuffer.wrap(Encoder.encode(new FccHelloMessage()));
            System.out.println("Sending HELLO message.");

            sc.write(buffer);
            buffer.clear();
            sc.read(buffer);
            Message response = Encoder.decode(buffer.array());
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
                        try {
                            sc.write(ByteBuffer.wrap(Encoder.encode(new FccReportMessage(agentReports.get(0)))));
                            agentReports.remove(0);
                            if (agentReports.isEmpty()) {
                                this.cancel();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                    handleAsk(selectionKey);
                }
            }
        }
    }

    private static void handleAsk(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            int read = socketChannel.read(byteBuffer);
            if (read == -1) {
                System.out.println("Server unexpectedly disconected..");
                socketChannel.close();
            }
            else {
                byteBuffer.flip();

                Message incomingMessage = Encoder.decode(byteBuffer.array());
                byteBuffer.clear();
                System.out.println("Received incoming message: " + incomingMessage.getMessageType());

                switch (incomingMessage.getMessageType()) {
                    case FCC_ASK:
                        System.out.println("Responding with STATUS message..");

                        FccStatus fccStatus = new FccStatus("FCC Stub", Arrays.asList("Agent One", "Agent Two"));

                        socketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccStatusMessage(fccStatus))));
                        break;
                    default:
                        System.out.println("Unsupported message type");
                }
            }
        } catch (IOException e) {
            socketChannel.close();
            e.printStackTrace();
        }
    }
}
