package pl.cleankod.exchange.provider;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;

public class CircuitBreakerNbpServiceProxy implements CurrencyConversionService {

    private final CircuitBreaker circuitBreaker;
    private final CurrencyConversionNbpService conversionNbpService;

    public CircuitBreakerNbpServiceProxy(CircuitBreaker circuitBreaker, CurrencyConversionNbpService conversionNbpService) {
        this.circuitBreaker = circuitBreaker;
        this.conversionNbpService = conversionNbpService;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        return circuitBreaker.executeSupplier(() -> conversionNbpService.convert(money, targetCurrency));
    }
}
