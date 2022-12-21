package pl.cleankod.exchange.adapter.provider.nbp.client;

import feign.hystrix.FallbackFactory;
import pl.cleankod.exchange.adapter.provider.nbp.dto.Rate;
import pl.cleankod.exchange.adapter.provider.nbp.dto.RateWrapperDto;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRatesNbpClientFallbackFactory {

    // This instance will be invoked if there are errors of any kind.
    public static final double fallBackValue = 4.5274d;
    public static FallbackFactory<ExchangeRatesNbpClient> fallbackFactory = cause -> (table, currency) -> {
        if ("EUR".equals(currency)){
            return new RateWrapperDto(
                    table, currency, "EUR",
                    List.of(new Rate("026/A/NBP/2022", "2022-02-08",  BigDecimal.valueOf(fallBackValue)))
            );
        }
        throw new RuntimeException();
    };


}
