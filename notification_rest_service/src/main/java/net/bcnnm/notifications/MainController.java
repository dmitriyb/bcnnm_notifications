package net.bcnnm.notifications;

import net.bcnnm.notifications.slack.SlackChannelWriter;
import net.bcnnm.notifications.slack.format.SlackFormatter;
import net.bcnnm.notifications.slack.format.SlackFormatterException;
import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Path("/api")
public class MainController {
    private final AgentReportDao reportDao;
    private final SlackChannelWriter slack;

    @Autowired
    public MainController(AgentReportDao reportDao, SlackChannelWriter slack) {
        this.reportDao = reportDao;
        this.slack = slack;
    }

    @POST
    @Path("/report")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void report(AgentReport report) {
        reportDao.saveReport(report);

        try {
            slack.writeMessage(SlackFormatter.format(report, AgentReport.class));
        } catch (SlackFormatterException e) {
            // TODO: add proper logging
            e.printStackTrace();
            slack.writeMessage(report.toString());
        }
    }

    @GET
    @Path("/task")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AgentReport> listReports() {
        return reportDao.getReportList();
    }

    @GET
    @Path("/task/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AgentReport getReportByTask(@PathParam("taskId") String taskId) {
        return reportDao.getReport(taskId);
    }

    @GET
    @Path("/request/{taskId}")
    public Response requestReport(@PathParam("taskId") String taskId) {
        AgentReport lastReport = reportDao.getReport(taskId);
        if (lastReport == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String agentHost = lastReport.getAgentIp();

        UriBuilder builder = new JerseyUriBuilder();
        URI uri = builder.scheme("http").host(agentHost).port(8080).path("request").path(taskId).build();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        return target.request().get();
    }

    // FOR TEST PURPOSE ONLY

    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public void clear() {
        reportDao.clearAllReports();
    }

    /**
     * Test report post. Create a client and call /report with {@link AgentReport} built from parameters
     */
    @GET
    @Path("/treport/{taskId}/{progress}")
    public Response testReport(@PathParam("taskId") String taskId, @PathParam("progress") int progress) {
        String localhostIp = "127.0.0.1";
        String sampleInfo = "CPU USAGE / MEM USAGE SAMPLE INFO at %s%%";

        TaskStatus status = TaskStatus.STARTED;
        if (progress > 0) {
            status = (progress < 100) ? TaskStatus.IN_PROGRESS : TaskStatus.FINISHED;
        }

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8090").path("api").path("report");
        AgentReport agentReport = new AgentReport(taskId,
                                                    localhostIp,
                                                    new Date(),
                                                    status,
                                                    progress,
                                                    Arrays.asList(String.format(sampleInfo, progress)));

        return target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(agentReport, MediaType.APPLICATION_JSON));
    }
}
