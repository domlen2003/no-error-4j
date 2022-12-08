package com.github.domlen2003.noerror4j.option;


import com.github.domlen2003.noerror4j.result.Err;
import com.github.domlen2003.noerror4j.result.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class NoneTest {
    @Before
    public void setUp() {
        Option.setErrorSink((msg, err) -> {
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void instantiation() {
        Option<String> none = None.instance();
        assertFalse(none.isPresent());
        assertTrue(none instanceof None<String>);
    }

    @Test
    public void mapSome() {
        Option<String> mapped = None.instance().mapSome(prev -> "Some String");
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
    }

    @Test
    public void flatMapSome() {
        Option<String> mapped = None.instance().flatMapSome(prev -> Some.of("Some String"));
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
    }

    @Test
    public void mapNone() {
        //Null mapper
        Option<String> mapped = None.<String>instance().mapNone(null);
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        //Null mapper return
        mapped = None.<String>instance().mapNone(() -> null);
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        //Error mapper return
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> {
            errorThrown.set(true);
        });
        mapped = None.<String>instance().mapNone(() -> {
            throw new RuntimeException("Error");
        });
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        assertTrue(errorThrown.get());
        //Valid mapper return
        mapped = None.<String>instance().mapNone(() -> "Some String");
        assertTrue(mapped.isPresent());
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some String"));
    }

    @Test
    public void flatMapNone() {
        //Null mapper
        Option<String> mapped = None.<String>instance().flatMapNone(null);
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        //Null mapper return
        mapped = None.<String>instance().flatMapNone(() -> null);
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        //Error mapper return
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> {
            errorThrown.set(true);
        });
        mapped = None.<String>instance().flatMapNone(() -> {
            throw new RuntimeException("Error");
        });
        assertFalse(mapped.isPresent());
        assertTrue(mapped instanceof None<String>);
        assertTrue(errorThrown.get());
        //Valid mapper return
        mapped = None.<String>instance().flatMapNone(() -> Some.of("Some String"));
        assertTrue(mapped.isPresent());
        assertTrue(mapped instanceof Some<String> someString && someString.getValue().equals("Some String"));
    }

    @Test
    public void doOnNone() {
        None<String> none = None.instance();
        //Null supplier
        try {
            assertEquals(
                    none.doOnNone(null),
                    none
            );
        } catch (Exception e) {
            fail();
        }
        //Error supplier
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> {
            errorThrown.set(true);
        });
        try {
            assertEquals(
                    none.doOnNone(() -> {
                        throw new RuntimeException("Error");
                    }),
                    none
            );
        } catch (Exception e) {
            fail();
        }
        assertTrue(errorThrown.get());
        //Valid supplier
        AtomicBoolean called = new AtomicBoolean(false);
        assertEquals(
                none.doOnNone(() -> called.set(true)),
                none
        );
        assertTrue(called.get());
    }

    @Test
    public void doOnSome() {
        None<String> none = None.instance();
        //Valid supplier
        AtomicBoolean called = new AtomicBoolean(false);
        assertEquals(
                none.doOnNone(() -> called.set(true)),
                none
        );
        assertTrue(called.get());
    }

    @Test
    public void asResult() {
        //None to error map
        None<String> none = None.instance();
        Result<String> result = none.asResult();
        assertTrue(result instanceof Err<String>);
    }
}
