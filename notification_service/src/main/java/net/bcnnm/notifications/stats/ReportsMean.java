package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.ExperimentReport;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@Component
public class ReportsMean implements ReportsAggregator{
    @Override
    public String getName() {
        return "mean";
    }

    @Override
    public String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException {
        double meanValue = reports.stream()
                .mapToDouble(report -> {
                    try {
                        return ((Number) PropertyUtils.getSimpleProperty(report, key)).doubleValue();
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException | NoSuchMethodException e) {
                        // LOG exception here
                        throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
                    }
                })
                .average().getAsDouble();

        return String.format("Key=%s, Mean=%s",key, meanValue);
    }
}
