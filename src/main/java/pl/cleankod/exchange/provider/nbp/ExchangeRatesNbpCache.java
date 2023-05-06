package pl.cleankod.exchange.provider.nbp;

import io.github.resilience4j.cache.Cache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

public class ExchangeRatesNbpCache {

    public record Key(String table, String currency) {
    }

    public static Cache create() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        Cache<Key, RateWrapper> cache = Cache.of(cacheManager
                .createCache("exchangeRatesNbpCache",
                        new MutableConfiguration<Key, RateWrapper>()
                                .setTypes(Key.class, RateWrapper.class)
                                .setStoreByValue(false)
                                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE))
                ));
        return cache;
    }

}
