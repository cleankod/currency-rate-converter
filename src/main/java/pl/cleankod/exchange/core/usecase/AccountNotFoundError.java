package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.ApplicationError;

public class AccountNotFoundError extends ApplicationError {

    public AccountNotFoundError(Account.Id id) {
        this(String.format("The account with id %s was not found.", id.value()));
    }

    public AccountNotFoundError(Account.Number number) {
        this(String.format("The account with number %s was not found.", number.value()));
    }

    private AccountNotFoundError(String message) {
        super(message, 404);
    }
}
