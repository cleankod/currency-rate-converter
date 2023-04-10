package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public interface ExchangeRatesNbpClient {
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    @Cacheable("exchangeRate")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency);
}
