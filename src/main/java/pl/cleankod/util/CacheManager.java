package pl.cleankod.util;

import lombok.NonNull;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private static volatile CacheManager INSTANCE;
    private final Map<String, CacheValue> cache;
    private final long timeToLiveCacheInMillis;

    private CacheManager(long timeToLiveCacheInSeconds) {
        this.cache = new ConcurrentHashMap<>(10);
        this.timeToLiveCacheInMillis = timeToLiveCacheInSeconds * 1000;
    }

    public void put(@NonNull String cacheKey, @NonNull Object value) {
        CacheValue cacheValue = new CacheValue(value, System.currentTimeMillis());
        cache.put(cacheKey, cacheValue);
    }

    public Optional<CacheManager.CacheValue> get(String cacheKey) {
        Optional<CacheManager.CacheValue> cacheValue = Optional.ofNullable(cache.get(cacheKey));
        cacheValue.ifPresent(cv -> {
            long cacheValueLifeTime = System.currentTimeMillis() - cv.cachedObjectInitTime;
            if (cacheValueLifeTime > timeToLiveCacheInMillis) {
                removeByKey(cacheKey);
            }
        });
        return Optional.ofNullable(cache.get(cacheKey));
    }

    public void removeByKey(String cacheKey) {
        cache.remove(cacheKey);
    }

    public void clearAllCache() {
        cache.clear();
    }

    public static CacheManager getInstance(long timeToLiveCacheInSeconds) {
        if (INSTANCE == null) {
            System.out.println("TWOZENIE CACHE MANAGER + " + timeToLiveCacheInSeconds);
            synchronized (CacheManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheManager(timeToLiveCacheInSeconds);
                }
            }
        }
        return INSTANCE;
    }

    public void putIfAbsent(String cacheKey, RateWrapper rateWrapper) {
        cache.putIfAbsent(cacheKey, CacheValue.of(rateWrapper));
    }

    public record CacheValue(Object cachedObject, long cachedObjectInitTime) {

        public static CacheValue of(Object cachedObject) {
            return new CacheValue(cachedObject, System.currentTimeMillis());
        }
    }
}