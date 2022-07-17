package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.util.UUID;

public record Account(Id id, Number number, Money balance) {

    public record Id(UUID value) {
        public Id {
            Preconditions.requireNonNull(value);
        }

        public static Id of(UUID value) {
            return new Id(value);
        }

        public static Id of(String value) {
            Preconditions.requireNonNull(value);
            return new Id(UUID.fromString(value));
        }
    }

    public record Number(String value) {

        public Number {
            Preconditions.requireNonNull(value);
            Preconditions.validateAccountNumber(value);
        }

        public static Number of(String value) {
            return new Number(value);
        }
    }
}
