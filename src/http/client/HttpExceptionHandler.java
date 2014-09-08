package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public interface HttpExceptionHandler {
    public void exception(Exception e, HttpRequestProcess httpRequestProcess);
}
