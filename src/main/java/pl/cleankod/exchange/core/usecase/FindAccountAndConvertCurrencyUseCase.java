package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.ApplicationError;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.domain.Result;

import java.util.Currency;

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

    public Result<Account, ApplicationError> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> convert(account, targetCurrency))
                .orElse(Result.fail(new AccountNotFoundError(id)));
    }

    public Result<Account, ApplicationError> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> convert(account, targetCurrency))
                .orElse(Result.fail(new AccountNotFoundError(number)));
    }

    private Result<Account, ApplicationError> convert(Account account, Currency targetCurrency) {
        return convert(account.balance(), targetCurrency)
                .successMap(convertedBalance -> new Account(account.id(), account.number(), convertedBalance));
    }

    private Result<Money, ApplicationError> convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return Result.successful(money.convert(currencyConversionService, targetCurrency));
        }

        if (!money.currency().equals(targetCurrency)) {
            return Result.fail(new CurrencyConversionError(money.currency(), targetCurrency));
        }

        return Result.successful(money);
    }
}
