package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Err;
import com.github.domlen2003.noerror4j.result.Ok;
import com.github.domlen2003.noerror4j.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An Option type that can be used to return a value or nothing
 * <br>
 * An Option can be either a Some or a None which could be:
 * <br>
 * Checked with the isPresent() method and then cast to the correct type
 * <pre>{@code
 * if (option.isPresent()) {
 *   System.out.println(((Some<String>) option).getValue());
 * }
 * }</pre>
 * Pattern matched with an if
 * <pre>{@code
 * if (option instanceof Some<String> some) {
 *  System.out.println(some.getValue());
 *  }
 *  }</pre>
 * Pattern matched with a switch
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Option.class);

    /**
     * Creates a new Option of a nullable value
     *
     * @param value the value to wrap
     * @return a {@link Some} if the value is not null, a {@link None} otherwise
     */
    @Contract("_ -> new")
    public static <T> @NotNull Option<T> of(T value) {
        return value == null ? new None<>() : new Some<>(value);
    }

    @Contract("_ -> new")
    public static <T> @NotNull Option<T> of(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") @NotNull Optional<T> value) {
        return of(value.orElse(null));
    }

    /**
     * Maps the Value of the current Option if Present
     *
     * @param mapper the mapper to apply
     * @return a new {@link Some} if the mapper, mapped-value and previous value are not null, a {@link None} otherwise
     */
    public <U> @NotNull Option<U> mapSome(Function<? super T, ? extends U> mapper) {
        return switch (this) {
            case Some<T> some -> {
                if (mapper == null) {
                    yield new None<>();
                }
                try {
                    U value = mapper.apply(some.getValue());
                    if (value == null) {
                        yield new None<>();
                    }
                    yield new Some<>(value);
                } catch (Exception e) {
                    LOGGER.error("Error thrown in mapper of Option.map(mapper)", e);
                    yield new None<>();
                }
            }
            case None<T> ignored -> new None<>();
        };
    }

    /**
     * Flat-maps the Value of the current Option if Present
     *
     * @param mapper the mapper to apply
     * @return a new {@link Some} if the mapper, new-option and previous-value are not null, a {@link None} otherwise
     */
    public <U> @NotNull Option<U> flatMapSome(Function<? super T, ? extends Option<U>> mapper) {
        return switch (this) {
            case Some<T> some -> {
                if (mapper == null) {
                    yield new None<>();
                }
                try {
                    Option<U> option = mapper.apply(some.getValue());
                    if (option == null) {
                        yield new None<>();
                    }
                    yield option;
                } catch (Exception e) {
                    LOGGER.error("Error thrown in mapper of Option.flatMap(mapper)", e);
                    yield new None<>();
                }
            }
            case None<T> ignored -> new None<>();
        };
    }

    /**
     * Returns the current Option if it is a {@link Some}, otherwise returns the supplied Value as a option.
     *
     * @param supplier the supplier to get the value from
     * @return a {@link Some} if the Option or the supplier has a value, else a {@link None}
     */
    public @NotNull Option<T> mapNone(Supplier<T> supplier) {
        return switch (this) {
            case None<T> ignored -> {
                if (supplier == null) {
                    yield new None<>();
                }
                T option = supplier.get();
                if (option == null) {
                    yield new None<>();
                }
                yield new Some<>(option);
            }
            case Some<T> some -> some;
        };
    }

    /**
     * Returns the current Option if it is a {@link Some}, otherwise returns the supplied Option.
     *
     * @param supplier the supplier to get the value from
     * @return a {@link Some} if any of the two Options has a value, else a {@link None}
     */
    public @NotNull Option<T> flatMapNone(Supplier<? extends Option<T>> supplier) {
        return switch (this) {
            case None<T> ignored -> {
                if (supplier == null) {
                    yield new None<>();
                }
                Option<T> option = supplier.get();
                if (option == null) {
                    yield new None<>();
                }
                yield option;
            }
            case Some<T> some -> some;
        };
    }

    /**
     * Calls a consumer with a value if the Option is a {@link Some}
     *
     * @param consumer the consumer to call
     * @return the current Option
     */
    public @NotNull Option<T> doOnSome(Consumer<T> consumer) {
        if (this instanceof Some<T> some) {
            try {
                consumer.accept(some.getValue());
            } catch (Exception e) {
                LOGGER.error("Error thrown in consumer of Option.doOnSome(consumer)", e);
            }
        }
        return this;
    }

    /**
     * Runs a runnable if the Option is a {@link None}
     *
     * @param runnable the runnable to  call
     * @return the current Option
     */
    public @NotNull Option<T> doOnNone(Runnable runnable) {
        if (this instanceof None<T> ignored) {
            try {
                runnable.run();
            } catch (Exception e) {
                LOGGER.error("Error thrown in runnable of Option.doOnNone(runnable)", e);
            }
        }
        return this;
    }

    /**
     * If the option is {@link Some} maps the value of Some to a new {@link Ok} returns s new  {@link Err} of a NullPointerException
     *
     * @return the new Result
     */
    public @NotNull Result<T> asResult() {
        return switch (this) {
            case Some<T> some -> new Ok<>(some.getValue());
            case None<T> ignored -> new Err<>(new NullPointerException("Option is empty"));
        };
    }

    /**
     * Whether the Option has a value
     *
     * @return true if the Option is a {@link Some}, false otherwise
     */
    public boolean isPresent() {
        return this instanceof Some;
    }
}
