package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public class HttpExceptionHandlerPrint implements HttpExceptionHandler {
    @Override
    public void exception(Exception e, HttpRequestProcess httpRequestProcess) {
        System.err.println(httpRequestProcess.getUrl());
        e.printStackTrace();
    }
}
