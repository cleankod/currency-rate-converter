package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import static pl.cleankod.exchange.configuration.CacheConfig.RATES_CACHE;

public interface ExchangeRatesNbpClient {
    @Cacheable(value = RATES_CACHE, keyGenerator = "exchangeRatesCacheKeyGenerator")
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency);
}
