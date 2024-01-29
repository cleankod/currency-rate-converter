package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.entrypoint.model.Result;

import java.util.Currency;

public interface CurrencyConversionService {
    
    Result<Money, String> convert(Money money, Currency targetCurrency);
}