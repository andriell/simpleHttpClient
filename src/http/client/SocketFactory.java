package http.client;

import http.datatypes.HttpUrl;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by arybalko on 09.09.14.
 */
public interface SocketFactory {
    public Socket socket(HttpUrl url) throws IOException;
}
