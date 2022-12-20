package pl.cleankod.exchange.adapter.provider.nbp;

import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.math.BigDecimal;
import java.util.Currency;

public class ExchangeRatesProvider implements ExchangeRatesProviderPort {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesProvider(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    // could possibly return a dedicated Rate domain object with a dedicated mapper
    // for the mapping between DTOs and domain objects
    public BigDecimal getExchangeRate(Currency targetCurrency){
        return exchangeRatesNbpClient
            .fetch("A", targetCurrency.getCurrencyCode()).rates().get(0).mid();
    }

}
