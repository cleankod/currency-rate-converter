package pl.cleankod.exchange.entrypoint.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String account) {
        super(String.format("Account %s was not found.", account));
    }
}
