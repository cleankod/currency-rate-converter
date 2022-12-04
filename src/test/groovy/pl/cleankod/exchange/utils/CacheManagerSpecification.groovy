package pl.cleankod.exchange.utils

import pl.cleankod.util.CacheManager
import spock.lang.Specification

class CacheManagerSpecification extends Specification {

    private static CacheManager cacheManager;
    private static long timeToLiveCacheInSeconds = 1;

    def setupSpec() {
        cacheManager = CacheManager.getInstance(timeToLiveCacheInSeconds);
    }

    def cleanup() {
        cacheManager.clearAllCache();
    }

    def "should get value from cache"() {
        given:
        String cacheValue1 = "cache value 1";
        String cacheValue2 = "cache value 1";
        String cacheValue3 = "cache value 1";

        when:
        cacheManager.put("key-1", cacheValue1);
        cacheManager.put("key-2", cacheValue2);
        cacheManager.put("key-3", cacheValue3);

        then:
        cacheManager.get("key-1").isPresent()
        cacheManager.get("key-1").get().cachedObject() == cacheValue1
        cacheManager.get("key-2").isPresent()
        cacheManager.get("key-2").get().cachedObject() == cacheValue1
        cacheManager.get("key-3").isPresent()
        cacheManager.get("key-3").get().cachedObject() == cacheValue1
    }

    def "should not find any value in cache after time living"() {
        given:
        String cacheValue1 = "cache value 1";
        String cacheValue2 = "cache value 2";
        String cacheValue3 = "cache value 3";

        when:
        cacheManager.put("key-1", cacheValue1);
        cacheManager.put("key-2", cacheValue2);
        cacheManager.put("key-3", cacheValue3);

        then:
        Thread.sleep(timeToLiveCacheInSeconds * 2001);
        !cacheManager.get("key-1").isPresent();
        !cacheManager.get("key-2").isPresent();
        !cacheManager.get("key-3").isPresent();

    }

    def "should throw NPE in case of null key"() {
        given:
        String cacheValue1 = "cache value 1";

        when:
        cacheManager.put(null, cacheValue1)

        then:
        thrown(NullPointerException)
    }

    def "should throw NPE in case of null value"() {
        given:
        String cacheValue1 = null;

        when:
        cacheManager.put("key-1", cacheValue1)

        then:
        thrown(NullPointerException)
    }
}