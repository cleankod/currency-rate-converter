package pl.cleankod.exchange.core.gateway;

public interface CacheService<K, T> {
    void put(K key, T value);

    T get(K key);

    boolean contains(K key);

    void remove(K key);

    int size();

    void cleanup();
}
