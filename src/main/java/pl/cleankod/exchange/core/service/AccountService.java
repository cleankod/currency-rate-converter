package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;

import pl.cleankod.exchange.core.port.AccountRepositoryPort;


import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final CurrencyConversionService currencyConversionService;

    private final AccountRepositoryPort accountRepositoryAdapter;

    public AccountService(CurrencyConversionService currencyConversionService, AccountRepositoryPort accountRepositoryAdapter) {
        this.currencyConversionService = currencyConversionService;
        this.accountRepositoryAdapter = accountRepositoryAdapter;
    }

    private Optional<Account> get(Account.Id id) {
        return accountRepositoryAdapter.find(id);
    }

    private Optional<Account> get(Account.Number number) {
        return accountRepositoryAdapter.find(number);
    }

    public Optional<Account> get(Account.Id id, Optional<String> targetCurrency) {
        return get(id)
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                currencyConversionService
                                        .convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );

    }

    public Optional<Account> get(Account.Number number, Optional<String> targetCurrency) {
        return get(number)
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                currencyConversionService
                                        .convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );
    }

}
