package net.bcnnm.notifications.fcc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class FccSocketSenderStub {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));

        System.out.println("Connected to server.. ");

        ByteBuffer buffer = ByteBuffer.wrap(Encoder.encode(new FccHelloMessage()));
        System.out.println("Sending HELLO message.");

        sc.write(buffer);
        buffer.clear();
        sc.read(buffer);
        Message response = Encoder.decode(buffer.array());
        System.out.println("Server responded with: " + response.getMessageType());

//        Thread.sleep(500000);

        sc.close();
        System.out.println("Closed connection.. ");
    }
}
