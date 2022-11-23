package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Currency baseCurrency;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Currency baseCurrency) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        if (targetCurrency.equals(money.currency())) {
            return money;
        }

        BigDecimal targetMidRate = baseCurrency.equals(targetCurrency) ? BigDecimal.valueOf(1L) : getExchangeRate(targetCurrency);

        BigDecimal convertedAmount;
        if (baseCurrency.equals(money.currency())) {
            convertedAmount = money.amount().divide(targetMidRate, 2, RoundingMode.HALF_EVEN);
            return new Money(convertedAmount, targetCurrency);
        }

        BigDecimal sourceMidRate = getExchangeRate(money.currency());
        BigDecimal calculatedMidRate = sourceMidRate.divide(targetMidRate, RoundingMode.HALF_EVEN);
        convertedAmount = money.amount().multiply(calculatedMidRate).setScale(2, RoundingMode.HALF_EVEN);

        return new Money(convertedAmount, targetCurrency);
    }

    private BigDecimal getExchangeRate(Currency currency) {
        RateWrapper targetRateWrapper = exchangeRatesNbpClient.fetch("A", currency.getCurrencyCode());
        return targetRateWrapper.rates().get(0).mid();
    }

    public Currency getBaseCurrency() {
        return this.baseCurrency;
    }
}
