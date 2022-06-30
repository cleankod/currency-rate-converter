package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    public static final short SCALE = 3;
    public static final String EXCHANGE_TABLE = "A";

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch(EXCHANGE_TABLE, targetCurrency.getCurrencyCode());
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP).setScale(SCALE, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
