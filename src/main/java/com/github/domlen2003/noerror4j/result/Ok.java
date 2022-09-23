package com.github.domlen2003.noerror4j.result;

import com.github.domlen2003.noerror4j.option.Option;
import com.github.domlen2003.noerror4j.option.Some;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Ok<T> extends Result<T> {
    private final T value;

    private Ok(@NotNull T value) {
        this.value = value;
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Result<T> of(@Nullable T value) {
        return value != null ? new Ok<>(value) : Err.of(new NullPointerException("Ok.of() value is null"));
    }

    public T getValue() {
        return value;
    }

    @Override
    @NotNull <U> Result<U> mapOk(@Nullable Function<@NotNull T, @Nullable U> mapper) {
        if (mapper == null) {
            return Err.of(new NullPointerException("Mapper for Result.mapOk(mapper) is null"));
        }
        try {
            U result = mapper.apply(value);
            return result == null ? Err.of(new NullPointerException("Mapper for Result.mapOk(mapper) returned null")) : new Ok<>(result);
        } catch (Throwable throwable) {
            return Err.of(throwable);
        }
    }

    @Override
    @NotNull <U> Result<U> flatMapOk(@Nullable Function<@NotNull T, @Nullable Result<U>> mapper) {
        if (mapper == null) {
            return Err.of(new NullPointerException("Mapper for Result.flatMapOk(mapper) is null"));
        }
        try {
            Result<U> result = mapper.apply(value);
            return result == null ? Err.of(new NullPointerException("Mapper for Result.flatMapOk(mapper) returned null")) : result;
        } catch (Throwable throwable) {
            return Err.of(throwable);
        }
    }

    @Override
    @NotNull Result<T> mapErr(@Nullable Function<@NotNull Throwable, @Nullable T> mapper) {
        return this;
    }

    @Override
    @NotNull Result<T> flatMapErr(@Nullable Function<@NotNull Throwable, @Nullable Result<T>> mapper) {
        return this;
    }

    @Override
    @NotNull Result<T> doOnErr(@Nullable Consumer<@NotNull Throwable> consumer) {
        return this;
    }

    @Override
    @NotNull Result<T> doOnOk(@Nullable Consumer<@NotNull T> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(value);
            } catch (Throwable e) {
                LOGGER.error("Error thrown in consumer of Result.doOnOk(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    Option<T> asOption() {
        return Some.of(value);
    }

    @Override
    boolean isPresent() {
        return true;
    }
}