package pl.cleankod.exchange.core.usecase;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super(String.format("User with id %s was not found.", id));
    }

    public UserNotFoundException(String number) {
        super(String.format("User with account number %s was not found.", number));
    }
}
