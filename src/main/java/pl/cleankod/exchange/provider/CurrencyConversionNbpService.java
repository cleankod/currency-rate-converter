package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.StubCurrencyConversionModel;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CurrencyConversionStubService currencyConversionStubService;

    private final Currency baseCurrency = Currency.getInstance("PLN");

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,CurrencyConversionStubService currencyConversionStubService) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.currencyConversionStubService = currencyConversionStubService;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {

        //converting from PLN ACCOUNT to other currency
        if(money.currency().getCurrencyCode().equalsIgnoreCase(baseCurrency.getCurrencyCode())) {

            RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
            BigDecimal midRate = rateWrapper.rates().get(0).mid().setScale(2, RoundingMode.HALF_EVEN);
            return new Money(money.amount().divide(midRate, RoundingMode.HALF_EVEN), targetCurrency);

        } else {
            //converting from currency that is not baseCurrency(PLN)
            //the API gives back the rate between the base currency which is ALWAYS PLN and targetCurrency for example USD
            //for example if you want to convert from EUR TO USD
            //first we must convert from EUR TO PLN
            // then convert from PLN TO the target currency

            BigDecimal amountFromAccountCurrencyToBaseCurrency = currencyConversionStubService.convert(money, Currency.getInstance("PLN")).amount();

            RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
            BigDecimal midRateBetWeenTargetCurrencyAndBaseCurrency = rateWrapper.rates().get(0).mid().setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal targetCurrencyAmountFromBaseCurrency = amountFromAccountCurrencyToBaseCurrency.divide(midRateBetWeenTargetCurrencyAndBaseCurrency, RoundingMode.HALF_EVEN);

            return new Money(targetCurrencyAmountFromBaseCurrency, targetCurrency);
        }

    }
}
