package pl.cleankod.exchange.provider.nbp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.entrypoint.exception.NbpApiException;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

@Slf4j
public class ExchangeRatesNbpClientImpl implements ExchangeRatesNbpClient {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesNbpClientImpl(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @Cacheable("rate")
    public RateWrapper fetch(String table, String currency) {
        try {
            return exchangeRatesNbpClient.fetch("A", currency);
        } catch (Exception e) {
            log.error(String.format("Could not fetch rates for given currency %s", currency));
            throw new NbpApiException(currency);
        }
    }
}
