package pl.cleankod.exchange.provider.nbp;

import feign.Param;
import feign.RequestLine;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public interface ExchangeRatesNbpClient {
    
    //TO be improved: add date as parameter and handle in CurrencyConversionNbpService
    // a fetchRates method to fetch rates for a given date or today's date
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/2022-02-08")
    RateWrapper fetch(@Param("table") String table,
                      @Param("currency") String currency);
}