package net.bcnnm.notifications.calcs;

import net.bcnnm.notifications.model.ExperimentReport;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface ReportsAggregator {
    List<String> CURRENTLY_SUPPORTED_CALCS = Arrays.asList("Min", "Max", "Mean");

    String getName();
    String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException;
}
