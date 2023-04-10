package pl.cleankod.exchange.provider.nbp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class NbpClientAdapter {

    private final Logger logger = LoggerFactory.getLogger(NbpClientAdapter.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public NbpClientAdapter(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    public RateWrapper fetch(Currency targetCurrency) {
        logger.debug("Fetch currency {} from NBP API", targetCurrency.getCurrencyCode());
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        logger.debug("NBP API response {}", rateWrapper);
        return rateWrapper;
    }
}
