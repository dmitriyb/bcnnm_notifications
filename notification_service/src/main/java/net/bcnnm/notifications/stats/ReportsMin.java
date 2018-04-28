package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.ExperimentReport;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@Component
public class ReportsMin implements ReportsAggregator {
    @Override
    public String getName() {
        return "min";
    }

    @Override
    public String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException {
        double minValue = reports.stream()
                .mapToDouble(report -> {
                    try {
                        return ((Number) PropertyUtils.getSimpleProperty(report, key)).doubleValue();
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException | NoSuchMethodException e) {
                        // LOG exception here
                        throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
                    }
                })
                .min().getAsDouble();

        return String.format("Key=%s, Min=%s",key, minValue);
    }
}
