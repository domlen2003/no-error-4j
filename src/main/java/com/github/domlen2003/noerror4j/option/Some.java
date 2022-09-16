package com.github.domlen2003.noerror4j.option;

public final class Some<T> extends Option<T> {
    private final T value;

    public Some(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}