package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @CircuitBreaker(name = "NBP", fallbackMethod = "fallback")
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        RateWrapper.MidRate midRate = rateWrapper.getMidRate();
        return money.convertTo(targetCurrency, midRate);
    }

    public Money fallback(Money money, Currency targetCurrency, Throwable e) {
        return money;
    }
}
