package pl.cleankod.exchange.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import pl.cleankod.util.Preconditions;

import java.util.UUID;
import java.util.regex.Pattern;

public record Account(Id id, Number number, Money balance) {

    @JsonGetter(value = "id")
    public UUID getIdAsValue(){
        return id().value();
    }
    @JsonGetter(value = "number")
    public String getNumberAsValue(){
        return number().value();
    }

    public static record Id(UUID value) {
        public Id {
            Preconditions.requireNonNull(value);
        }

        public static Id of(UUID value) {
            return new Id(value);
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Id of(String value) {
            Preconditions.requireNonNull(value);
            return new Id(UUID.fromString(value));
        }
    }

    public static record Number(String value) {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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
