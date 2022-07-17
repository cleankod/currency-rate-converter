package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.provider.model.RateWrapper;

import java.util.Optional;

public interface Cache{

    Optional<RateWrapper> get(String currency, String requestedDate);

    void put(String currencyCode, String requestedDate, RateWrapper rate);

}
