package pl.cleankod.exchange.provider;


import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;
import pl.cleankod.exchange.core.gateway.CacheService;

import java.util.ArrayList;

public class CacheServiceImpl<K, T> implements CacheService<K, T> {
    private final long timeToLive;

    // LRUMap with a fixed maximum size which removes the least recently used entry if an entry is added when full.
    private final LRUMap<K, CacheObject> cacheMap;

    public CacheServiceImpl(long timeToLive, final long timerInterval, int maxItems) {
        this.timeToLive = timeToLive * 1000;

        cacheMap = new LRUMap<>(maxItems);

        if (this.timeToLive > 0 && timerInterval > 0) {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(timerInterval * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    cleanup();
                }
            });

            // By setting the thread as Daemon we are assuring that the cache thread is terminated.
            // JVM will exit if all threads remaining are daemons.
            t.setDaemon(true);
            t.start();
        }
    }

    public void put(K key, T value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheObject(value));
        }
    }

    public T get(K key) {
        synchronized (cacheMap) {
            CacheObject c = cacheMap.get(key);
            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    @Override
    public boolean contains(K key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }

    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    public void cleanup() {

        // System: The System class contains several useful class fields and methods.
        // It cannot be instantiated. Among the facilities provided by the System class are standard input, standard output,
        // and error output streams; access to externally defined properties and environment variables;
        // a means of loading files and libraries; and a utility method for quickly copying a portion of an array.
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey;

        synchronized (cacheMap) {
            MapIterator<K, CacheObject> itr = cacheMap.mapIterator();
            deleteKey = new ArrayList<>((cacheMap.size() / 2) + 1);
            K key;
            CacheObject c;

            while (itr.hasNext()) {
                key = itr.next();
                c = itr.getValue();

                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            // A hint to the scheduler that the current thread is willing to yield its current use of a processor.
            // The scheduler is free to ignore this hint.
            Thread.yield();
        }
    }

    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public T value;

        protected CacheObject(T value) {
            this.value = value;
        }
    }
}
