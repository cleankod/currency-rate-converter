package pl.cleankod.exchange.provider.conversion.nbp.client;

import pl.cleankod.exchange.provider.conversion.nbp.model.RateWrapper;

import java.util.HashMap;

public class ExchangeRatesNbpClientCachingWrapper implements ExchangeRatesNbpClient {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    private final HashMap<String, RateWrapper> localCache = new HashMap<>(16);

    public ExchangeRatesNbpClientCachingWrapper(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    /**
     * Normally this would be solved by annotating the method with a @Cacheable annotation which would use the parameters
     * as cache keys and rely on the cache manager implementation in a bean annotated with @EnableCaching.
     * <p>
     * Or to remain framework-agnostic we could directly use a caching mechanism of our choice, like Infinispan, Hazelcast, or Redis.
     * But for the purpose of this exercise a simple HashMap for local caching is enough.
     */
    @Override
    public RateWrapper fetch(String table, String currency) {
        return localCache.computeIfAbsent(table + "-" + currency,
                (key) -> exchangeRatesNbpClient.fetch(table, currency));
    }

}
