package pl.cleankod.exchange.provider;

import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.CircuitBreakerService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CircuitBreakerService circuitBreakerService;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        CircuitBreakerService circuitBreakerService) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.circuitBreakerService = circuitBreakerService;
    }

    @Override
    @Cacheable("money")
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, 2, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
