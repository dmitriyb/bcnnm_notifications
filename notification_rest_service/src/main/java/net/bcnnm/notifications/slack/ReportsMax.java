package net.bcnnm.notifications.slack;

import net.bcnnm.notifications.model.AgentReport;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

@Component
public class ReportsMax implements ReportsAggregator {
    @Override
    public String getName() {
        return "max";
    }

    @Override
    public String aggregate(Collection<AgentReport> reports, String key) throws AggregationException {
        PropertyDescriptor keyPropertyDescriptor = null;
        try {
            keyPropertyDescriptor = new PropertyDescriptor(key, AgentReport.class);
        } catch (IntrospectionException e) {
            throw new AggregationException(String.format("Faild to get field: %s", key), e);
        }
        Method keyGetter = keyPropertyDescriptor.getReadMethod();

        long maxValue = reports.stream()
                .mapToLong(report -> {
                    try {
                        return ((Number) keyGetter.invoke(report)).longValue();
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                        // LOG exception here
                        throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
                    }
                })
                .max().getAsLong();

        return String.format("Key=%s, Max=%s",key, maxValue);
    }
}
