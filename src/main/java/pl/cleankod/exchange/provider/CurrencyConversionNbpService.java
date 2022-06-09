package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final RateWrapper.MidRate DEFAULT_MID_RATE = new RateWrapper.MidRate(BigDecimal.ONE);

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @CircuitBreaker(name = "NBP", fallbackMethod = "fallback")
    public RateWrapper.MidRate getMidRate(Currency targetCurrency, Currency baseCurrency) {
        //this api is really weird, because we can not ask for a default currency (PLN). This is why I made this hacky if below
        if (baseCurrency == targetCurrency) {
            return DEFAULT_MID_RATE;
        }

        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        return rateWrapper.getMidRate();
    }

    private RateWrapper.MidRate fallback(Currency targetCurrency, Currency baseCurrency, Throwable e) {
        return new RateWrapper.MidRate(BigDecimal.ONE);
    }
}
