package pl.cleankod.exchange.core.port;

import java.math.BigDecimal;
import java.util.Currency;

public interface ExchangeRatesProviderPort {
    BigDecimal getExchangeRate(Currency targetCurrency);

}
