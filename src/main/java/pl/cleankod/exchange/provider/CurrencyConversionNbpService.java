package pl.cleankod.exchange.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.exceptions.BaseException;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClientWrapper;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pl.cleankod.exchange.exceptions.BaseException.ErrorCode.THIRD_PARTY_CLIENT;

@Component
public class CurrencyConversionNbpService implements CurrencyConversionService {

    private static final int DIVISION_SCALE = 2;
    private final ExchangeRatesNbpClientWrapper exchangeRatesNbpClient;

    @Autowired
    public CurrencyConversionNbpService(ExchangeRatesNbpClientWrapper exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {

        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());

        if (rateWrapper == null || rateWrapper.rates() == null || rateWrapper.rates().isEmpty()) {
            String message = String.format("No rates found for currency %s", targetCurrency.getCurrencyCode());
            throw new BaseException(message, THIRD_PARTY_CLIENT, INTERNAL_SERVER_ERROR);
        }

        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, DIVISION_SCALE, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
