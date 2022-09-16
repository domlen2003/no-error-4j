package cc.notabot.noerror4j.result;

public final class Ok<T> extends Result<T> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}