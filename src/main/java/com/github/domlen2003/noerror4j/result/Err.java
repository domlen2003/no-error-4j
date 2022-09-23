package com.github.domlen2003.noerror4j.result;

import com.github.domlen2003.noerror4j.option.None;
import com.github.domlen2003.noerror4j.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Err<T> extends Result<T> {
    private final Throwable error;

    /**
     * Creates a new Err instance.
     * <p><font color="red">
     *     WARNING!!! despite the intention of this library, this constructor is not safe and will throw an error if the error is null.
     * </font></p>
     * @throws NullPointerException if error is null
     * @param error the error of the Result.
     */
    @SuppressWarnings("ConstantConditions")
    public Err(@NotNull Throwable error) {
        if (error == null) {
            throw new NullPointerException("new Err(error) error is null");
        }
        this.error =  error;
    }

    /**
     * Creates a new Err instance.
     * <p><font color="red">
     *     WARNING!!! despite the intention of this library, this constructor is not safe and will throw an error if the message is null.
     * </font></p>
     * @throws NullPointerException if error is null
     * @param message the message of the error.
     */
    @SuppressWarnings("ConstantConditions")
    public Err(@NotNull String message) {
        if (message == null) {
            throw new NullPointerException("new Err(message) message is null");
        }
        this.error = new Exception(message);
    }

    /**
     * Gets the wrapped error.
     * @return the result error
     */
    public Throwable getError() {
        return error;
    }

    @Override
    @NotNull <U> Result<U> mapOk(@Nullable Function<@NotNull T, @Nullable U> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull <U> Result<U> flatMapOk(@Nullable Function<@NotNull T, @Nullable Result<U>> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull Result<T> mapErr(@Nullable Function<@NotNull Throwable, @Nullable T> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) is null"));
        }
        try {
            T result = mapper.apply(error);
            return result == null ? new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) returned null")) : new Ok<>(result);
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    @Override
    @NotNull Result<T> flatMapErr(@Nullable Function<@NotNull Throwable, @Nullable Result<T>> mapper) {
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
    @NotNull Result<T> doOnErr(@Nullable Consumer<@NotNull Throwable> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(error);
            } catch (Throwable e) {
                LOGGER.error("Error thrown in consumer of Result.doOnErr(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull Result<T> doOnOk(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }

    @Override
    @NotNull
    Option<T> asOption() {
        LOGGER.error("Error dropped when converting Result.asOption()", error);
        return new None<>();
    }

    @Override
    boolean isPresent() {
        return false;
    }
}