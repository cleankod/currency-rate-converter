package pl.cleankod.exchange.core.domain;

public interface SingleValueObject<T> {
    String VALUE_METHOD_NAME = "value";

    T value();
}
