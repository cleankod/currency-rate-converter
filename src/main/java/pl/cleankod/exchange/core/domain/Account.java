package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.exception.InvalidDataException;
import pl.cleankod.exchange.exception.error_type.AccountErrorType;
import pl.cleankod.util.Preconditions;

import java.util.UUID;
import java.util.regex.Pattern;

public record Account(Id id, Number number, Money balance) {

    public record Id(UUID value) {
        public Id {
            Preconditions.requireNonNull(value);
        }

        public static Id of(final UUID value) {
            Preconditions.requireNonNull(value);
            return new Id(value);
        }

        public static Id of(final String value) {
            Preconditions.requireNonNull(value);
            return new Id(UUID.fromString(value));
        }
    }

    public record Number(String value) {
        private static final Pattern PATTERN =
                Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

        public Number {
            Preconditions.requireNonNull(value);
            if (!PATTERN.matcher(value).matches()) {
                throw new InvalidDataException(AccountErrorType.INVALID_ACCOUNT_NUMBER,
                        String.format("Account number '%s' is invalid", value));
            }
        }

        public static Number of(final String value) {
            return new Number(value);
        }
    }
}
