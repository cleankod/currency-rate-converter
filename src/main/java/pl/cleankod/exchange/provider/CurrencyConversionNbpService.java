package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.Rate;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Optional<Money> convert(Money money, Currency targetCurrency) {
        return Optional.ofNullable(exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode()))
                .map(RateWrapper::rates)
                .map(rates -> rates.get(0))
                .map(Rate::mid)
                .map(midRate -> money.amount().divide(midRate, 2, RoundingMode.HALF_UP))
                .map(calculatedRate -> new Money(calculatedRate, targetCurrency));
    }
}
