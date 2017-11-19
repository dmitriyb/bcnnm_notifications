package net.bcnnm.notifications.fcc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

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
                System.out.println("Run stanby mode, ready for requests");
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
                System.out.println("Recieved incoming message: " + incomingMessage.getMessageType());

                switch (incomingMessage.getMessageType()) {
                    case FCC_ASK:
                        System.out.println("Responding with STATUS message..");
                        socketChannel.write(ByteBuffer.wrap(Encoder.encode(new FccStatusMessage())));
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
