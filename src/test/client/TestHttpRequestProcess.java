package test.client;

import http.client.HttpExceptionHandlerPrint;
import http.client.HttpTransaction;
import http.client.RedirectManagerSerial;
import http.cookie.CookieManagerSerial;
import http.datatypes.ContentType;
import http.datatypes.HttpUrl;
import http.client.HttpRequestProcess;
import http.header.HttpHeaders;
import http.stream.output.HttpPartOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * Created by arybalko on 04.09.14.
 */
public class TestHttpRequestProcess {
    CookieManagerSerial cookieManager;
    HttpExceptionHandlerPrint httpExceptionHandlerPrint;
    RedirectManagerSerial redirectManager;

    public static void main(String[] args) {
        try {
            new TestHttpRequestProcess().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        String fileCookie = System.getProperty("user.dir") + File.separator + "data" + File.separator + "TestHttpRequestProcess-cookie.bin";
        cookieManager = new CookieManagerSerial(fileCookie);
        if (new File(fileCookie).isFile()) {
            cookieManager.load();

        }

        String fileRedirect = System.getProperty("user.dir") + File.separator + "data" + File.separator + "TestHttpRequestProcess-redirect.bin";
        redirectManager = new RedirectManagerSerial(fileRedirect);
        if (new File(fileRedirect).isFile()) {
            redirectManager.load();
            redirectManager.printAll();
        }

        httpExceptionHandlerPrint = new HttpExceptionHandlerPrint();

        //print("https://google.ru/");
        //print("http://ya.ru/");
        print("http://vk.com/");

        //download("http://vk.com/", "vk.html");
        //download("http://i.msdn.microsoft.com/dynimg/IC52612.gif", "IC52612.gif");

        System.out.println("Сохранено кук " + cookieManager.save());
        System.out.println("Сохранено редиректов " + redirectManager.save());
    }

    void print(String url) throws Exception {
        HttpRequestProcess httpRequestProcess = new HttpRequestProcess();
        httpRequestProcess.setCookieManager(cookieManager);
        //httpRequestProcess.setRedirectManager(redirectManager);
        httpRequestProcess.setExceptionHandler(httpExceptionHandlerPrint);

        httpRequestProcess.setUrl(new HttpUrl(url.getBytes()));

        HttpPartOutputStream httpPartOutputStream = new HttpPartOutputStream();
        httpRequestProcess.setOutputStream(httpPartOutputStream);
        httpRequestProcess.run();
        Iterator<HttpTransaction> transactions = httpRequestProcess.transactionsIterator();
        while (transactions.hasNext()) {
            System.out.println(transactions.next());
        }

        byte[] contentType = httpRequestProcess.getLastTransaction().getHeaderResponse().get(HttpHeaders.contentType);
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
        HttpRequestProcess httpRequestProcess = new HttpRequestProcess();
        httpRequestProcess.setCookieManager(cookieManager);
        httpRequestProcess.setRedirectManager(redirectManager);
        httpRequestProcess.setExceptionHandler(httpExceptionHandlerPrint);

        httpRequestProcess.setUrl(new HttpUrl(url.getBytes()));

        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + file;
        File file1 = new File(fileName);
        if (file1.isFile()) {
            file1.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        httpRequestProcess.setOutputStream(fileOutputStream);
        httpRequestProcess.run();
        Iterator<HttpTransaction> transactions = httpRequestProcess.transactionsIterator();
        while (transactions.hasNext()) {
            System.out.println(transactions.next());
        }

        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
