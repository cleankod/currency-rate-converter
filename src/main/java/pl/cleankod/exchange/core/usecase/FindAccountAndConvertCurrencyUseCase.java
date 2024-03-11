package pl.cleankod.exchange.core.usecase;

import static pl.cleankod.util.domain.Failures.ACCOUNT_NOT_FOUND;
import static pl.cleankod.util.domain.Failures.CURRENCY_CONVERSION_EXCEPTION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Optional;
import pl.cleankod.util.domain.Failure;
import pl.cleankod.util.domain.Result;

public class FindAccountAndConvertCurrencyUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindAccountAndConvertCurrencyUseCase.class);

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

    public Result<Account> execute(Account.Id id, Currency targetCurrency) {
        return processAccount(accountRepository.find(id), targetCurrency);
    }

    public Result<Account> execute(Account.Number number, Currency targetCurrency) {
        return processAccount(accountRepository.find(number), targetCurrency);
    }

    private Result processAccount(Optional<Account> accountOptional, Currency targetCurrency) {
        return accountOptional.map(account -> {
                    var conv = convert(account.balance(), targetCurrency);
                    if (conv.isOk()) {
                        return Result.ok(new Account(account.id(), account.number(), (Money) conv.unwrap()));
                    }
                    return conv;
                }
        ).orElse(Result.failure(new Failure(ACCOUNT_NOT_FOUND, "Account not found")));
    }

    private Result convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            LOGGER.info("Converting money from {} to {} successfully", money.currency(), targetCurrency);
            return Result.ok(currencyConversionService.convert(money, targetCurrency));
        }

        if (!money.currency().equals(targetCurrency)) {
            LOGGER.error("Cannot convert currency from %s to %s.", money.currency(), targetCurrency);
            return Result.failure(new Failure(CURRENCY_CONVERSION_EXCEPTION,
                    String.format("Cannot convert currency from %s to %s.", money.currency(), targetCurrency),
                    new CurrencyConversionException(money.currency(), targetCurrency)));
        }

        return Result.ok(money);
    }
}
