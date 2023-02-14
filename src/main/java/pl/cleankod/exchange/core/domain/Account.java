package pl.cleankod.exchange.core.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.cleankod.util.IdDeserializer;
import pl.cleankod.util.NumberDeserializer;
import pl.cleankod.util.Preconditions;

import java.util.UUID;
import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Account(@JsonUnwrapped @JsonDeserialize(using = IdDeserializer.class) Id id,
                      @JsonUnwrapped @JsonDeserialize(using = NumberDeserializer.class) Number number, Money balance) {

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record Id(@JsonProperty("id") UUID value) {
        public Id {
            Preconditions.requireNonNull(value);
        }

        @JsonCreator
        public Id(String value) {
            this(UUID.fromString(value));
        }


        public static Id of(UUID value) {
            return new Id(value);
        }

        public static Id of(String value) {
            Preconditions.requireNonNull(value);
            return new Id(UUID.fromString(value));
        }
    }

    //@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record Number(@JsonProperty("number") String value) {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        @JsonCreator
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
