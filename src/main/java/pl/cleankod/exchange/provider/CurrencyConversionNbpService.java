package pl.cleankod.exchange.provider;

import io.github.resilience4j.cache.Cache;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache<String, RateWrapper> cache;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache<String, RateWrapper> cache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = cache;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        var rateWrapper = Cache.decorateSupplier(
                        cache, () -> exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode())
                )
                .apply(targetCurrency.getCurrencyCode());
        var midRate = rateWrapper.rates().get(0).mid();

        return money.exchangeTo(midRate, targetCurrency);
    }
}
