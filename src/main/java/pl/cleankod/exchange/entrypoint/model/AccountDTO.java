package pl.cleankod.exchange.entrypoint.model;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;

public record AccountDTO(String id, String number, Money Balance) {
    public static AccountDTO from(Account account) {
        return new AccountDTO(account.id().value().toString(), account.number().value(), account.balance());
    }
}
