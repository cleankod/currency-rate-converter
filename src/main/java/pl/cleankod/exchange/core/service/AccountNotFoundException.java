package pl.cleankod.exchange.core.service;

public class AccountNotFoundException extends RuntimeException {
    AccountNotFoundException() {
        super("Account not found");
    }
}
