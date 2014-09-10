package http.client;

import http.datatypes.HttpUrl;
import http.datatypes.HttpUrlSheme;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * Created by arybalko on 02.09.14.
 */
public class HttpSoketFactoryDefault implements HttpSocketFactory {
    // Максимальное время установления соединения в секундах
    private int connectionTimeout = 1000;
    private Proxy proxy;

    @Override
    public synchronized Socket socket(HttpRequestProcess requestProcess) throws IOException {
        Socket socket;
        if (proxy == null) {
            socket = new Socket();
        } else {
            socket = new Socket(proxy);
        }

        HttpUrl url = requestProcess.getUrl();
        String domain = url.getDomain().toString();
        int port = url.getPort();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(domain, port);
        socket.connect(inetSocketAddress, connectionTimeout);

        if (url.getScheme().equals(HttpUrlSheme.https)) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            // Насамом деле это класс SSLSocket
            return sslsocketfactory.createSocket(socket, domain, port, true);
        }
        return socket;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
