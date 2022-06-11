package pl.cleankod.exchange.core.cache;

public interface CacheService {

    <T> void add(String key, T value);

    void remove(String key);

    <T> Object get(String key);

    void clear();

}
