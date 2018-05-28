package net.bcnnm.notifications.calcs;

import net.bcnnm.notifications.model.ExperimentReport;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ReportsMedian implements ReportsAggregator {
    @Override
    public String getName() {
        return "median";
    }

    @Override
    public String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException {
        double[] values = reports.stream()
                .mapToDouble(report -> {
                    try {
                        return ((Number) PropertyUtils.getSimpleProperty(report, key)).doubleValue();
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException | NoSuchMethodException e) {
                        // LOG exception here
                        throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
                    }
                }).sorted().toArray();

        double median = (values.length % 2 != 0) ?
                values[values.length / 2] :
                (values[values.length / 2] + values[values.length / 2 - 1]) / 2;

        return String.format("Key=%s, Median=%s",key, median);
    }
}
