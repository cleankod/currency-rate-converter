package pl.cleankod.exchange.provider.nbp;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import javax.cache.Cache;

public class CachingExchangeRatesNbpClient implements ExchangeRatesNbpClient {
    private final ExchangeRatesNbpClient delegate;
    private final Cache<String, RateWrapper> cache;

    public CachingExchangeRatesNbpClient(ExchangeRatesNbpClient delegate, Cache<String, RateWrapper> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public RateWrapper fetch(String table, String currency) {
        String key = String.format("%s-%s", table, currency);

        if (!cache.containsKey(key)) {
            RateWrapper rateWrapper = delegate.fetch(table, currency);
            cache.putIfAbsent(key, rateWrapper);
        }

        return cache.get(key);
    }
}
