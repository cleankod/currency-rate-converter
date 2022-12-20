package pl.cleankod.exchange.adapter.provider.nbp;

import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;

import java.math.BigDecimal;
import java.util.Currency;

public class ExchangeRatesProvider implements ExchangeRatesProviderPort {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesProvider(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    // could possibly return a dedicated Rate domain object with a dedicated mapper
    // for the mapping between DTOs and domain objects
    @Cacheable("exchangeRates")
    //TODO: cacheing makes sense in conjunction with getExchangeRateByDate implementation
    public BigDecimal getExchangeRate(Currency targetCurrency){
        return exchangeRatesNbpClient
            .fetch("A", targetCurrency.getCurrencyCode()).rates().get(0).mid();
    }

}
