package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.ExchangeRate;
import pl.cleankod.exchange.core.gateway.ExchangeRateService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

public class NbpExchangeRateService implements ExchangeRateService {
    private static Currency PLN = Currency.getInstance("PLN");

    private final ExchangeRatesNbpClient nbpClient;

    public NbpExchangeRateService(ExchangeRatesNbpClient nbpClient) {
        this.nbpClient = nbpClient;
    }

    @Override
    public Optional<ExchangeRate> getExchangeRate(Currency source, Currency target) {
        if (source.equals(PLN)) {
            BigDecimal rate = getExchangeRateFromPlnTo(target);
            return Optional.of(ExchangeRate.of(target, PLN, rate).inverse());
        }

        if (target.equals(PLN)) {
            BigDecimal rate = getExchangeRateFromPlnTo(source);
            return Optional.of(ExchangeRate.of(source, PLN, rate));
        }

        return Optional.empty();
    }

    private BigDecimal getExchangeRateFromPlnTo(Currency currency) {
        RateWrapper rateWrapper = nbpClient.fetch("A", currency.getCurrencyCode());
        return rateWrapper.rates().get(0).mid();
    }
}
