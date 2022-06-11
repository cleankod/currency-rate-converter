package pl.cleankod.exchange.core.mapper;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.dto.AccountDto;
import pl.cleankod.exchange.core.dto.MoneyDto;

public class AccountMapper {

    public AccountDto toDto(final Account entity, final Money money) {
        return new AccountDto(entity.id().value(), entity.number().value(),
                MoneyDto.of(money.amount(), money.currency()));
    }

}
