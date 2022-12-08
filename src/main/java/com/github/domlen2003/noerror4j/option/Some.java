package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Ok;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class Some<T> extends Option<T> {
    private final T value;

    private Some(@NotNull T value) {
        this.value = value;
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Option<T> of(@Nullable T value) {
        return value != null ? new Some<>(value) : None.instance();
    }

    /**
     * Gets the wrapped value.
     *
     * @return the Options value.
     */
    @NotNull
    public T getValue() {
        return value;
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        if (mapper == null) {
            return None.instance();
        }
        try {
            U result = mapper.apply(value);
            return result == null ? None.instance() : new Some<>(result);
        } catch (Exception e) {
            sinkError("Error thrown in mapper of Option.map(mapper)", e);
            return None.instance();
        }
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        if (mapper == null) {
            return None.instance();
        }
        try {
            Option<U> option = mapper.apply(value);
            return option == null ? None.instance() : option;
        } catch (Exception e) {
            sinkError("Error thrown in mapper of Option.flatMap(mapper)", e);
            return None.instance();
        }
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        return this;
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        return this;
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(value);
            } catch (Exception e) {
               sinkError("Error thrown in consumer of Option.doOnSome(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> doOnNone(@Nullable Runnable runnable) {
        return this;
    }

    @Override
    @NotNull
    @Contract("-> new")
    public Result<T> asResult() {
        return Ok.of(value);
    }

    @Override
    @Contract("-> true")
    public boolean isPresent() {
        return true;
    }
}