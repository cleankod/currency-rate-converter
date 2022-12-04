package pl.cleankod.exchange.provider.nbp.client;

import feign.Param;
import feign.RequestLine;
import pl.cleankod.exchange.provider.nbp.domain.RateWrapper;

public interface ExchangeRatesNbpClient {
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-12-02")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency);
}
