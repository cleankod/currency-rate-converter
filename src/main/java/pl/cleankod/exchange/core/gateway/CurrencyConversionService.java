package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;

import java.util.Currency;
import java.util.Optional;

public interface CurrencyConversionService {
    Optional<Money> convert(Money money, Currency targetCurrency);
}
