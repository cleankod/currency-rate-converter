package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import org.springframework.cache.annotation.CachePut;
import pl.cleankod.util.nbp.model.RateWrapper;
import io.github.resilience4j.retry.annotation.Retry;

@Retry(name = "NbpApiRetry")
public interface ExchangeRatesNbpClient {

    @CachePut("exchangeRates")
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency);
}
