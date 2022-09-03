package pl.cleankod.exchange.provider.nbp;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public class ExchangeRatesNbpService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Retry(name = "nbpResilience")
    @Cacheable(cacheNames = "nbpCache")
    public RateWrapper fetch(String table, String currency) {
        return exchangeRatesNbpClient.fetch(table, currency);
    }
}
