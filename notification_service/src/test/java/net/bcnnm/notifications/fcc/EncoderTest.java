package net.bcnnm.notifications.fcc;

import net.bcnnm.notifications.fcc.model.FccReportMessage;
import net.bcnnm.notifications.fcc.model.Message;
import net.bcnnm.notifications.model.ExperimentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EncoderTest {

    @Test
    public void encodeDecodeExperimentReport() {

        // Given
        UUID fccId = UUID.randomUUID();
        UUID experimentId = UUID.randomUUID();
        List<String> info = Arrays.asList("Test info", "Another test info", "More test info");

        ExperimentReport experimentReport = new ExperimentReport(fccId.toString(), experimentId.toString(),
                0L, TaskStatus.FINISHED, 100 , info);

        // When
        byte[] encoded = Encoder.encode(new FccReportMessage(experimentReport));
        Message decodedMessage = Encoder.decode(encoded);
        ExperimentReport decodedReport = ((FccReportMessage) decodedMessage).getPayload();

        // Then
        assertThat(experimentReport).isEqualToComparingFieldByField(decodedReport);
    }
}