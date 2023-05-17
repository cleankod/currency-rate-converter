package pl.cleankod.exchange.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class CacheConfig {

    public static final String EXCHANGE_RATE_CACHE_NAME = "exchangeRates";

    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        return cacheManager;
    }

    @Bean
    public Cache<String, RateWrapper> exchangeRateCache(CacheManager cacheManager) {
        CacheConfiguration<String, RateWrapper> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, RateWrapper.class, ResourcePoolsBuilder.heap(100))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.of(1, ChronoUnit.HOURS)))
                .build();

        return cacheManager.createCache(EXCHANGE_RATE_CACHE_NAME, cacheConfiguration);
    }
}
