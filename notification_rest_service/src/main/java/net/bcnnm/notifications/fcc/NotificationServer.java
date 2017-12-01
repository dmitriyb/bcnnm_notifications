package net.bcnnm.notifications.fcc;

import me.ramswaroop.jbot.core.slack.models.Event;
import net.bcnnm.notifications.fcc.model.FccAskMessage;
import net.bcnnm.notifications.fcc.model.FccAuthMessage;
import net.bcnnm.notifications.fcc.model.FccStatusMessage;
import net.bcnnm.notifications.fcc.model.Message;
import net.bcnnm.notifications.slack.SlackBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Component
public class NotificationServer {

    private SocketChannel fccSocketChannel;

    @Autowired
    private SlackBot slackBot;

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
                System.out.println("Recieved incoming message: " + incomingMessage.getMessageType());

                switch (incomingMessage.getMessageType()) {
                    case FCC_HELLO:
                        System.out.println("Responding with AUTH message..");
                        socketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccAuthMessage())));
                        break;
                    case FCC_STATUS:
                        System.out.println("Recieved status..");
                        FccStatusMessage fccStatusMessage = (FccStatusMessage) incomingMessage;
                        slackBot.replyWithObject(fccStatusMessage.getPayload());
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
}
