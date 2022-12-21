package pl.cleankod.exchange.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.FailureReason;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionService {

    private final ExchangeRatesProviderPort exchangeRatesProvider;
    private static final Logger log = LoggerFactory.getLogger(CurrencyConversionService.class);

    public CurrencyConversionService(ExchangeRatesProviderPort exchangeRatesProvider) {
        this.exchangeRatesProvider = exchangeRatesProvider;
    }

    public Result<Money, Failure> convert(Money money, Currency targetCurrency) {
        Result<BigDecimal, Failure> result = exchangeRatesProvider.getExchangeRate(targetCurrency);
        if (result.isFail()){
            log.error(result.failValue().failureReason.toString() + " " + result.failValue().exception.getMessage()); // can be improved
            return Result.fail(result.failValue());
        }
        if (result.successfulValue() == null || result.successfulValue().doubleValue() <= 0d){
            log.error(result.failValue().failureReason.toString(),
                    "Exchange rate does not have a permissible value: " + result.successfulValue());
            return Result.fail(new Failure(FailureReason.EXCHANGE_RATE_NOT_APPLICABLE, null));
        }
        BigDecimal calculatedRate =
                money.amount().divide(result.successfulValue(),2,  RoundingMode.HALF_UP);
        return Result.successful(new Money(calculatedRate, targetCurrency));
    }

}
