package net.bcnnm.notifications.calcs;

import net.bcnnm.notifications.model.ExperimentReport;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValueArray implements ReportsAggregator {
    @Override
    public String getName() {
        return "array";
    }

    @Override
    public String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException {

        List<Object> keyValues = reports.stream().map(report -> {
            try {
                return PropertyUtils.getSimpleProperty(report, key);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException | NoSuchMethodException e) {
                // LOG exception here
                throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
            }
        }).collect(Collectors.toList());

        return String.format("Key=%s, Array=%s",key, keyValues);
    }
}
