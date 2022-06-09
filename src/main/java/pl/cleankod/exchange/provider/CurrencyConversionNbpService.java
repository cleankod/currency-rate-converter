package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @CircuitBreaker(name = "NBP", fallbackMethod = "fallback")
    public RateWrapper.MidRate convert(Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        return rateWrapper.getMidRate();
    }

    public RateWrapper.MidRate fallback(Currency targetCurrency, Throwable e) {
        return new RateWrapper.MidRate(BigDecimal.ONE);
    }
}
