package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public class HttpExceptionHandlerPrint implements HttpExceptionHandler {
    @Override
    public void exception(Exception e, HttpRequestProcess httpRequestProcess) {
        System.out.println(e + " " + httpRequestProcess.getUrl());
    }
}
