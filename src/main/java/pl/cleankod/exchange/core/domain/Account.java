package pl.cleankod.exchange.core.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public record Account(Id id, Number number, Money balance) {

    public static record Id(UUID value) {
        public Id {
            Objects.requireNonNull(value, "Given value cannot be null");
        }

        public static Id of(UUID value) {
            return new Id(value);
        }

        public static Id of(String value) {
            Objects.requireNonNull(value, "Given value cannot be null");
            return new Id(UUID.fromString(value));
        }
    }

    public static record Number(String value) {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        public Number {
            Objects.requireNonNull(value, "Given value cannot be null");
            if (!PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("The account number does not match the pattern: " + PATTERN);
            }
        }

        public static Number of(String value) {
            return new Number(value);
        }
    }
}
