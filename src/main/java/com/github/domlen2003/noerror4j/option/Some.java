package com.github.domlen2003.noerror4j.option;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Some<T> extends Option<T> {
    private final T value;

    public Some(@NotNull T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}