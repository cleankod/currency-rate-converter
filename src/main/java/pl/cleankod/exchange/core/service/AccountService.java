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

    public Optional<Account> get(Account.Id id) {
        return accountRepositoryAdapter.find(id);
    }

    public Optional<Account> get(Account.Number number) {
        return accountRepositoryAdapter.find(number);
    }

    public Optional<Account> getById(String id, Optional<String> targetCurrency) {
        return get(Account.Id.of(id))
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                currencyConversionService
                                        .convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );

    }

    public Optional<Account> getByNumber(String number, Optional<String> targetCurrency) {
        return get(Account.Number.of(number))
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                currencyConversionService
                                        .convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );
    }

}
