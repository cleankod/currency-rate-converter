package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.MoneyOperationFailedReason;
import pl.cleankod.util.domain.Result;

import java.util.Currency;

public interface ConvertAccountCurrencyService {
    Result<Account, MoneyOperationFailedReason> execute(Account account, Currency targetCurrency);
}
