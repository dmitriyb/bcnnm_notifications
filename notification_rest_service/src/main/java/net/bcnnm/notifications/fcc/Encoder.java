package net.bcnnm.notifications.fcc;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Encoder {
    private static final int HEADER_LENGTH = Integer.BYTES;

    public static Message decode(byte[] message) {
        byte[] header = Arrays.copyOfRange(message, 0, HEADER_LENGTH);
        MessageType messageType = decodeHeader(header);

        switch (messageType) {
            case FCC_HELLO:
                return new FccHelloMessage();
            case FCC_AUTH:
                return new FccAuthMessage();
            case FCC_ASK:
                return new FccAskMessage();
            case FCC_STATUS:
                return new FccStatusMessage();
//            case FCC_ADD_EXPERIMENT:
//                return decodePayload(messageType, agentID, payload);
//            case FCC_KILL:
//                return decodePayload(messageType, agentID, payload);
            default:
                System.out.println(String.format("Unknown message type: %s", messageType));
                return null;
        }
    }

    public static byte[] encode(Message message) {
        byte[] header = encodeHeader(message.getMessageType());

        return ByteBuffer.allocate(header.length)
                .put(header)
                .array();
    }

    private static byte[] encodeHeader(MessageType messageType) {
        ByteBuffer header = ByteBuffer.allocate(Integer.BYTES);
        header.putInt(messageType.getId());
        return header.array();
    }

    private static MessageType decodeHeader(byte[] header) {
        byte[] messageTypeBytes = Arrays.copyOfRange(header, 0, Integer.BYTES);
        return MessageType.getByID(ByteBuffer.wrap(messageTypeBytes).getInt());
    }
}
