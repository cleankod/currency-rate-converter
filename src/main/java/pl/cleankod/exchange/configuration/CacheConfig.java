package pl.cleankod.exchange.configuration;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import pl.cleankod.exchange.cache.ExchangeRatesCacheKeyGenerator;

@Configuration
public class CacheConfig {

    public static final String RATES_CACHE = "rates";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(RATES_CACHE);
    }

    @Bean
    public KeyGenerator exchangeRatesCacheKeyGenerator() {
        return new ExchangeRatesCacheKeyGenerator();
    }


    @Scheduled(fixedRate = 60 * 60 * 1000) // every 60 minutes
    public void evictCache() {
        Cache ratesCache = cacheManager().getCache("rates");
        if (null != ratesCache) {
            ratesCache.clear();
        }

    }
}
