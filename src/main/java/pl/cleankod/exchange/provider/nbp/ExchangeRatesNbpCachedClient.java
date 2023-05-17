package pl.cleankod.exchange.provider.nbp;

import org.ehcache.Cache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Optional;

public class ExchangeRatesNbpCachedClient implements ExchangeRatesNbpClient {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache<String, RateWrapper> cache;

    public ExchangeRatesNbpCachedClient(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        Cache<String, RateWrapper> cache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = cache;
    }

    @Override
    public RateWrapper fetch(String table, String currency) {
        return Optional.ofNullable(cache.get(currency))
                .orElseGet(() -> {
                    RateWrapper exchangeRate = exchangeRatesNbpClient.fetch(table, currency);
                    cache.put(currency, exchangeRate);
                    return exchangeRate;
                });
    }
}
