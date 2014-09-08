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
        HttpUrl url = new HttpUrl("http://www.google.ru/search?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&client=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        HttpHeaderRequest rquest = new HttpHeaderRequest(HttpRequestMethod.GET, url);
        //rquest.set(HttpHeaders.acceptEncoding, "deflate,sdch");
        //rquest.set(HttpHeaders.acceptEncoding, "deflate");
        rquest.setCookie(new Cookie("lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        rquest.setCookie(new Cookie("lang2=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        System.out.println(new String(rquest.getByte()));

        Socket socket = new Socket(url.getDomain().toString(), url.getPort());
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(rquest.getByte());


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
