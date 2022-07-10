package pl.cleankod.exchange.provider.nbp.cache;

import org.springframework.stereotype.Component;
import pl.cleankod.exchange.provider.Cache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class NbpLFUCache implements Cache {

    private LinkedHashMap<String, CacheEntry> cacheMap = new LinkedHashMap<>();
    private final int maxCapacity;

    public NbpLFUCache(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public Optional<RateWrapper> get(String currency) {
        if (cacheMap.containsKey(currency)) {
            CacheEntry entry = cacheMap.get(currency);
            CacheEntry toSave = new CacheEntry(currency, entry.rate(), entry.frequency() + 1);
            cacheMap.put(currency, toSave);
            return Optional.of(entry.rate());
        }
        return Optional.empty();
    }

    @Override
    public void put(String currency, RateWrapper rate) {
        if (!this.isFull()) {
            cacheMap.put(currency, new CacheEntry(currency, rate, 0));
        } else {
            cacheMap.put(currency, new CacheEntry(currency, rate, 0));
        }
    }

    @Override
    public void clear(int maxCapacity) {
        this.cacheMap = new LinkedHashMap<>(maxCapacity);
    }

    private boolean isFull() {
        if (cacheMap.size() == maxCapacity) {
            return true;
        }
        return false;
    }


}
