package pl.cleankod.exchange.config.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.exchange.core.cache.CacheService;
import pl.cleankod.exchange.core.cache.CacheServiceImpl;

@Configuration
public class CacheConfiguration {

    @Bean
    CacheService cacheService() {
        return new CacheServiceImpl();
    }

}
