package com.github.domlen2003.noerror4j.result;

@SuppressWarnings("unused")
public final class Err<T> extends Result<T> {
    private final Throwable error;

    public Err(Throwable error) {
        this.error = error;
    }

    public Err(String error) {
        this(new RuntimeException(error));
    }

    public Throwable getError() {
        return error;
    }
}