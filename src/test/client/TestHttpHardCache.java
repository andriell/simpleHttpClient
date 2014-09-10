package test.client;

import http.client.HttpHardCache;
import http.client.HttpRequestProcess;
import http.datatypes.HttpUrl;
import test.Test;

import java.io.File;

/**
 * Created by arybalko on 10.09.14.
 */
public class TestHttpHardCache {
    public static void main(String[] args) {
        try {
            new TestHttpHardCache().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        String dirName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "cache-test";
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        String t1 = "1234567890";

        HttpHardCache cache = new HttpHardCache(dir);

        HttpUrl url1 = new HttpUrl("https://www.google.ru/a/b/c/d/e/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        HttpUrl url2 = new HttpUrl("https://www.google.ru/a/b/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");

        HttpRequestProcess requestProcess1 = new HttpRequestProcess();
        HttpRequestProcess requestProcess2 = new HttpRequestProcess();
        requestProcess1.setUrl(url1);
        requestProcess2.setUrl(url2);

        cache.save(requestProcess1, t1.getBytes(), 1);
        String r1 = new String(cache.get(requestProcess1));
        Test.t(r1.equals(t1), true, "Save1");

        /*cache.save(requestProcess2, t1.getBytes(), -1);
        byte[] r2 = cache.get(requestProcess2);
        Test.t(r2 == null, true, "Save2");*/
    }
}
