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
    private static final None<?> INSTANCE = new None<>();

    private None() {
    }

    @NotNull
    @Contract(" -> !null")
    @SuppressWarnings("unchecked")
    public static <T> None<T> instance() {
        return (None<T>) INSTANCE;
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        return None.instance();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        return None.instance();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        if (supplier == null) {
            return None.instance();
        }
        try {
            T result = supplier.get();
            return result == null ? None.instance() : Some.of(result);
        } catch (Exception e) {
            sinkError("Error thrown in supplier of Option.mapNone(supplier)", e);
        }
        return None.instance();
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        if (supplier == null) {
            return None.instance();
        }
        try {
            Option<T> option = supplier.get();
            return option == null ? None.instance() : option;
        } catch (Exception e) {
            sinkError("Error thrown in supplier of Option.flatMapNone(supplier)", e);
        }
        return None.instance();
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
                sinkError("Error thrown in runnable of Option.doOnNone(runnable)", e);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof None;
    }

    @Override
    public String toString() {
        return "None";
    }
}