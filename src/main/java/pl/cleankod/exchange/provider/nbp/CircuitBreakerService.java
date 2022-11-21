package pl.cleankod.exchange.provider.nbp;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import pl.cleankod.exchange.provider.nbp.model.Rate;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.List;

public class CircuitBreakerService {
    private final CircuitBreaker circuitBreaker;
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CircuitBreakerService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                 CircuitBreaker circuitBreaker) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.circuitBreaker = circuitBreaker;
    }

    public RateWrapper fetch(String table, String currency) {
        return circuitBreaker.run(
                () -> exchangeRatesNbpClient.fetch(table, currency),
                r -> new RateWrapper("rate", "EUR", "EUR",
                        List.of(new Rate("_ as String", "_ as String", BigDecimal.valueOf(4.5274)))
                )
        );
    }
}
