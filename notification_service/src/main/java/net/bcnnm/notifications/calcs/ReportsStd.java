package net.bcnnm.notifications.calcs;

import net.bcnnm.notifications.model.ExperimentReport;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@Component
public class ReportsStd implements ReportsAggregator {
    @Override
    public String getName() {
        return "std";
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
                }).toArray();

        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        double mean = sum / values.length;

        double sumSquares = 0;
        for (double value : values) {
            sumSquares += (value - mean) * (value - mean);
        }

        return String.format("Key=%s, Std=%s",key, Math.sqrt(sumSquares / values.length));
    }
}
