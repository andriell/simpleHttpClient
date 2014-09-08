package test.header;

import http.cookie.Cookie;
import http.datatypes.HttpRequestMethod;
import http.datatypes.HttpUrl;
import http.header.HttpHeaderRequest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by arybalko on 02.09.14.
 */
public class TestHttpHeaderRequest {
    public static void main(String[] args) {
        try {
            new TestHttpHeaderRequest().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        HttpUrl url = new HttpUrl("http://www.google.com/search?q=Coocie&ie=utf-8&oe=utf-8&rls=org.mozilla:ru:official&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM");
        HttpHeaderRequest rquest = new HttpHeaderRequest();
        rquest.setMethod(HttpRequestMethod.GET);
        rquest.url(url);
        //rquest.set(HttpHeaders.acceptEncoding, "deflate,sdch");
        //rquest.set(HttpHeaders.acceptEncoding, "deflate");
        rquest.addCookie(new Cookie("lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        rquest.addCookie(new Cookie("lang2=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        rquest.addCookie(new Cookie("lang2=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        System.out.println(new String(rquest.getBytes()));

        Socket socket = new Socket(url.getDomain().toString(), url.getPort());
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(rquest.getBytes());


        byte[] r = new byte[1000];
        for (int i = 0; i < r.length; i++) {
            r[i] = (byte) inputStream.read();
            if (r[i] == -1) {
                break;
            }
        }

        System.out.println(new String(r));
    }
}
