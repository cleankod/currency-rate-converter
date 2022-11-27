package pl.cleankod.exchange.provider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public class CurrencyConversionNbpService implements CurrencyConversionService {
   private static final String NBP_SERVICE = "NbpService";

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CircuitBreaker circuitBreaker;

    public CurrencyConversionNbpService(final ExchangeRatesNbpClient exchangeRatesNbpClient, final CircuitBreakerRegistry circuitBreakerRegistry) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(NBP_SERVICE);
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = callNbpClientForData(targetCurrency);
        BigDecimal midRate = rateWrapper.rates().get(rateWrapper.rates().size() - 1).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, scale, RoundingMode.HALF_EVEN);
        return new Money(calculatedRate, targetCurrency);
    }

    private RateWrapper callNbpClientForData(Currency targetCurrency) {
      return circuitBreaker.decorateSupplier(() -> exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode(),
            LocalDate.now().minusDays(5),
            LocalDate.now().minusDays(1))).get();
    }
}
