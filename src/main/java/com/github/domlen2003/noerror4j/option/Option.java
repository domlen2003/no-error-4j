package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Err;
import com.github.domlen2003.noerror4j.result.Ok;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An Option type that can be used to return a value or nothing
 * <h2>An Option can be either a Some or a None which could be:</h2>
 * <h3>Checked with the isPresent() method and then cast to the correct type</h3>
 * <pre>{@code
 * if (option.isPresent()) {
 *   System.out.println(((Some<String>) option).getValue());
 * }
 * }</pre>
 * <h3>Pattern matched with an if</h3>
 * <pre>{@code
 * if (option instanceof Some<String> some) {
 *  System.out.println(some.getValue());
 *  }
 *  }</pre>
 * <h3>Pattern matched with a switch</h3>
 * <pre>{@code
 *  String value = switch (option) {
 *    case Some<String> some -> some.getValue();
 *    case None<String> none -> "No value";
 *  }
 *  }</pre>
 *
 * @param <T> the type of the value
 */
@SuppressWarnings("unused")
public sealed abstract class Option<T> permits None, Some {
    private static BiConsumer<String, Throwable> errorSink;

    public static void setErrorSink(BiConsumer<String, Throwable> errorSink) {
        if (errorSink != null) {
            Option.errorSink = errorSink;
        }
    }

    protected static void sinkError(String message, Throwable error) {
        if (errorSink != null) {
            errorSink.accept(message, error);
        }
    }

    /**
     * Creates a new Option of a nullable {@link Optional}
     *
     * @param value the optional to convert
     * @return a {@link Some} if the optional is present, a {@link None} otherwise
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    @NotNull
    @Contract("_ -> new")
    public static <T> Option<T> of(@Nullable Optional<T> value) {
        return value == null || value.isEmpty() ? None.instance() : Some.of(value.get());
    }

    /**
     * Applies a map function to the result in a safe way (catches any exceptions as {@link Err})
     *
     * @param mapper the function to map the result
     * @return the mapped result
     */
    @NotNull
    @Contract("_ -> new")
    public <U> Option<U> map(@Nullable Function<@NotNull Option<T>, @Nullable Option<U>> mapper) {
        if (mapper == null) {
            return None.instance();
        }
        try {
            Option<U> result = mapper.apply(this);
            return result == null ? None.instance() : result;
        } catch (Throwable throwable) {
            sinkError("Error thrown in mapper of Option.map(mapper)", throwable);
            return None.instance();
        }
    }

    /**
     * Maps the Value of the current Option if Present
     *
     * @param mapper the mapper to apply
     * @return a new {@link Some} if the mapper, mapped-value and previous value are not null, a {@link None} otherwise
     */
    @NotNull
    public abstract <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper);

    /**
     * Flat-maps the Value of the current Option if Present
     *
     * @param mapper the mapper to apply
     * @return a new {@link Some} if the mapper, new-option and previous-value are not null, a {@link None} otherwise
     */
    @NotNull
    public abstract <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper);

    /**
     * Returns the current Option if it is a {@link Some}, otherwise returns the supplied Value as an Option.
     *
     * @param supplier the supplier to get the value from
     * @return a {@link Some} if the Option or the supplier has a value, else a {@link None}
     */
    @NotNull
    public abstract Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier);

    /**
     * Returns the current Option if it is a {@link Some}, otherwise returns the supplied Option.
     *
     * @param supplier the supplier to get the value from
     * @return a {@link Some} if any of the two Options has a value, else a {@link None}
     */
    @NotNull
    public abstract Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier);

    /**
     * Calls a consumer with a value if the Option is a {@link Some}
     *
     * @param consumer the consumer to call
     * @return the current Option
     */
    @NotNull
    public abstract Option<T> doOnSome(@Nullable Consumer<T> consumer);

    /**
     * Runs a runnable if the Option is a {@link None}
     *
     * @param runnable the runnable to  call
     * @return the current Option
     */
    @NotNull
    public abstract Option<T> doOnNone(@Nullable Runnable runnable);

    /**
     * If the option is {@link Some} maps the value of Some to a new {@link Ok} returns s new  {@link Err} of a NullPointerException
     *
     * @return the new Result
     */
    @NotNull
    public abstract Result<T> asResult();

    /**
     * Whether the Option has a value
     *
     * @return true if the Option is a {@link Some}, false otherwise
     */
    @Contract("-> _")
    public abstract boolean isPresent();
}
