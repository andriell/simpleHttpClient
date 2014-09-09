package test.client;

import http.client.*;
import http.cookie.CookieManagerSerial;
import http.datatypes.ContentType;
import http.datatypes.HttpUrl;
import http.header.HttpHeaders;
import http.stream.output.HttpPartOutputStream;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by arybalko on 04.09.14.
 */
public class TestHttpRequestProcess {
    SocketFactory socketFactory;
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
        //HttpHeaderRequest.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.103 Safari/537.36");

        socketFactory = new DefaultSoketFactory();

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

//        print("https://google.ru/");
        print("http://ya.ru/");
        print("http://vk.com/");

        download("http://vk.com/", "vk.html");
        download("http://i.msdn.microsoft.com/dynimg/IC52612.gif", "IC52612.gif");

        System.out.println("Сохранено кук " + cookieManager.save());
        System.out.println("Сохранено редиректов " + redirectManager.save());
    }

    void print(String url) throws Exception {
        HttpRequestProcess httpRequestProcess = new HttpRequestProcess();
        httpRequestProcess.setSocketFactory(socketFactory);
        httpRequestProcess.setCookieManager(cookieManager);
        httpRequestProcess.setRedirectManager(redirectManager);
        httpRequestProcess.setExceptionHandler(httpExceptionHandlerPrint);
        httpRequestProcess.beforeRedirect(new TestEventHandler("Redirect"));
        httpRequestProcess.beforeComplite(new TestEventHandler("Complite"));
        httpRequestProcess.beforeComplite(new TestEventHandler("Complite 2"));

        httpRequestProcess.setUrl(new HttpUrl(url.getBytes()));

        HttpPartOutputStream httpPartOutputStream = new HttpPartOutputStream();
        httpRequestProcess.setOutputStream(httpPartOutputStream);
        httpRequestProcess.run();

        byte[] contentType = httpRequestProcess.getHeaderResponse().get(HttpHeaders.contentType);
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
        httpRequestProcess.setSocketFactory(socketFactory);
        httpRequestProcess.setCookieManager(cookieManager);
        httpRequestProcess.setRedirectManager(redirectManager);
        httpRequestProcess.setExceptionHandler(httpExceptionHandlerPrint);
        TestEventHandler handler = new TestEventHandler("Complite");
        httpRequestProcess.beforeRedirect(new TestEventHandler("Redirect"));
        httpRequestProcess.beforeComplite(handler);
        httpRequestProcess.beforeComplite(handler);

        httpRequestProcess.setUrl(new HttpUrl(url.getBytes()));

        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + file;
        File file1 = new File(fileName);
        if (file1.isFile()) {
            file1.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        httpRequestProcess.setOutputStream(fileOutputStream);
        httpRequestProcess.run();

        fileOutputStream.flush();
        fileOutputStream.close();
    }

    class TestEventHandler extends HttpEventHandler {
        private String name;

        TestEventHandler(String name) {
            this.name = name;
        }

        @Override
        public void on(HttpRequestProcess httpRequestProcess) {
            System.out.print(name + " >>> " + httpRequestProcess.getHeaderRequest() + name +" <<< " + httpRequestProcess.getHeaderResponse());
        }
    }
}
