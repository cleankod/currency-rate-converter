package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.provider.nbp.model.MidRate;

import java.util.Currency;

public interface RateFetchingService {
    MidRate fetch(Currency targetCurrency);
}
