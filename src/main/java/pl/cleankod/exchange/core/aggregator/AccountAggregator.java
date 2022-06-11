package pl.cleankod.exchange.core.aggregator;

import pl.cleankod.exchange.core.dto.AccountDto;

import java.util.UUID;

public interface AccountAggregator {

    AccountDto findAccountById(UUID id, String currency);

    AccountDto findAccountByNumber(String number, String currency);

}
