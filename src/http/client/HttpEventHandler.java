package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public interface HttpEventHandler {
    public void on(HttpRequestProcess httpRequestProcess);
}
