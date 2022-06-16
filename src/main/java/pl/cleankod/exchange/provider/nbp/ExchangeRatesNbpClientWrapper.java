package pl.cleankod.exchange.provider.nbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.cleankod.exchange.exceptions.BaseException;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pl.cleankod.exchange.exceptions.BaseException.ErrorCode.THIRD_PARTY_CLIENT;

@Component
public class ExchangeRatesNbpClientWrapper implements ExchangeRatesNbpClient {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    @Autowired
    public ExchangeRatesNbpClientWrapper(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @Cacheable("nbpApiResult")
    public RateWrapper fetch(String table, String currency) {
        RateWrapper rateWrapper;
        try {
            rateWrapper = exchangeRatesNbpClient.fetch("A", currency);
        } catch (Exception e) {
            String message = String.format("An exception was thrown when ExchangeRatesNbpClient was trying to fetch rates" +
                    " for currency %s", currency);
            throw new BaseException(message, THIRD_PARTY_CLIENT, INTERNAL_SERVER_ERROR, e);
        }
        return rateWrapper;
    }
}
