package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.entrypoint.model.ErrorResponse;
import pl.cleankod.exchange.entrypoint.util.Result;

import java.util.Currency;

public interface CurrencyConversionService {
    Result<Money, ErrorResponse> convert(Money money, Currency targetCurrency);
}
