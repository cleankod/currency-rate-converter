package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final FindAccountUseCase findAccountUseCase;

    private final CurrencyConversionService currencyConversionService;

    public FindAccountAndConvertCurrencyUseCase(FindAccountUseCase findAccountUseCase,
                                                CurrencyConversionService currencyConversionService) {
        this.findAccountUseCase = findAccountUseCase;
        this.currencyConversionService = currencyConversionService;
    }

    public Optional<Account> execute(Account.Id id, String targetCurrency) {

        var account = findAccountUseCase.execute(id);

        return Optional.ofNullable(targetCurrency)
                .map(Currency::getInstance)
                .map(currency -> convertAccount(account, currency))
                .orElse(account);
    }

    public Optional<Account> execute(Account.Number number, String targetCurrency) {

        var account = findAccountUseCase.execute(number);

        return Optional.ofNullable(targetCurrency)
                .map(Currency::getInstance)
                .map(currency -> convertAccount(account, currency))
                .orElse(account);
    }

    private Optional<Account> convertAccount(Optional<Account> sourceAccount, Currency targetCurrency) {
        return sourceAccount.map(account ->
                new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    private Money convert(Money money, Currency targetCurrency) {
        return money.convert(currencyConversionService, targetCurrency)
                .orElseThrow(() -> new CurrencyConversionException(money.currency(), targetCurrency));
    }
}
