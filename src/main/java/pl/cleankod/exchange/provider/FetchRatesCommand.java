package pl.cleankod.exchange.provider;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class FetchRatesCommand extends HystrixCommand<Money> {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Money money;
    private final Currency targetCurrency;

    public FetchRatesCommand(
            String hystrixKey,
            ExchangeRatesNbpClient exchangeRatesNbpClient,
            Money money,
            Currency targetCurrency) {
        super(Setter.withGroupKey(
                        HystrixCommandGroupKey.Factory
                                .asKey(hystrixKey))
                .andCommandKey(
                        HystrixCommandKey.Factory
                                .asKey(hystrixKey))
                .andThreadPoolKey(
                        HystrixThreadPoolKey.Factory
                                .asKey(hystrixKey)));
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.money = money;
        this.targetCurrency = targetCurrency;
    }

    @Override
    protected Money run() {
        System.out.println("--------------normal execution-----------");
        try {
            RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
            BigDecimal midRate = rateWrapper.rates().get(0).mid();
            BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP);
            return new Money(calculatedRate, targetCurrency);

        } catch (Exception exc) {
            throw new RuntimeException("Exception", exc);
        }
    }

    @Override
    protected Money getFallback() {
        System.out.println("------- fallback! ---------");
        return null;
    }
}

