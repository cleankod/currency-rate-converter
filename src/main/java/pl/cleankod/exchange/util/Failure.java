package pl.cleankod.exchange.util;

public class Failure {
    public final FailureReason failureReason;
    public final Exception exception;

    public Failure(FailureReason failureReason, Exception exception) {
        this.failureReason = failureReason;
        this.exception = exception;
    }
}
