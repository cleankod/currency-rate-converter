package pl.cleankod.exchange.provider.conversion.nbp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.provider.conversion.nbp.model.RateWrapper;

public class ExchangeRatesNbpClientCircuitBreakerWrapper implements ExchangeRatesNbpClient {

    private final Logger LOG = LoggerFactory.getLogger(ExchangeRatesNbpClientCircuitBreakerWrapper.class);

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesNbpClientCircuitBreakerWrapper(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    /**
     * Here we should apply the circuit breaker by using resilience4j as a dependency.
     * Unfortunately at this moment time does not permit me to finish this implementation as well but with a spare half an hour it can be done here.
     * <p>
     * We could rely on Spring support for circuit breaker and solve the problem with annotations but in the case we wish
     * to switch the framework the most portable solution is to use directly resilience4j.
     */
    @Override
    public RateWrapper fetch(String table, String currency) {
        LOG.info("Will make a HTTP call to fetch the rates by table {} and currecy {} ", table, currency);

        return exchangeRatesNbpClient.fetch(table, currency);
    }

}
