package com.github.domlen2003.noerror4j.option;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class OptionTest {
    @Before
    public void setUp() {
        Option.setErrorSink((msg, err) -> {
        });
    }

    @Test
    public void setErrorSink() {
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> errorThrown.set(true));
        Option.sinkError("Error", new RuntimeException("Error"));
        assertTrue(errorThrown.get());
    }

    @Test
    public void of() {
        //Null
        Option<String> option = Option.of(null);
        assertTrue(option instanceof None);
        //Null optional
        Option<String> none = Option.of(Optional.empty());
        assertTrue(none instanceof None);
        //Not null optional
        Option<String> some = Option.of(Optional.of("Some String"));
        assertTrue(some instanceof Some);
    }

    @Test
    public void map() {
        //Null mapper
        Option<String> nullMap = Some.of("Some String").map(null);
        assertTrue(nullMap instanceof None);
        //Null mapper return
        Option<String> nullReturn = Some.of("Some String").map(prev -> null);
        assertTrue(nullReturn instanceof None);
        //Error mapper return
        AtomicBoolean errorThrown = new AtomicBoolean(false);
        Option.setErrorSink((msg, err) -> errorThrown.set(true));
        Option<String> errorReturn = Some.of("Some String").map(prev -> {
            throw new RuntimeException("Error");
        });
        assertTrue(errorReturn instanceof None);
        assertTrue(errorThrown.get());
        //Valid mapper return
        Option<String> validReturn = None.instance().map(prev -> Some.of("Some String"));
        assertTrue(validReturn instanceof Some);
    }
}
