package pl.cleankod.exchange.provider;

import org.springframework.stereotype.Component;
import pl.cleankod.exchange.core.gateway.Cache;
import pl.cleankod.exchange.provider.model.CacheEntry;
import pl.cleankod.exchange.provider.model.RateWrapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class NbpLFUCache implements Cache {

    private Map<String, CacheEntry> cacheMap = new HashMap<>();
    private final int maxCapacity;

    public NbpLFUCache(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public Optional<RateWrapper> get(String currencyCode, String requestedDate) {
        if (cacheMap.containsKey(currencyCode) && cacheMap.get(currencyCode).date().equals(requestedDate)) {
            CacheEntry entry = cacheMap.get(currencyCode);
            CacheEntry toSave = new CacheEntry(requestedDate, entry.rate(), entry.frequency() + 1);
            cacheMap.put(currencyCode, toSave);
            return Optional.of(entry.rate());
        }
        return Optional.empty();
    }

    @Override
    public void put(String currencyCode, String requestedDate, RateWrapper rate) {
        cacheMap.put(currencyCode, new CacheEntry(requestedDate, rate, 0));
    }

    @Override
    public void clear(int maxCapacity) {
        this.cacheMap = new LinkedHashMap<>(maxCapacity);
    }

    private boolean isFull() {
        return maxCapacity == cacheMap.size();
    }


}
