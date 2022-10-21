package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.NPBCache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private NPBCache cache;
    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = new NPBCache();
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = getRateWrapper(targetCurrency);
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }

    private RateWrapper getRateWrapper(Currency targetCurrency) {
        RateWrapper rateWrapper = cache.get(targetCurrency);

        if (Objects.isNull(rateWrapper)) {
            rateWrapper = addMissObjectToCacheAndReturnIt(targetCurrency);
        }

        return new RateWrapper(rateWrapper.table(), rateWrapper.currency(), rateWrapper.code(), rateWrapper.rates());
    }

    private RateWrapper addMissObjectToCacheAndReturnIt(Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        cache.put(targetCurrency, rateWrapper);
        return rateWrapper;
    }
}
