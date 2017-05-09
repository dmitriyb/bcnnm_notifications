package net.bcnnm.notifications;

import net.bcnnm.notifications.model.AgentReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentReportDao {
    private final MongoOperations mongoTemplate;

    @Autowired
    public AgentReportDao(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveReport(AgentReport report) {
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

    public List<AgentReport> getReportList() {
        return mongoTemplate.findAll(AgentReport.class);
    }

    public AgentReport getReport(String taskId) {
        Query byTask = new Query().addCriteria(Criteria.where("taskId").is(taskId));
        return mongoTemplate.findOne(byTask, AgentReport.class);
    }

    // FOR TEST PURPOSE ONLY
    public void clearAllReports() {
        mongoTemplate.dropCollection(AgentReport.class);
    }

}
