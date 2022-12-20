package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.port.AccountRepositoryPort;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;

import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    private final AccountRepositoryPort accountRepositoryAdapter;

    public AccountService(CurrencyConversionService currencyConversionService, Currency baseCurrency, AccountRepositoryPort accountRepositoryAdapter) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
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
                                convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );

    }

    public Optional<Account> getByNumber(String number, Optional<String> targetCurrency) {
        return get(Account.Number.of(number))
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );
    }

    private Money convert(Money money, Currency targetCurrency) {

        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        // Suggestion (maybe): refactor since if
        // baseCurrency = "PLN", targetCurrency = "PLN" and if money.currency() = "EUR",
        // an Exception is thrown --> the inverse of the opposite exchange rate could be used
        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
