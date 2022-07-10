package pl.cleankod.exchange.provider.nbp.cache;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public record CacheEntry(String currency, RateWrapper rate, int frequency) {
    public CacheEntry {
    }
}
