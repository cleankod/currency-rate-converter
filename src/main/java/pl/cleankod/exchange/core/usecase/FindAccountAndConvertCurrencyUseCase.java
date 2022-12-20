package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.port.AccountRepositoryPort;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepositoryPort accountRepositoryAdapter;
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public FindAccountAndConvertCurrencyUseCase(AccountRepositoryPort accountRepositoryAdapter,
                                                CurrencyConversionService currencyConversionService,
                                                Currency baseCurrency) {
        this.accountRepositoryAdapter = accountRepositoryAdapter;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepositoryAdapter.find(id)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepositoryAdapter.find(number)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        // TODO: (maybe) refactor since if baseCurrency = "PLN", targetCurrency = "PLN" and if money.currency() = "EUR", an Exception is thrown
        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
