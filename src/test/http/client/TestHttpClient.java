package test.http.client;

import http.client.HttpExceptionHandlerPrint;
import http.client.HttpTransaction;
import http.cookie.CookieManagerSerial;
import http.datatypes.ContentType;
import http.datatypes.HttpUrl;
import http.client.HttpClient;
import http.header.HttpHeaders;
import http.stream.output.HttpPartOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * Created by arybalko on 04.09.14.
 */
public class TestHttpClient {
    CookieManagerSerial cookieManager;
    HttpExceptionHandlerPrint httpExceptionHandlerPrint;

    public static void main(String[] args) {
        try {
            new TestHttpClient().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "TestHttpClient.bin";
        cookieManager = new CookieManagerSerial(fileName);
        if (new File(fileName).isFile()) {
            cookieManager.load();
        }

        httpExceptionHandlerPrint = new HttpExceptionHandlerPrint();

        print("http://google.ru/");
        print("http://google.ru/");
        print("http://ya.ru/");
        print("http://vk.com/");

        download("http://vk.com/", "vk.html");
        download("http://i.msdn.microsoft.com/dynimg/IC52612.gif", "IC52612.gif");

        cookieManager.save();
    }

    void print(String url) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.setCookieManager(cookieManager);
        httpClient.setExceptionHandler(httpExceptionHandlerPrint);

        httpClient.setUrl(new HttpUrl(url.getBytes()));

        HttpPartOutputStream httpPartOutputStream = new HttpPartOutputStream();
        httpClient.setOutputStream(httpPartOutputStream);
        httpClient.run();
        Iterator<HttpTransaction> transactions = httpClient.getTransactions();
        while (transactions.hasNext()) {
            System.out.println(transactions.next());
        }

        byte[] contentType = httpClient.getLastTransaction().getHeaderResponse().get(HttpHeaders.contentType);
        if (contentType != null) {
            String charsetName = ContentType.getCharset(contentType);
            if (charsetName != null) {
                System.out.println(new String(httpPartOutputStream.getBytes(), charsetName));
                System.out.println();
                return;
            }
        }
        System.out.println(new String(httpPartOutputStream.getBytes()));
        System.out.println();
    }

    void download(String url, String file) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.setCookieManager(cookieManager);
        httpClient.setExceptionHandler(httpExceptionHandlerPrint);

        httpClient.setUrl(new HttpUrl(url.getBytes()));

        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + file;
        File file1 = new File(fileName);
        if (file1.isFile()) {
            file1.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        httpClient.setOutputStream(fileOutputStream);
        httpClient.run();
        Iterator<HttpTransaction> transactions = httpClient.getTransactions();
        while (transactions.hasNext()) {
            System.out.println(transactions.next());
        }

        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
