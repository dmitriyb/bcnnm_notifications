package net.bcnnm.notifications;

import net.bcnnm.notifications.model.ExperimentReport;
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

    public void saveReport(ExperimentReport report) {
        Query query = new Query();
        query.addCriteria(Criteria.where("fccId").is(report.getFccId()));
        query.addCriteria(Criteria.where("experimentId").is(report.getExperimentId()));

        Update update = new Update();
        update.set("modelingTime", report.getModelingTime());
        update.set("progress", report.getProgress());
        update.set("status", report.getStatus());
        update.pushAll("info", report.getInfo().toArray());

        mongoTemplate.upsert(query, update, ExperimentReport.class);
    }

    public List<ExperimentReport> getReportList() {
        return mongoTemplate.findAll(ExperimentReport.class);
    }

    public ExperimentReport getReport(String taskId) {
        Query byTask = new Query().addCriteria(Criteria.where("taskId").is(taskId));
        return mongoTemplate.findOne(byTask, ExperimentReport.class);
    }
}
