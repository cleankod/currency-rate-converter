package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

public record Account(UUID id, String number, Money balance) implements Serializable {

    private static final Pattern PATTERN =
            Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

    public Account(UUID id, String number, Money balance) {
        Preconditions.requireNonNull(id);
        this.id = id;
        Preconditions.requireNonNull(number);
        if (!PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("The account number does not match the pattern: " + PATTERN);
        }
        this.number = number;
        this.balance = balance;
    }
}
