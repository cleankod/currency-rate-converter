package pl.cleankod.exchange.exception.error;

import java.time.Instant;

public record ErrorMessage(int status, String message, Instant timestamp, Error error) {
}
