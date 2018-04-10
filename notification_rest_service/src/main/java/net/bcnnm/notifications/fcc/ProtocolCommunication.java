package net.bcnnm.notifications.fcc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProtocolCommunication {

    public static void writeToSocket(SocketChannel socketChannel, byte[] message) {
        try {
            // send 4 bytes with length of the following message
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4).putInt(message.length);
            lengthBuffer.flip();
            socketChannel.write(lengthBuffer);

            ByteBuffer messageBuffer = ByteBuffer.wrap(message);
            while (messageBuffer.hasRemaining()) {
                socketChannel.write(messageBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFromSocket(SocketChannel socketChannel) {
        try {
            ByteBuffer messageLengthBuffer = ByteBuffer.allocate(4);
            int bytesRead = socketChannel.read(messageLengthBuffer);
            System.out.printf("Socket channel info: %s, %s\n", socketChannel.getLocalAddress(), socketChannel.getRemoteAddress());
            if (bytesRead == -1) {
                socketChannel.close();
                return new byte[0];
            }
            else {
                messageLengthBuffer.flip();
                int messageLength = messageLengthBuffer.getInt();
                ByteBuffer messageBuffer = ByteBuffer.allocate(messageLength);

                while (messageBuffer.hasRemaining()) {
                    bytesRead = socketChannel.read(messageBuffer);
                    if (bytesRead == -1) {
                        socketChannel.close();
                        return new byte[0];
                    }
                }
                messageBuffer.flip();

                byte[] message = messageBuffer.array();
                messageBuffer.clear();

                return message;
            }
        } catch (IOException e) {
            e.printStackTrace();

            try {
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return new byte[0];
        }
    }
}
