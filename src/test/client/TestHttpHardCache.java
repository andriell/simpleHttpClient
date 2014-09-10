package test.client;

import http.client.HttpHardCache;
import http.client.HttpRequestProcess;
import http.datatypes.HttpUrl;

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

        HttpUrl url = new HttpUrl("https://www.google.ru/a/b/c/d/e/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");

        HttpRequestProcess requestProcess = new HttpRequestProcess();
        requestProcess.setUrl(url);
        File fileCache = cache.fileCache(requestProcess);

        cache.save(requestProcess, t1.getBytes(), 1);
        System.out.println(new String(cache.get(requestProcess)));

        System.out.println(fileCache);
        int time = (int) (System.currentTimeMillis() / 60000L);

        System.out.println(System.currentTimeMillis() / 1000);
        System.out.println(time);
    }
}
