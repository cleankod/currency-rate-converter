package pl.cleankod.exchange.core.cache;

import java.lang.ref.SoftReference;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheServiceImpl implements CacheService {

    private final ConcurrentHashMap<String, SoftReference<CacheObject>> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void add(final String key, final T value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.remove(key);
        } else {
            final Instant expiryTime = Instant.now().plus(1, ChronoUnit.HOURS);
            cache.put(key, new SoftReference<>(new CacheObject(value, expiryTime)));
        }
    }

    @Override
    public void remove(final String key) {
        cache.remove(key);
    }

    @Override
    public <T> T get(final String key) {
        return Optional.ofNullable(cache.get(key))
                .map(SoftReference::get)
                .filter(cacheObject -> !cacheObject.isExpired())
                .map(val -> (T) val.value()).orElse(null);
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
