package net.bcnnm.stub;


import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Simple reporting service stub that moves task on +20% on every /request/{taskId} request
 */
@RestController
public class MainController {

    private HashMap<String, Integer> taskProgress = new HashMap<>();

    @Value("${notification.service.url}")
    private String NOTIFICATION_SERVICE_URL;


    @RequestMapping("/request/{taskId}")
    public Response requestReport(@PathVariable("taskId") String taskId) {
        reportOnTask(taskId);
        return Response.ok().build();
    }

    private void reportOnTask(String taskId) {
        String sampleInfo = "CPU USAGE / MEM USAGE SAMPLE INFO at %s%%";
        String localhostIp = "127.0.0.1";

        AgentReport report = new AgentReport();
        report.setAgentIp(localhostIp);
        report.setTaskId(taskId);
        report.setTimestamp(new Date());

        if (!taskProgress.containsKey(taskId)) {
            report.setStatus(TaskStatus.STARTED);
            report.setProgress(0);
            taskProgress.put(taskId, 0);
        }
        else {
            Integer currentProgress = taskProgress.get(taskId);
            currentProgress += 20;

            if (currentProgress < 100) {
                report.setStatus(TaskStatus.IN_PROGRESS);
                report.setProgress(currentProgress);
                taskProgress.put(taskId, currentProgress);
            }
            else {
                report.setStatus(TaskStatus.FINISHED);
                report.setProgress(100);
                taskProgress.remove(taskId);
            }
        }

        report.setInfo(Arrays.asList(String.format(sampleInfo, report.getProgress())));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(NOTIFICATION_SERVICE_URL).path("api").path("report");
        target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(report, MediaType.APPLICATION_JSON));
    }
}
