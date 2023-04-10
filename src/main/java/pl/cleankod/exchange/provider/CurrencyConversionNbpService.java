package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.NbpClientAdapter;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {

    private final NbpClientAdapter nbpClientAdapter;

    public CurrencyConversionNbpService(NbpClientAdapter nbpClientAdapter) {
        this.nbpClientAdapter = nbpClientAdapter;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = nbpClientAdapter.fetch(targetCurrency);
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
