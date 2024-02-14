package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exceptions.RateRetrievalException;

import java.util.Currency;

public interface CurrencyConversionService {
    Money convert(Money money, Currency targetCurrency) throws RateRetrievalException;
}
