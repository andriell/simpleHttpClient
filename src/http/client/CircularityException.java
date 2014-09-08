package http.client;

import http.datatypes.HttpUrl;

/**
 * Created by arybalko on 08.09.14.
 */
public class CircularityException extends Exception {
    public CircularityException() {
        super();
    }

    public CircularityException(String message) {
        super(message);
    }

    public CircularityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CircularityException(Throwable cause) {
        super(cause);
    }

    protected CircularityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CircularityException(HttpUrl from, HttpUrl to) {
        super(from + " > " + to);
    }
}
