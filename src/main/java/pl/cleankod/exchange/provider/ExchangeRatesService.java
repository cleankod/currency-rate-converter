package pl.cleankod.exchange.provider;

import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.Currency;

public interface ExchangeRatesService {
    @Cacheable(value = "exchangeRate")
    BigDecimal getExchangeRate(Currency targetCurrency);
}
