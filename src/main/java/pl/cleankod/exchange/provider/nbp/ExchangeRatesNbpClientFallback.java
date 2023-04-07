package pl.cleankod.exchange.provider.nbp;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.List;

public class ExchangeRatesNbpClientFallback implements ExchangeRatesNbpClient {
    private Exception exception;

    public ExchangeRatesNbpClientFallback(Exception exception) {
        this.exception = exception;
    }

    @Override
    public RateWrapper fetch(String table, String currency) {
        return new RateWrapper("A", "EUR", "EUR", List.of());
    }
}
