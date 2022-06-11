package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Money;

import java.util.Currency;

public interface CurrencyConversionService {
    Money convert(Money money, Currency targetCurrency);

}
