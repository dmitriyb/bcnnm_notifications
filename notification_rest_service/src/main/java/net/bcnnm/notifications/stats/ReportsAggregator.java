package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.AgentReport;

import java.util.Collection;

public interface ReportsAggregator {
    String getName();
    String aggregate(Collection<AgentReport> reports, String key) throws AggregationException;
}
