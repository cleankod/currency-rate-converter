package pl.cleankod.exchange.provider.nbp.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class FetchRatesCommand extends HystrixCommand<RateWrapper> {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Currency targetCurrency;

    public FetchRatesCommand(
            String hystrixKey,
            ExchangeRatesNbpClient exchangeRatesNbpClient,
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
        this.targetCurrency = targetCurrency;
    }

    @Override
    protected RateWrapper run() {
        System.out.println("--------------normal execution-----------");
        try {
            return exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        } catch (Exception exc) {
            throw new RuntimeException("Exception", exc);
        }
    }

    @Override
    protected RateWrapper getFallback() {
        return null;
    }
}

