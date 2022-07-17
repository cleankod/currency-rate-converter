package pl.cleankod.util;

import pl.cleankod.exchange.entrypoint.model.ValidationException;

import java.util.Currency;
import java.util.UUID;
import java.util.regex.Pattern;

public interface Preconditions {

    Pattern PATTERN =
            Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("Given value cannot be null");
        }
        return obj;
    }

    static void validateAccountNumber(String account) throws IllegalArgumentException {
        if (!PATTERN.matcher(account).matches()) {
            throw new ValidationException(String.format("The account number does not match the pattern %s ", PATTERN));
        }
    }

    static void validateUUIDFormat(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Format validation failed for id %s", id));
        }
    }

    static void validateCurrency(String currencyCode) {
        if (currencyCode != null && Currency.getAvailableCurrencies().stream().noneMatch(c -> c.getCurrencyCode().equals(currencyCode.toUpperCase()))) {
            throw new ValidationException(String.format("The currency ' %s ' is not available.", currencyCode));
        }

    }

}
