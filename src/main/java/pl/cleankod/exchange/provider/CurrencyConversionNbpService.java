package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        var rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        var midRate = rateWrapper.rates().get(0).mid();
        var calculatedRate = money.amount()
                .divide(midRate, RoundingMode.HALF_EVEN);

        return new Money(calculatedRate, targetCurrency);
    }
}
