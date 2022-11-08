package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Err;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class None<T> extends Option<T> {
    private None() {
    }

    @NotNull
    @Contract(" -> new")
    public static <T>  None<T> create() {
        return new None<>();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        return None.create();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        return None.create();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        if (supplier == null) {
            return None.create();
        }
        try {
            T result = supplier.get();
            return result == null ? None.create() : Some.of(result);
        } catch (Exception e) {
            LOGGER.error("Error thrown in supplier of Option.mapNone(supplier)");
        }
        return None.create();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        if (supplier == null) {
            return None.create();
        }
        try {
            Option<T> option = supplier.get();
            return option == null ? None.create() : option;
        } catch (Exception e) {
            LOGGER.error("Error thrown in supplier of Option.flatMapNone(supplier)");
        }
        return None.create();
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }


    @Override
    @NotNull
    @Contract("_ -> this")
    public Option<T> doOnNone(@Nullable Runnable runnable) {
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
    @Contract("-> new")
    public Result<T> asResult() {
        return Err.of(new NullPointerException("Option is empty"));
    }

    @Override
    @Contract("-> false")
    public boolean isPresent() {
        return false;
    }
}