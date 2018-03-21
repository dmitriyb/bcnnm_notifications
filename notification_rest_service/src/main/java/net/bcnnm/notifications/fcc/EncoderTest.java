package net.bcnnm.notifications.fcc;

import net.bcnnm.notifications.fcc.model.FccReport;
import net.bcnnm.notifications.fcc.model.FccReportMessage;
import net.bcnnm.notifications.fcc.model.Message;
import net.bcnnm.notifications.model.ExperimentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EncoderTest {

    @Test
    public void test() {
        UUID fccId = UUID.randomUUID();
        UUID experimentId = UUID.randomUUID();
        List<String> strings = new ArrayList<>();
        strings.add("test");
        strings.add("test2");
        strings.add("test3");

        System.out.printf("Fcc id: %s, experimentId: %s, strings: %s\n\n", fccId, experimentId, strings);

        ExperimentReport experimentReport = new ExperimentReport(fccId.toString(), experimentId.toString(), 0L, TaskStatus.FINISHED, 100 , strings);
        byte[] encoded = Encoder.encode(new FccReportMessage(experimentReport));

        Message decodedMessage = Encoder.decode(encoded);
        ExperimentReport fccReport1 = ((FccReportMessage) decodedMessage).getPayload();
        System.out.printf("ExperimentId: %s, strings: %s", fccReport1.getExperimentId(), fccReport1.getInfo());
    }
}