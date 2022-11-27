package pl.cleankod.exchange.provider.nbp;

import java.time.LocalDate;

import feign.Param;
import feign.RequestLine;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public interface ExchangeRatesNbpClient {

    @Cacheable(cacheNames = "nbpApiResponse", unless = "#result == null")
    @RequestLine("GET /exchangerates/rates/{table}/{currency}/{startDate}/{endDate}")
    RateWrapper fetch(
        @Param("table") String table,
        @Param("currency") String currency,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
