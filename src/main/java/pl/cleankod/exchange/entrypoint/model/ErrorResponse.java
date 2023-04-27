package pl.cleankod.exchange.entrypoint.model;

public record ErrorResponse(String message, Exception exception) {

}
