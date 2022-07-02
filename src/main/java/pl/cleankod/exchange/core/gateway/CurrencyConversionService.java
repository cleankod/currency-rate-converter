package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.MoneyOperationFailedReason;
import pl.cleankod.util.domain.Result;

import java.util.Currency;

public interface CurrencyConversionService {
    Result<Money, MoneyOperationFailedReason> convert(Money money, Currency targetCurrency);
}
