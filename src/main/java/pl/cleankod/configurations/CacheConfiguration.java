package pl.cleankod.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.exchange.core.gateway.CacheService;
import pl.cleankod.exchange.provider.CacheServiceImpl;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Optional;

@Configuration
public class CacheConfiguration {
    private static final int RATE_TIME_TO_LIVE = 3600;
    private static final int CLEANUP_TIMER = 600;
    private static final int CACHE_MAX_ITEMS = 100;


    @Bean
    CacheService<String, Optional<RateWrapper>> cacheServiceForRateWrapper() {
        return new CacheServiceImpl<String, Optional<RateWrapper>>(
                RATE_TIME_TO_LIVE,
                CLEANUP_TIMER,
                CACHE_MAX_ITEMS);
    }
}
