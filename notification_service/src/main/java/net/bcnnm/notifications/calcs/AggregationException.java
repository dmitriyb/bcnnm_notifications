package net.bcnnm.notifications.calcs;

public class AggregationException extends RuntimeException {
    public AggregationException(String message) {
        super(message);
    }

    public AggregationException(String message, Exception cause) {
        super(message, cause);
    }
}
