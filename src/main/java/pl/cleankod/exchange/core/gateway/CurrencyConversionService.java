package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public interface CurrencyConversionService {
    RateWrapper.MidRate getMidRate(
            Currency targetCurrency,
            Currency sourceCurrency
    );
}
