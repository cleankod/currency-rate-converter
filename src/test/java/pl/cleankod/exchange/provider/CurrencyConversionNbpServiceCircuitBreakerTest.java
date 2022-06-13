package pl.cleankod.exchange.provider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.cleankod.exchange.core.domain.rate.MidRate;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyConversionNbpServiceCircuitBreakerTest {

    @Autowired
    private CurrencyConversionNbpService currencyConversionNbpService;

    private final MidRate CIRCUIT_BREAKER_MID_RATE = new MidRate(
            Currency.getInstance("PLN"),
            BigDecimal.ONE
    );

    @Test
    void shouldReturnDefaultCurrency() {
        //when
        MidRate rate = currencyConversionNbpService.getMidRate(Currency.getInstance("USD"));

        //then
        assertEquals(CIRCUIT_BREAKER_MID_RATE, rate);
    }

}


