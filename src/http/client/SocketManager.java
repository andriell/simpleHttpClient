package http.client;

import http.datatypes.HttpUrl;
import http.datatypes.HttpUrlSheme;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;

/**
 * Created by arybalko on 02.09.14.
 */
public class SocketManager {
    public static synchronized Socket get(HttpUrl url) throws Exception {
        if (url.getScheme().equals(HttpUrlSheme.https)) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(url.getDomain().toString(), url.getPort());
            return sslsocket;
        }
        return new Socket(url.getDomain().toString(), url.getPort());
    }
}
