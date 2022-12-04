package pl.cleankod.exchange.core.usecase.dto;

import pl.cleankod.exchange.core.domain.Account;

import java.io.Serializable;

public record AccountDto(String id, String number, MoneyDto balance) implements Serializable {
    public static AccountDto from(Account account) {
        return new AccountDto(String.valueOf(account.id().value()),
                account.number().value(), MoneyDto.from(account.balance()
        ));
    }
}