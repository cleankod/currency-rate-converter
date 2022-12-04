package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.repository.AccountRepository;
import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.util.Currency;
import java.util.UUID;

public class FindAccountAndConvertCurrencyUseCase extends FindAccountUseCase {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                CurrencyConversionService currencyConversionService,
                                                Currency baseCurrency) {
        super(accountRepository);
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Result<Account, AccountRetrievalFailedReason> execute(UUID id, Currency targetCurrency) {
        return super.execute(id).fold(
                account -> execute(account, targetCurrency),
                Result::fail
        );
    }

    public Result<Account, AccountRetrievalFailedReason> execute(String number, Currency targetCurrency) {
        return super.execute(number).fold(
                account -> execute(account, targetCurrency),
                Result::fail
        );
    }

    private Result<Account, AccountRetrievalFailedReason> execute(Account account, Currency targetCurrency) {
        return convert(account.balance(), targetCurrency).fold(
                convertedValue -> Result.successful
                        (new Account(account.id(), account.number(), convertedValue)),
                Result::fail);
    }

    private Result<Money, AccountRetrievalFailedReason> convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            return Result.fail(AccountRetrievalFailedReason.CURRENCY_MISMATCH);
            //throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return Result.successful(money);
    }
}
