package cc.notabot.noerror4j.result;

public final class Err<T> extends Result<T> {
    private final Throwable error;

    public Err(Throwable error) {
        this.error = error;
    }

    public Err(String error) {
        this.error = new RuntimeException(error);
    }

    public Throwable getError() {
        return error;
    }
}