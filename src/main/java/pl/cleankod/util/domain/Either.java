package pl.cleankod.util.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Either<F, S> {
    private static final String LEFT_VALUE_CANNOT_BE_NULL = "The left value cannot be null.";
    private static final String RIGHT_VALUE_CANNOT_BE_NULL = "The right value cannot be null.";
    private final F leftValue;
    private final S rightValue;
    private final boolean successful;

    private Either(F leftValue, S rightValue, boolean successful) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.successful = successful;
    }

    public static <S> Either<Throwable, S> tryCatch(Supplier<S> function) {
        try {
            return Either.right(function.get());
        } catch (Throwable t) {
            return new Either<>(t, null, false);
        }
    }

    public static <F, S> Either<F, S> right() {
        return new Either<>(null, null, true);
    }

    public static <F, S> Either<F, S> right(S value) {
        return new Either<>(null, Objects.requireNonNull(value, RIGHT_VALUE_CANNOT_BE_NULL), true);
    }

    public static <F, S> Either<F, S> left() {
        return new Either<>(null, null, false);
    }

    public static <F, S> Either<F, S> left(F value) {
        return new Either<>(Objects.requireNonNull(value, LEFT_VALUE_CANNOT_BE_NULL), null, false);
    }

    public boolean isRight() {
        return successful;
    }

    public boolean isLeft() {
        return !successful;
    }

    public F leftValue() {
        return Objects.requireNonNull(leftValue, LEFT_VALUE_CANNOT_BE_NULL);
    }

    public S rightValue() {
        return Objects.requireNonNull(rightValue, RIGHT_VALUE_CANNOT_BE_NULL);
    }

    public <FM> Either<FM, S> mapLeft(Function<F, FM> failureMapper) {
        if (isLeft()) {
            return Either.left(failureMapper.apply(this.leftValue));
        }
        return Optional.ofNullable(rightValue)
                .map(s -> new Either<FM, S>(null, s, true))
                .orElse(Either.right());
    }

    public <SM> Either<F, SM> mapRight(Function<S, SM> successMapper) {
        if (isRight()) {
            return Either.right(successMapper.apply(this.rightValue));
        }
        return Optional.ofNullable(leftValue)
                .map(s -> new Either<F, SM>(s, null, false))
                .orElse(Either.left());
    }

    public <FM, SM> Either<FM, SM> mapBi(Function<F, FM> failureMapper, Function<S, SM> successMapper) {
        if (isLeft()) {
            return Either.left(failureMapper.apply(this.leftValue));
        }
        return Either.right(successMapper.apply(this.rightValue));
    }

    public <U> U fold(Function<F, U> failureMapper, Function<S, U> successMapper) {
        if (isLeft()) {
            return failureMapper.apply(this.leftValue);
        }
        return successMapper.apply(this.rightValue);
    }

    public S orElseGet(Function<F, S> failureMapper) {
        return Optional.ofNullable(this.rightValue).orElseGet(() -> failureMapper.apply(this.leftValue));
    }

    public S orElse(S other) {
        return Optional.ofNullable(this.rightValue).orElse(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return successful == either.successful && Objects.equals(rightValue, either.rightValue) && Objects.equals(leftValue, either.leftValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rightValue, leftValue, successful);
    }

    @Override
    public String toString() {
        return "Either{" +
                "rightValue=" + rightValue +
                ", leftValue=" + leftValue +
                ", successful=" + successful +
                '}';
    }
}
