package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Err;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class None<T> extends Option<T> {
    @Override
    @NotNull <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        return new None<>();
    }

    @Override
    @NotNull <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        return new None<>();
    }

    @Override
    @NotNull
    Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        if (supplier == null) {
            return new None<>();
        }
        T result = supplier.get();
        return result == null ? new None<>() : new Some<>(result);
    }

    @Override
    @NotNull
    Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        if (supplier == null) {
            return new None<>();
        }
        Option<T> option = supplier.get();
        return option == null ? new None<>() : option;
    }

    @Override
    @NotNull
    Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }

    @Override
    @NotNull
    Option<T> doOnNone(@Nullable Runnable runnable) {
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                LOGGER.error("Error thrown in runnable of Option.doOnNone(runnable)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    Result<T> asResult() {
        return new Err<>(new NullPointerException("Option is empty"));
    }

    @Override
    boolean isPresent() {
        return false;
    }
}