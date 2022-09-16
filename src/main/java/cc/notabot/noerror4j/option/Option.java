package cc.notabot.noerror4j.option;

import cc.notabot.noerror4j.result.Err;
import cc.notabot.noerror4j.result.Ok;
import cc.notabot.noerror4j.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public sealed abstract class Option<T> permits None, Some {
    private static final Logger LOGGER = LoggerFactory.getLogger(Option.class);

    /**
     * Creates a new Option of a nullable value
     *
     * @param value the value to wrap
     * @return a {@link Some} if the value is not null, a {@link None} otherwise
     */
    @Contract("_ -> new")
    public static <T> @NotNull Option<T> of(@Nullable T value) {
        return value == null ? new None<>() : new Some<>(value);
    }

    /**
     * Maps the Value of the current Option if Present
     *
     * @param mapper the mapper to apply
     * @return a new {@link Some} if the mapper, mapped-value and previous value are not null, a {@link None} otherwise
     */
    public <U> @NotNull Option<? extends U> mapSome(Function<? super T, ? extends U> mapper) {
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
    public <U> @NotNull Option<? extends U> flatMapSome(Function<? super T, ? extends Option<? extends U>> mapper) {
        return switch (this) {
            case Some<T> some -> {
                if (mapper == null) {
                    yield new None<>();
                }
                try {
                    Option<? extends U> option = mapper.apply(some.getValue());
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
     * Returns the current Option if it is a {@link Some}, otherwise returns the supplied Option.
     *
     * @param supplier the supplier to get the value from
     * @return a {@link Some} if any of the two Options has a value, else a {@link None}
     */
    public @NotNull Option<? extends T> mapNone(Supplier<? extends Option<? extends T>> supplier) {
        return switch (this) {
            case None<T> ignored -> {
                if (supplier == null) {
                    yield new None<>();
                }
                Option<? extends T> option = supplier.get();
                if (option == null) {
                    yield new None<>();
                }
                yield option;
            }
            case Some<T> some -> some;
        };
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
