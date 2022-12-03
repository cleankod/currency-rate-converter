package pl.cleankod.util;

public interface Preconditions {
    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Given value cannot be null");
        }
        return obj;
    }
}
