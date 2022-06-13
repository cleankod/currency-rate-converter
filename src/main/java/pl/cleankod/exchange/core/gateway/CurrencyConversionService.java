package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.rate.MidRate;

import java.util.Currency;

public interface CurrencyConversionService {
    MidRate getMidRate(Currency targetCurrency);
}
