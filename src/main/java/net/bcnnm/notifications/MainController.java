package net.bcnnm.notifications;

import net.bcnnm.notifications.model.AgentReport;
import net.bcnnm.notifications.model.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Path("/api")
public class MainController {
    private final MongoOperations mongoTemplate;

    @Autowired
    public MainController(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @POST
    @Path("/report")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void report(AgentReport report) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskId").is(report.getTaskId()));
        query.addCriteria(Criteria.where("agentIp").is(report.getAgentIp()));

        Update update = new Update();
        update.set("timestamp", report.getTimestamp());
        update.set("progress", report.getProgress());
        update.set("status", report.getStatus());
        update.pushAll("info", report.getInfo().toArray());

        mongoTemplate.upsert(query, update, AgentReport.class);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AgentReport> listReports() {
        return mongoTemplate.findAll(AgentReport.class);
    }

    @GET
    @Path("/get/{task}")
    @Produces(MediaType.APPLICATION_JSON)
    public AgentReport getReportByTask(@PathParam("task") String taskId) {
        Query byTask = new Query().addCriteria(Criteria.where("taskId").is(taskId));
        return mongoTemplate.findOne(byTask, AgentReport.class);
    }

    // FOR TEST PURPOSE ONLY

    /**
     * Clears MongoDB storage
     */
    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public void clear() {
        mongoTemplate.dropCollection(AgentReport.class);
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
        WebTarget target = client.target("http://localhost:8080").path("api").path("report");
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
