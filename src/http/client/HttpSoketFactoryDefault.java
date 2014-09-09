package http.client;

import http.datatypes.HttpUrl;
import http.datatypes.HttpUrlSheme;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by arybalko on 02.09.14.
 */
public class HttpSoketFactoryDefault implements HttpSocketFactory {
    private String proxyHost;
    private int proxyPort;

    @Override
    public synchronized Socket socket(HttpRequestProcess requestProcess) throws IOException {
        HttpUrl url = requestProcess.getUrl();
        String host = proxyHost;
        int port = proxyPort;
        if (proxyHost == null) {
            host = url.getDomain().toString();
            port = url.getPort();
        }

        if (url.getScheme().equals(HttpUrlSheme.https)) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            // Насамом деле это класс SSLSocket
            return sslsocketfactory.createSocket(proxyHost, proxyPort);

        }
        return new Socket(proxyHost, proxyPort);
    }

    public void setProxy(String host, int port) {
        proxyHost = host;
        proxyPort = port;
    }

    public void unsetProxy() {
        proxyHost = null;
        proxyPort = 0;
    }
}
