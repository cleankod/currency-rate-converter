package pl.cleankod.exchange.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import pl.cleankod.util.Preconditions;

import java.util.UUID;
import java.util.regex.Pattern;

public record Account(Id id, Number number, Money balance) {

    public record Id(@JsonValue UUID value) {
        public Id {
            Preconditions.requireNonNull(value);
        }

        public static Id of(UUID value) {
            return new Id(value);
        }
        @JsonCreator
        public static Id of(String value) {
            Preconditions.requireNonNull(value);
            return new Id(UUID.fromString(value));
        }
    }

    public record Number(@JsonValue String value) {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        public Number {
            Preconditions.requireNonNull(value);
            if (!PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("The account number does not match the pattern: " + PATTERN);
            }
        }
        @JsonCreator
        public static Number of(String value) {
            return new Number(value);
        }
    }
}
