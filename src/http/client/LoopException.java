package http.client;

import http.datatypes.HttpUrl;

/**
 * Created by arybalko on 08.09.14.
 */
public class LoopException extends Exception {
    public LoopException() {
        super();
    }

    public LoopException(String message) {
        super(message);
    }

    public LoopException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoopException(Throwable cause) {
        super(cause);
    }

    protected LoopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LoopException(HttpUrl from, HttpUrl to) {
        super(from + " > " + to);
    }
}
