package pl.cleankod.exchange.core.util;

public interface Preconditions {
    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("Given value cannot be null");
        }
        return obj;
    }
}
