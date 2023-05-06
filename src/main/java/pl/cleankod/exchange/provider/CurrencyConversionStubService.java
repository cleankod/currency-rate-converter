package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.domain.AppErrors;
import pl.cleankod.util.Either;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionStubService implements CurrencyConversionService {

    private static final Either<? extends AppErrors.Base, BigDecimal> PLN_TO_EUR_RATE = Either.right(BigDecimal.valueOf(0.22d));
    private static final Either<? extends AppErrors.Base, BigDecimal> EUR_TO_PLN_RATE = Either.right(BigDecimal.valueOf(4.58d));

    @Override
    public Either<? extends AppErrors.Base, BigDecimal> getRate(Currency currency, Currency targetCurrency) {
        if ("PLN".equals(currency.getCurrencyCode()) && "EUR".equals(targetCurrency.getCurrencyCode())) {
            return PLN_TO_EUR_RATE;
        } else if ("EUR".equals(currency.getCurrencyCode()) && "PLN".equals(targetCurrency.getCurrencyCode())) {
            return EUR_TO_PLN_RATE;
        } else {
            return AppErrors.eitherConversion(currency, targetCurrency);
        }
    }
}
