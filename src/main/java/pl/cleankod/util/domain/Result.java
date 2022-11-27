package pl.cleankod.util.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Result<S, F> {
    private static final String FAIL_VALUE_CANNOT_BE_NULL = "The fail value cannot be null.";
    private static final String SUCCESS_VALUE_CANNOT_BE_NULL = "The success value cannot be null.";
    private final S successfulValue;
    private final F failValue;
    private final boolean successful;

    public static <S, F> Result<S, F> successful() {
        return new Result<>(null, null, true);
    }

    public static <S, F> Result<S, F> successful(S value) {
        return new Result<>(Objects.requireNonNull(value, SUCCESS_VALUE_CANNOT_BE_NULL), null, true);
    }

    public static <S, F> Result<S, F> fail() {
        return new Result<>(null, null, false);
    }

    public static <S, F> Result<S, F> fail(F value) {
        return new Result<>(null, Objects.requireNonNull(value, FAIL_VALUE_CANNOT_BE_NULL), false);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean isFail() {
        return !successful;
    }

    public S successfulValue() {
        return Objects.requireNonNull(successfulValue, SUCCESS_VALUE_CANNOT_BE_NULL);
    }

    public F failValue() {
        return Objects.requireNonNull(failValue, FAIL_VALUE_CANNOT_BE_NULL);
    }

    public <X> Result<S, X> failMap(Function<F, X> failureMapper) {
        if (isFail()) {
            return Result.fail(failureMapper.apply(this.failValue));
        }
        return Optional.ofNullable(successfulValue)
            .map(s -> new Result<S, X>(s, null, true))
            .orElse(Result.successful());
    }

    public <Y> Result<Y, F> successMap(Function<S, Y> successMapper) {
        if (isSuccessful()) {
            return Result.successful(successMapper.apply(this.successfulValue));
        }
        return Result.fail(this.failValue);
    }

    public <U> U fold(Function<S, U> successMapper, Function<F, U> failureMapper) {
        if (isFail()) {
            return failureMapper.apply(this.failValue);
        }
        return successMapper.apply(this.successfulValue);
    }

    public <X, Y> Result<X, Y> biMap(Function<S, X> successMapper, Function<F, Y> failureMapper) {
        if (isFail()) {
            return Result.fail(failureMapper.apply(this.failValue));
        }
        return Result.successful(successMapper.apply(this.successfulValue));
    }

    public S orElseGet(Function<F, S> failureMapper) {
        return Optional.ofNullable(this.successfulValue).orElseGet(() -> failureMapper.apply(this.failValue));
    }

    public S orElse(S other) {
        return Optional.ofNullable(this.successfulValue).orElse(other);
    }
}
