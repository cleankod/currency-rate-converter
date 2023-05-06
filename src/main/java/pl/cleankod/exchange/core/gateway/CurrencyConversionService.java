package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.util.domain.AppErrors;
import pl.cleankod.util.Either;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public interface CurrencyConversionService {

    default Either<? extends AppErrors.Base, Money> convert(Money money, Currency targetCurrency) {
        if (money.currency().equals(targetCurrency)) {
            return Either.right(money);
        }
        if (money.amount().compareTo(BigDecimal.ZERO) == 0) {
            return Either.right(new Money(BigDecimal.ZERO, targetCurrency));
        }
        return getRate(money.currency(), targetCurrency)
                .mapRight(rate -> {
                    BigDecimal convertedAmount = money.amount().multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
                    return new Money(convertedAmount, targetCurrency);
                });
    }

    Either<? extends AppErrors.Base, BigDecimal> getRate(Currency currency, Currency targetCurrency);
}
