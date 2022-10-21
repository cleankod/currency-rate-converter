package pl.cleankod.exchange.entrypoint.model;

public record ApiError(Integer status,String message, String timestamp) {
}
