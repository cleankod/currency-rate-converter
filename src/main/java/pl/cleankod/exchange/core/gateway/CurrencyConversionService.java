package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;

import java.util.Currency;

public interface CurrencyConversionService {
    Money convert(Money money, Currency targetCurrency);
}
