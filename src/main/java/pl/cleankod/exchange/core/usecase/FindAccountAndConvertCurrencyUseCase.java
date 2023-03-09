package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Currency;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                CurrencyConversionService currencyConversionService,
                                                Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (baseCurrency != targetCurrency) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        if (money.currency() != targetCurrency) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
