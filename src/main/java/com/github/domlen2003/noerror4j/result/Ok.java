package com.github.domlen2003.noerror4j.result;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Ok<T> extends Result<T> {
    private final T value;

    public Ok(@NotNull T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}