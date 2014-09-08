package test.client;

import http.client.HttpClient;
import http.client.HttpExceptionHandlerPrint;
import http.cookie.CookieManagerSerial;

import java.io.File;

/**
 * Created by arybalko on 08.09.14.
 */
public class TestHttpClient {
    public static void main(String[] args) {
        try {
            new TestHttpClient().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "TestHttpRequestProcess.bin";
        CookieManagerSerial cookieManager = new CookieManagerSerial(fileName);
        if (new File(fileName).isFile()) {
            cookieManager.load();
        }
        HttpExceptionHandlerPrint httpExceptionHandlerPrint = new HttpExceptionHandlerPrint();

        HttpClient.getInstance().setCookieManager(cookieManager);
        HttpClient.getInstance().setExceptionHandler(httpExceptionHandlerPrint);

        String s = HttpClient.getInstance().getString("", "http://vk.com");
        System.out.println(s);

        cookieManager.save();
    }
}
