package pl.cleankod.exchange.core.domain;

import java.io.Serializable;

public interface SingleValueObject<T extends Serializable> {
    T value();
}