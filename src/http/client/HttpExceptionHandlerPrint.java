package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public class HttpExceptionHandlerPrint implements HttpExceptionHandler {
    @Override
    public void exception(Exception e, HttpClient httpClient) {
        System.out.println(e + " " + httpClient.getUrl());
    }
}
