package pl.cleankod.exchange.core.dto;

import pl.cleankod.exchange.core.domain.Account;

public record AccountDTO(String id, String number, MoneyDTO balance) {

    public static AccountDTO from(Account account) {
        return new AccountDTO(String.valueOf(account.id().value()),
                account.number().value(), MoneyDTO.from(account.balance()));
    }
}
