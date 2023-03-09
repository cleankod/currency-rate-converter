package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Currency;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.MoneyRoundingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }


    //we should correct the code, because if account currency != PLN then, conversion is wrong, because NBP provides rates for PLN
    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.name());
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate,10, RoundingMode.HALF_EVEN);
        return new Money(MoneyRoundingUtil.roundHalfEven(calculatedRate), targetCurrency);
    }
}
