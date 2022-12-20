package pl.cleankod.exchange.adapter.provider.nbp.client;

import feign.Param;
import feign.RequestLine;
import pl.cleankod.exchange.adapter.provider.nbp.dto.RateWrapperDto;


public interface ExchangeRatesNbpClient {
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    RateWrapperDto fetch(@Param("table") String table, @Param("currency") String currency);
}

