package com.github.domlen2003.noerror4j.option;

import com.github.domlen2003.noerror4j.result.Ok;
import com.github.domlen2003.noerror4j.result.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class SomeTest {
    @Before
    public void setUp() {
        Option.setErrorSink((msg, err) -> {
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void instantiation() {
        Option<String> some = Some.of("Some String");
        assertTrue(some instanceof Some<String>);
        assertTrue(some.isPresent());
    }

    @Test
    public void mapSome() {
        //Null
        Option<String> mapped = Some.of("Some String").mapSome(null);
        assertTrue(mapped instanceof None<String>);
        //Null mapper
        mapped = Some.of("Some String").mapSome(prev -> null);
        assertTrue(mapped instanceof None<String>);
        //Error mapper return
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> errorThrown.set(true));
        mapped = Some.of("Some String").mapSome(prev -> {
            throw new RuntimeException("Error");
        });
        assertTrue(mapped instanceof None<String>);
        assertTrue(errorThrown.get());
        //Valid map
        mapped = Some.of("Some String").mapSome(prev -> "Some other String");
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some other String"));
    }

    @Test
    public void flatMapSome() {
        //Null
        Option<String> mapped = Some.of("Some String").flatMapSome(null);
        assertTrue(mapped instanceof None<String>);
        //Null mapper
        mapped = Some.of("Some String").flatMapSome(prev -> null);
        assertTrue(mapped instanceof None<String>);
        //Error mapper return
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> errorThrown.set(true));
        mapped = Some.of("Some String").flatMapSome(prev -> {
            throw new RuntimeException("Error");
        });
        assertTrue(mapped instanceof None<String>);
        assertTrue(errorThrown.get());
        //Valid map
        mapped = Some.of("Some String").flatMapSome(prev -> Some.of("Some other String"));
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some other String"));
    }

    @Test
    public void mapNone() {
        Option<String> mapped = Some.of("Some String").mapNone(null);
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some String"));
    }

    @Test
    public void flatMapNone() {
        //Null mapper
        Option<String> mapped = Some.of("Some String").flatMapNone(null);
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some String"));
    }

    @Test
    public void doOnSome() {
        Option<String> some = Some.of("Some String");
        //Null supplier
        assertEquals(
                some.doOnSome(null),
                some
        );
        //Error supplier
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> errorThrown.set(true));
        assertEquals(
                some.doOnSome(val -> {
                    throw new RuntimeException("Error");
                }),
                some
        );
        assertTrue(errorThrown.get());
        //Valid supplier
        AtomicBoolean called = new AtomicBoolean(false);
        assertEquals(
                some.doOnSome(val -> called.set(true)),
                some
        );
        assertTrue(called.get());
    }

    @Test
    public void doOnNone() {
        Option<String> none = Some.of("Some String");
        //Valid supplier
        AtomicBoolean called = new AtomicBoolean(false);
        assertEquals(
                none.doOnNone(() -> called.set(true)),
                none
        );
        assertFalse(called.get());
    }

    @Test
    public void asResult() {
        //Some to okr map
        Option<String> some = Some.of("Some String");
        Result<String> result = some.asResult();
        assertTrue(result instanceof Ok<String>);
    }
}
