package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.WholeMoney;

import java.util.Currency;

public interface CurrencyConversionService {
    WholeMoney convertAndRoundToWhole(Money money, Currency targetCurrency);
}
