package com.github.domlen2003.noerror4j.result;

import com.github.domlen2003.noerror4j.option.None;
import com.github.domlen2003.noerror4j.option.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Err<T> extends Result<T> {
    private final Throwable error;

    private Err(@NotNull Throwable error) {
        this.error = error;
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> Err<T> of(@Nullable Throwable value) {
        return value != null ?
                new Err<>(value) :
                new Err<>(new NullPointerException("Err.of() error is null"));
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> Err<T> of(@Nullable String message) {
        return message != null ?
                new Err<>(new RuntimeException(message)) :
                new Err<>(new NullPointerException("Err.of() message is null"));
    }

    @NotNull
    @Contract("_, _-> new")
    public static <T> Err<T> of(@Nullable String message, Throwable cause) {
        return message != null ?
                new Err<>(new RuntimeException(message, cause)) :
                new Err<>(new NullPointerException("Err.of() message is null"));
    }

    /**
     * Gets the wrapped error.
     *
     * @return the result error
     */
    @NotNull
    public Throwable getError() {
        return error;
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Result<U> mapOk(@Nullable Function<@NotNull T, @Nullable U> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public <U> Result<U> flatMapOk(@Nullable Function<@NotNull T, @Nullable Result<U>> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Result<T> mapErr(@Nullable Function<@NotNull Throwable, @Nullable T> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) is null"));
        }
        try {
            T result = mapper.apply(error);
            return result == null ? new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) returned null")) : Ok.of(result);
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    @Override
    @NotNull
    @Contract("_ -> new")
    public Result<T> flatMapErr(@Nullable Function<@NotNull Throwable, @Nullable Result<T>> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.flatMapErr(mapper) is null"));
        }
        try {
            Result<T> result = mapper.apply(error);
            return result == null ? new Err<>(new NullPointerException("Mapper for Result.flatMapErr(mapper) returned null")) : result;
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Result<T> doOnErr(@Nullable Consumer<@NotNull Throwable> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(error);
            } catch (Throwable e) {
                sinkError("Error thrown in consumer of Result.doOnErr(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public Result<T> doOnOk(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }

    @Override
    @NotNull
    @Unmodifiable
    @Contract("-> new")
    public Option<T> asOption() {
        sinkError("Error dropped when converting Result.asOption()", error);
        return None.instance();
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
        return obj instanceof Err<?> other && error.equals(other.error);
    }

    @Override
    public String toString() {
        return "Err[" + error + "]";
    }
}