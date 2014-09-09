package http.client;

import http.datatypes.HttpUrl;
import http.datatypes.HttpUrlSheme;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by arybalko on 02.09.14.
 */
public class DefaultSoketFactory implements SocketFactory {
    @Override
    public synchronized Socket connection(HttpUrl url) throws IOException {
        if (url.getScheme().equals(HttpUrlSheme.https)) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            // Насамом деле это класс SSLSocket
            return sslsocketfactory.createSocket(url.getDomain().toString(), url.getPort());
        }
        return new Socket(url.getDomain().toString(), url.getPort());
    }
}
