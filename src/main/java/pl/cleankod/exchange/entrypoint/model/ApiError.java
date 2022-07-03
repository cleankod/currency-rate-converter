package pl.cleankod.exchange.entrypoint.model;

import org.slf4j.MDC;

public record ApiError(String message, String traceId) {
    public ApiError(String message) {
        this(message, MDC.get("TraceId"));
    }
}