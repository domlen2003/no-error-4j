package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Ok;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class Some<T> extends Option<T> {
    private final T value;

    /**
     * Creates a new Some instance.
     * <p><font color="red">
     *     WARNING!!! despite the intention of this library, this constructor is not safe and will throw an error if the value is null.
     * </font></p>
     * @throws NullPointerException if value is null
     * @param value the value of the Option.
     */
    @SuppressWarnings("ConstantConditions")
    public Some(@NotNull T value) {
        if (value == null) {
            throw new NullPointerException("new Some(value) value is null");
        }
        this.value = value;
    }

    /**
     * Gets the wrapped value.
     * @return the Options value.
     */
    @NotNull
    public T getValue() {
        return value;
    }

    @Override
    @NotNull
    <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        if (mapper == null) {
            return new None<>();
        }
        try {
            U result = mapper.apply(value);
            return result == null ? new None<>() : new Some<>(result);
        } catch (Exception e) {
            LOGGER.error("Error thrown in mapper of Option.map(mapper)", e);
            return new None<>();
        }
    }

    @Override
    @NotNull
    <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        if (mapper == null) {
            return new None<>();
        }
        try {
            Option<U> option = mapper.apply(value);
            return option == null ? new None<>() : option;
        } catch (Exception e) {
            LOGGER.error("Error thrown in mapper of Option.flatMap(mapper)", e);
            return new None<>();
        }
    }

    @Override
    @NotNull
    Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        return this;
    }

    @Override
    @NotNull
    Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        return this;
    }

    @Override
    @NotNull
    Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(value);
            } catch (Exception e) {
                LOGGER.error("Error thrown in consumer of Option.doOnSome(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    Option<T> doOnNone(@Nullable Runnable runnable) {
        return this;
    }

    @Override
    @NotNull
    Result<T> asResult() {
        return new Ok<>(value);
    }

    @Override
    boolean isPresent() {
        return true;
    }
}