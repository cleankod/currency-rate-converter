package pl.cleankod.exchange.provider;

import feign.Param;
import feign.RequestLine;
import pl.cleankod.exchange.provider.model.RateWrapper;

public interface ExchangeRatesNbpClient {
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/{localDate}")
    RateWrapper fetch(@Param("table") String table, @Param("currency") String currency, @Param("localDate") String localDate);
}
