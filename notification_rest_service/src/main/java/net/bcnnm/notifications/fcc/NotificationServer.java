package net.bcnnm.notifications.fcc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NotificationServer {

    public static void main(String[] args) throws IOException {
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
                        SocketChannel sc = ((ServerSocketChannel) selectionKey.channel()).accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);

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
    }

    private static void handleIncoming(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(byteBuffer);
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
                break;
            default:
                break;
        }
    }
}
