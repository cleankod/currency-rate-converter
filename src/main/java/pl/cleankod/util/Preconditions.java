package pl.cleankod.util;

import pl.cleankod.exchange.entrypoint.model.IncorrectFormatException;

import java.util.UUID;
import java.util.regex.Pattern;

public interface Preconditions {

    static final Pattern PATTERN =
            Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("Given value cannot be null");
        }
        return obj;
    }

    static void validateAccountNumber(String account) throws IllegalArgumentException {
        if (!PATTERN.matcher(account).matches()) {
            throw new IllegalArgumentException("The account number does not match the pattern: " + PATTERN);
        }
    }

    static UUID validateAndReturnUUID(String id) throws IncorrectFormatException {
        try {
           return UUID.fromString(id);
        } catch (IllegalArgumentException e){
            throw new IncorrectFormatException(IncorrectFormatException.INCORRECT_UUID_FORMAT);
        }
    }

}
