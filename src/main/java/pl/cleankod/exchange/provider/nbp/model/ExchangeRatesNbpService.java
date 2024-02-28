package pl.cleankod.exchange.provider.nbp.model;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

@Service
public class ExchangeRatesNbpService {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    Logger logger = LoggerFactory.getLogger(ExchangeRatesNbpClient.class);
    public ExchangeRatesNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Cacheable("npbCache")
    /*@Retryable(retryFor = {FeignException.class}, maxAttempts = 2, backoff = @Backoff(value = 2000))
        i want to add a retry mechanism to handle the circuit breaker case,
        basically i handle the FeignException
        for some reason the @Retryable it's not found, i added all the dependencies...
     * */
     public RateWrapper get(String table, String currency) {
        return exchangeRatesNbpClient.fetch(table, currency);
    }

    @CacheEvict(value = "npbCache", allEntries = true)
    @Scheduled(cron = "0 0 1 * * *")
    public void removeAll() {
        logger.info("###CLEAR CACHE NBP###");
    }


}
