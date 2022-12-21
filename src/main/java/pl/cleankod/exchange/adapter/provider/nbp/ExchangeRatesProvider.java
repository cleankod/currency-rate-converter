package pl.cleankod.exchange.adapter.provider.nbp;

//import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.FailureReason;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.util.Currency;

public class ExchangeRatesProvider implements ExchangeRatesProviderPort {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesProvider(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    // could possibly return a dedicated Rate domain object with a dedicated mapper
    // for the mapping between DTOs and domain objects
    //@Cacheable("exchangeRates")
    //TODO: caching makes sense in conjunction with getExchangeRateByDate implementation
    public Result<BigDecimal, Failure> getExchangeRate(Currency targetCurrency){
        try {
            BigDecimal exchangeRate =
                    exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode()).rates().get(0).mid();
            return Result.successful(exchangeRate);
        } catch (Exception ex) {
            return Result.fail(new Failure(FailureReason.RETRIEVING_EXCHANGE_RATE_ISSUE, ex));
        }
    }

}
