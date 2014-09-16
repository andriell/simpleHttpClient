package test;

import http.datatypes.HttpUrl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * Created by arybalko on 01.09.14.
 */
public class TestProxy {
    public static void main(String[] args) {
        try {
            new TestProxy().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws Exception {
        HttpUrl url = new HttpUrl("https://www.google.ru");
        System.out.println(url.getDomain());
        System.out.println(url.getPort());
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.250.1.6", 3128));

        Socket socket = new Socket(proxy);
        socket.connect(new InetSocketAddress(url.getDomain().toString(), url.getPort()));
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // "10.250.1.6", 3128
        // url.getDomain().toString(), url.getPort()
        socket = (SSLSocket) sslsocketfactory.createSocket(socket, url.getDomain().toString(), url.getPort(), true);

        String header = "GET / HTTP/1.1\r\n" +
                "Host: www.google.ru\r\n" +
                "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
                "accept-encoding: deflate\r\n" +
                "accept-language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4\r\n"+
                "cache-control: no-cache\r\n"+
                "pragma: no-cache\r\n"+
                "user-agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.102 Safari/537.36\r\n"+
                "x-request-data: CIa2yQEIpbbJAQiptskBCMS2yQEInobKAQi4iMoBCO6IygEI0ZTKAQ==\r\n" +
                "\r\n";

        InputStream inputStream = socket.getInputStream();
        OutputStream outputstream = socket.getOutputStream();

        outputstream.write(header.getBytes());

        byte[] bytes = new byte[1000];

        for(int i = 0; i < 1000; i++) {
            byte b = (byte) inputStream.read();
            bytes[i] = b;
        }

        System.out.println(new String(bytes));
    }
}
