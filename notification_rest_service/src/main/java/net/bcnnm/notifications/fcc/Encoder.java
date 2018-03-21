package net.bcnnm.notifications.fcc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.bcnnm.notifications.fcc.model.*;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.ExperimentReport;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Encoder {
    private static final int HEADER_LENGTH = Integer.BYTES;

    public static Message decode(byte[] message) {
        byte[] header = Arrays.copyOfRange(message, 0, HEADER_LENGTH);
        byte[] body = Arrays.copyOfRange(message, HEADER_LENGTH, message.length);
        MessageType messageType = decodeHeader(header);

        switch (messageType) {
            case FCC_HELLO:
                return new FccHelloMessage();
            case FCC_AUTH:
                return new FccAuthMessage();
            case FCC_ASK:
                return new FccAskMessage();
            case FCC_STATUS:
                FccStatus fccStatus = decodeBody(body, FccStatus.class);
                return new FccStatusMessage(fccStatus);
            case FCC_REPORT:
                ExperimentReport experimentReport = decodeBody(body, ExperimentReport.class);
                return new FccReportMessage(experimentReport);
//            case FCC_ADD_EXPERIMENT:
//                return decodePayload(messageType, agentID, payload);
//            case FCC_KILL:
//                return decodePayload(messageType, agentID, payload);
            case FCC_COMMAND:
                FccCommand fccCommand = decodeBody(body, FccCommand.class);
                return new FccCommandMessage(fccCommand);
            case FCC_ACKNOWLEDGE:
                FccAcknowledge acknowledge = decodeBody(body, FccAcknowledge.class);
                return new FccAcknowledgeMessage(acknowledge);
            default:
                System.out.println(String.format("Unknown message type: %s", messageType));
                return null;
        }
    }

    public static byte[] encode(Message message) {
        byte[] header = encodeHeader(message.getMessageType());

        byte[] body = encodeBody(message.getPayload());

        return ByteBuffer.allocate(header.length + body.length)
                .put(header)
                .put(body)
                .array();
    }

    private static byte[] encodeHeader(MessageType messageType) {
        ByteBuffer header = ByteBuffer.allocate(Integer.BYTES);
        header.putInt(messageType.getId());
        return header.array();
    }

    private static MessageType decodeHeader(byte[] header) {
        return MessageType.getByID(ByteBuffer.wrap(header).getInt());
    }

    private static byte[] encodeBody(Object obj) {
        Gson gson = new Gson();

        return gson.toJson(obj).getBytes(UTF_8);
    }

    private static <T extends Payload> T decodeBody(byte[] body, Class<T> toClass) {
        Gson gson = new GsonBuilder().setLenient().create();
        String bodyString = new String(body, UTF_8).trim();

        return gson.fromJson(bodyString, toClass);
    }
}
