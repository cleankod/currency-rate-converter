package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache cache;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache cache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = cache;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        BigDecimal midRate = this.getRateFromProviderOrCache(targetCurrency).rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.FLOOR);
        return new Money(calculatedRate, targetCurrency);
    }

    private RateWrapper getRateFromProviderOrCache(Currency targetCurrency) {
        RateWrapper rateWrapper;
        Optional<RateWrapper> optionalRateWrapper = cache.get(targetCurrency.getDisplayName());
        if (optionalRateWrapper.isPresent() &&
                //check if the correct day of the exchange is saved in the cache
                this.getDayForExchange().equals(LocalDate.parse(optionalRateWrapper.get().rates().get(0).effectiveDate()))
        ) {
            rateWrapper = optionalRateWrapper.get();
        } else {
            rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode(), getDayForExchange().toString());
            cache.put(targetCurrency.getDisplayName(), rateWrapper);
        }

        return rateWrapper;
    }

    private LocalDate getDayForExchange() {
        LocalDate dateOfExchange;
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY -> dateOfExchange = LocalDate.now().minusDays(3);
            case SUNDAY -> dateOfExchange = LocalDate.now().minusDays(2);
            default -> dateOfExchange = LocalDate.now().minusDays(1);
        }
        return dateOfExchange;
    }
}
