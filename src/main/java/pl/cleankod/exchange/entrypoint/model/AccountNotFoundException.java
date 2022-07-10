package pl.cleankod.exchange.entrypoint.model;

public class AccountNotFoundException extends Exception{

    public static final String ACCOUNT_NOT_FOUND = "Requested account does not exist.";

    public AccountNotFoundException(String message) {
        super(message);
    }
}
