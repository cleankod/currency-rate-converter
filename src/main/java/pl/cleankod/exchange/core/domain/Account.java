package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.util.UUID;
import java.util.regex.Pattern;

public record Account(Id id, Number number, WholeMoney balance) {
    public record Id(UUID value) implements SingleValueObject<UUID> {
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

    public record Number(String value) implements SingleValueObject<String> {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        public Number {
            Preconditions.requireNonNull(value);
            if (!PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("The account number does not match the pattern: " + PATTERN);
            }
        }

        public static Number of(String value) {
            return new Number(value);
        }
    }
}
