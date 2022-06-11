package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import feign.Response;

public interface ExchangeRatesNbpClient {

    @RequestLine("GET /exchangerates/rates/{table}/{currency}/{date}")
    Response fetch(@Param("table") String table, @Param("currency") String currency, @Param("date") String date);

}
