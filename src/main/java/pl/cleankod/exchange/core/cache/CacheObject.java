package pl.cleankod.exchange.core.cache;

import java.time.Instant;

public record CacheObject<T>(T value, Instant expiryTime) {

    boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }

}
