package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.ExchangeRate;

import java.util.Currency;
import java.util.Optional;

public interface ExchangeRateService {
    Optional<ExchangeRate> getExchangeRate(Currency source, Currency target);
}
