package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.ExperimentReport;

import java.util.Collection;

public interface ReportsAggregator {
    String getName();
    String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException;
}
