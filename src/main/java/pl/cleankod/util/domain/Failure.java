package pl.cleankod.util.domain;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class Failure {

    private final String code;
    private final String message;
    private final Throwable exception;

    public Failure(String code, String message, Throwable exception) {
        this.code = requireNonNull(code);
        this.message = requireNonNull(message);
        this.exception = exception;
    }

    public Failure(String code, String message) {
        this(code, message, null);
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public Optional<Throwable> exception() {
        return Optional.ofNullable(exception);
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(code, message);
    }
}