package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.util.domain.AppErrors;
import pl.cleankod.util.domain.Either;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Either<? extends AppErrors.Base, BigDecimal> getRate(Currency currency, Currency targetCurrency) {
        //TODO: support conversion through PLN (e.g. EUR -> PLN -> GPB) ?
        String targetCurrencyCode = targetCurrency.getCurrencyCode();
        if ("PLN".equals(currency.getCurrencyCode()) && !"PLN".equals(targetCurrencyCode)) {
            return getRatePlnToCurrency(targetCurrency);
        } else {
            return AppErrors.eitherConversion(currency, targetCurrency);
        }
    }

    private Either<AppErrors.Provider, BigDecimal> getRatePlnToCurrency(Currency targetCurrency) {
        return getRateCurrencyToPln(targetCurrency)
                .mapRight(rate -> BigDecimal.ONE.divide(rate, 2, RoundingMode.HALF_DOWN));
    }

    private Either<AppErrors.Provider, BigDecimal> getRateCurrencyToPln(Currency currency) {
        return Either.tryCatch(() -> exchangeRatesNbpClient.fetch("A", currency.getCurrencyCode()))
                .mapBi(
                        throwable -> AppErrors.provider(throwable.getMessage()),
                        rateWrapper -> rateWrapper.rates().get(0).mid()
                );
    }

}
