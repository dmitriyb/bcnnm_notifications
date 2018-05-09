package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.ExperimentReport;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface ReportsAggregator {
    List<String> CURRENTLY_SUPPORTED_STATS = Arrays.asList("Min", "Max", "Mean");

    String getName();
    String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException;
}
