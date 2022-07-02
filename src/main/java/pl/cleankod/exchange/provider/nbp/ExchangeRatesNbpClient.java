package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public interface ExchangeRatesNbpClient {
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    @CircuitBreaker(name = "ExchangeRatesNbpClientfetch", fallbackMethod = "fallback")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency);

    static RateWrapper fallback(String table, String currency, Throwable throwable) {
        throw new FetchFallbackInvokedException("Fetch call failed", throwable);
    }
}
