package http.client;

import http.stream.output.HttpHeaderOutputStream;
import http.header.HttpHeaderRequest;

/**
 * Created by arybalko on 04.09.14.
 */
public class HttpTransaction {
    private HttpHeaderRequest headerRequest;
    private HttpHeaderOutputStream headerResponse;

    public HttpHeaderRequest getHeaderRequest() {
        return headerRequest;
    }

    protected void setHeaderRequest(HttpHeaderRequest headerRequest) {
        this.headerRequest = headerRequest;
    }

    public HttpHeaderOutputStream getHeaderResponse() {
        return headerResponse;
    }

    protected void setHeaderResponse(HttpHeaderOutputStream headerResponse) {
        this.headerResponse = headerResponse;
    }

    @Override
    public String toString() {
        return ">>> " + headerRequest + "<<< " + headerResponse;
    }
}
