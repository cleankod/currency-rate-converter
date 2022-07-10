package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Optional;

public interface Cache{

    Optional<RateWrapper> get(String currency);

    void put(String currency, RateWrapper rate);

    void clear(int maxCapacity);
}
