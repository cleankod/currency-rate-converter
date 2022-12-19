package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CircuitBreaker circuitBreaker;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, CircuitBreaker circuitBreaker) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = circuitBreaker.decorateSupplier( () -> exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode())).get();
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
