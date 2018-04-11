package net.bcnnm.notifications.stats;

import net.bcnnm.notifications.model.ExperimentReport;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

@Component
public class ReportsMean implements ReportsAggregator{
    @Override
    public String getName() {
        return "mean";
    }

    @Override
    public String aggregate(Collection<ExperimentReport> reports, String key) throws AggregationException {
        PropertyDescriptor keyPropertyDescriptor;
        try {
            keyPropertyDescriptor = new PropertyDescriptor(key, ExperimentReport.class);
        } catch (IntrospectionException e) {
            throw new AggregationException(String.format("Faild to get field: %s", key), e);
        }
        Method keyGetter = keyPropertyDescriptor.getReadMethod();

        double meanValue = reports.stream()
                .mapToDouble(report -> {
                    try {
                        return ((Number) keyGetter.invoke(report)).doubleValue();
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                        // LOG exception here
                        throw new AggregationException(String.format("Failed to get value of field: %s", key), e);
                    }
                })
                .average().getAsDouble();

        return String.format("Key=%s, Mean=%s",key, meanValue);
    }
}
