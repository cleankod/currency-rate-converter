package pl.cleankod.exchange.provider.nbp.service;

import lombok.extern.slf4j.Slf4j;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.exception.NbpApiException;
import pl.cleankod.exchange.entrypoint.model.ErrorResponse;
import pl.cleankod.exchange.entrypoint.util.Result;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Slf4j
public class CurrencyConversionNbpService implements CurrencyConversionService {

    private final ExchangeRatesNbpClientImpl exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClientImpl exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Result<Money, ErrorResponse> convert(Money money, Currency targetCurrency) {
        try{
            RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
            BigDecimal midRate = rateWrapper.rates().get(0).mid();
            BigDecimal calculatedRate = money.amount().divide(midRate, 2, RoundingMode.HALF_EVEN);
            return Result.successful(new Money(calculatedRate, targetCurrency));
        } catch (NbpApiException exception) {
            log.error(String.format("Could not fetch rates for given currency %s", targetCurrency));
            return Result.fail(new ErrorResponse(exception.getMessage(), new CurrencyConversionException(money.currency(), targetCurrency)));

        }


    }
}
