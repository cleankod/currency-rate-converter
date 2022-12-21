package pl.cleankod.exchange.core.port;

import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.util.Currency;

public interface ExchangeRatesProviderPort {
    Result<BigDecimal, Failure> getExchangeRate(Currency targetCurrency);

}
