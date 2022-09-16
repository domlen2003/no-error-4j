package com.github.domlen2003.noerror4j.result;

import com.github.domlen2003.noerror4j.option.None;
import com.github.domlen2003.noerror4j.option.Option;
import com.github.domlen2003.noerror4j.option.Some;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h1>A Result type that can be used to return a value or an error</h1>
 * <h2>A Result can be either a {@link Ok} or a {@link Err} which could be:</h2>
 * <h3>Checked with the isSuccess() method and then cast to the correct type</h3>
 * <pre>{@code
 * if (result.isSuccess()) {
 *    System.out.println(((Ok<String>) result).getValue());
 * }
 * }</pre>
 * <h3>Pattern matched with an if</h3>
 * <pre>{@code
 * if (result instanceof Ok<String> success) {
 *   System.out.println(success.getValue());
 * }
 * }</pre>
 * <h3>Pattern matched with a switch</h3>
 * <pre>{@code
 * String value = switch (result) {
 *    case Ok<String> success -> success.getValue();
 *    case Err<String> failure -> "Error: " + failure.getError().getMessage();
 * };
 * }</pre>
 *
 * @param <T> the type of the value
 */
@SuppressWarnings("unused")
public sealed abstract class Result<T> permits Err, Ok {
    private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);

    /**
     * If getting the supplier didn't throw an exception creates an {@link Ok}, creates an {@link Err} otherwise
     *
     * @param supplier the supplier to get the value from
     * @param <T>      the type of the value
     * @return the result
     */
    public static <T> @NotNull Result<T> of(Supplier<T> supplier) {
        try {
            if (supplier == null) {
                return new Err<>(new NullPointerException("Supplier for Result.of(supplier) is null"));
            }
            T value = supplier.get();
            if (value == null) {
                return new Err<>(new NullPointerException("Getting Supplier for Result.of(supplier) returned null"));
            }
            return new Ok<>(value);
        } catch (Throwable error) {
            return new Err<>(error);
        }
    }

    /**
     * If the result is {@link Ok} maps the value of Ok to a new Value or returns the {@link Err}
     * <br><br>
     * This follows the pattern of <a href="https://blog.logrocket.com/what-is-railway-oriented-programming/">railway oriented programming.</a>
     *
     * @param mapper the function to map the value
     * @return the new Result
     */
    public <U> @NotNull Result<U> mapOk(Function<T, U> mapper) {
        return switch (this) {
            case Ok<T> success -> {
                if (mapper == null) {
                    yield new Err<>(new NullPointerException("Mapper for Result.mapOk(mapper) is null"));
                }
                try {
                    U result = mapper.apply(success.getValue());
                    if (result == null) {
                        yield new Err<>(new NullPointerException("Mapper for Result.mapOk(mapper) returned null"));
                    }
                    yield new Ok<>(result);
                } catch (Throwable throwable) {
                    yield new Err<>(throwable);
                }
            }
            case Err<T> failure -> new Err<>(failure.getError());
        };
    }

    /**
     * If the result is {@link Ok} maps the value of Ok to a new Result or returns the {@link Err}
     *
     * @param mapper the function to map the value
     * @return the new Result
     */
    public <U> @NotNull Result<U> flatMapOk(Function<T, Result<U>> mapper) {
        return switch (this) {
            case Ok<T> success -> {
                if (mapper == null) {
                    yield new Err<>(new NullPointerException("Mapper for Result.flatMapOk(mapper) is null"));
                }
                try {
                    Result<U> result = mapper.apply(success.getValue());
                    if (result == null) {
                        yield new Err<>(new NullPointerException("Mapper for Result.flatMapOk(mapper) returned null"));
                    }
                    yield result;
                } catch (Throwable throwable) {
                    yield new Err<>(throwable);
                }
            }
            case Err<T> failure -> new Err<>(failure.getError());
        };
    }

    /**
     * If the result is {@link Err} maps the error of Err to a new Value or returns the {@link Ok}
     * <br><br>
     * This is the possibility to switch back into the happy path
     *
     * @param mapper the function to map the error
     * @return the new Result
     */
    public @NotNull Result<T> mapErr(Function<Throwable, T> mapper) {
        return switch (this) {
            case Ok<T> success -> success;
            case Err<T> failure -> {
                if (mapper == null) {
                    yield new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) is null"));
                }
                try {
                    T result = mapper.apply(failure.getError());
                    if (result == null) {
                        yield new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) returned null"));
                    }
                    yield new Ok<>(result);
                } catch (Throwable throwable) {
                    yield new Err<>(throwable);
                }
            }
        };
    }

    /**
     * If the result is {@link Err} maps the error of Err to a new Result or returns the {@link Ok}
     * <br><br>
     * This is the possibility to switch back into the happy path
     *
     * @param mapper the function to map the error
     * @return the new Result
     */
    public @NotNull Result<T> flatMapErr(Function<Throwable, Result<T>> mapper) {
        return switch (this) {
            case Ok<T> success -> success;
            case Err<T> failure -> {
                if (mapper == null) {
                    yield new Err<>(new NullPointerException("Mapper for Result.flatMapErr(mapper) is null"));
                }
                try {
                    Result<T> result = mapper.apply(failure.getError());
                    if (result == null) {
                        yield new Err<>(new NullPointerException("Mapper for Result.flatMapError(mapper) returned null"));
                    }
                    yield result;
                } catch (Throwable throwable) {
                    yield new Err<>(throwable);
                }
            }
        };
    }

    /**
     * Applies a map function to the result in a safe way (catches any exceptions as {@link Err})
     *
     * @param mapper the function to map the result
     * @return the mapped result
     */
    public <U> @NotNull Result<U> map(Function<Result<T>, Result<U>> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.map(mapper) is null"));
        }
        try {
            Result<U> result = mapper.apply(this);
            if (result == null) {
                return new Err<>(new NullPointerException("Mapper for Result.map(mapper) returned null"));
            }
            return result;
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    /**
     * Sends a throwable to the consumer when the Result is an{@link Err}
     *
     * @param consumer the consumer to send the throwable to
     * @return the current result
     */
    public @NotNull Result<T> doOnErr(Consumer<Throwable> consumer) {
        if (this instanceof Err<T> ex) {
            try {
                consumer.accept(ex.getError());
            } catch (Throwable e) {
                LOGGER.error("Error thrown in consumer of Result.doOnErr(consumer)", e);
            }
        }
        return this;
    }

    /**
     * Sends a value to the consumer when the Result is an{@link Ok}
     *
     * @param consumer the consumer to send the value to
     * @return the current result
     */
    public @NotNull Result<T> doOnOk(Consumer<T> consumer) {
        if (this instanceof Ok<T> ex) {
            try {
                consumer.accept(ex.getValue());
            } catch (Throwable e) {
                LOGGER.error("Error thrown in consumer of Result.doOnOk(consumer)", e);
            }
        }
        return this;
    }

    /**
     * If the result is {@link Ok} maps the value of Ok to a new {@link Some} or logs the error and returns {@link None}
     *
     * @return the new Option
     */
    public Option<T> asOption() {
        return switch (this) {
            case Ok<T> success -> new Some<>(success.getValue());
            case Err<T> failure -> {
                LOGGER.error("Error dropped when converting Result.asOption()", failure.getError());
                yield new None<>();
            }
        };
    }

    public boolean isPresent() {
        return this instanceof Ok;
    }
}

