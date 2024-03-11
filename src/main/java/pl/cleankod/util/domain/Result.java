package pl.cleankod.util.domain;

import java.util.Objects;
import java.util.function.Function;

public class Result<T> {

    private final T value;
    private final Failure failure;

    private Result(T value, Failure failure) {
        this.value = value;
        this.failure = failure;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }
    public static <T> Result<T> failure(Failure failure) {
        Objects.requireNonNull(failure);
        return new Result<>(null, failure);
    }
    public boolean isOk() {
        return failure == null;
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (!isOk()) {
            return new Result(value, this.failure);
        }
        return Result.ok(mapper.apply(value));
    }
    public T unwrap() {
        return value;
    }

    public Result<T> orElse(Function<Failure, T> mapper) {
        if (isOk()) {
            return Result.ok(value);
        }
        return Result.ok(mapper.apply(failure));
    }
    public Result<T> onFailure(Function<Failure, T> mapper) {
        return orElse(mapper);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result<?> result = (Result<?>) o;
        return Objects.equals(value, result.value)
                && Objects.equals(failure, result.failure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, failure);
    }
}