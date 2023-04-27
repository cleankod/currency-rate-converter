package pl.cleankod.exchange.entrypoint.model;

public record ApiError(Integer statusCode, String message) {
}
