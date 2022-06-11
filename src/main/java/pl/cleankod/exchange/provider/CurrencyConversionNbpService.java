package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Currency baseCurrency;
    private final RateWrapper.MidRate baseCurrencyMidRate;
    private final Currency PL_CURRENCY = Currency.getInstance("PLN");
    private final String targetNbpTable = "A";

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.baseCurrencyMidRate = new RateWrapper.MidRate(BigDecimal.ONE, baseCurrency);
    }

    @Override
    @CircuitBreaker(name = "NBP", fallbackMethod = "fallback")
    public RateWrapper.MidRate getMidRate(Currency targetCurrency) {
        //this api is really weird, because we can not ask for a default currency (PLN). This is why I made this hacky if below
        if (baseCurrency == targetCurrency) {
            return baseCurrencyMidRate;
        }

        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch(targetNbpTable, targetCurrency.getCurrencyCode());
        return rateWrapper.getMidRate();
    }

    private RateWrapper.MidRate fallback(Currency targetCurrency, Throwable e) {
        return new RateWrapper.MidRate(BigDecimal.ONE, PL_CURRENCY);
    }
}
