package test.client;

import http.client.HttpClientCacheDir;
import http.client.HttpRequestProcess;
import http.datatypes.HttpUrl;
import test.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by arybalko on 10.09.14.
 */
public class TestHttpHardCache {
    public static void main(String[] args) {
        try {
            new TestHttpHardCache().go();
            new TestHttpHardCache().testDelete();
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

        HttpClientCacheDir cache = new HttpClientCacheDir(dir);

        HttpRequestProcess requestProcess1 = new HttpRequestProcess();
        HttpUrl url1 = new HttpUrl("https://www.google.ru/a/b/c/d/e/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        requestProcess1.setUrl(url1);

        HttpRequestProcess requestProcess2 = new HttpRequestProcess();
        HttpUrl url2 = new HttpUrl("https://www.google.ru/a/b/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        requestProcess2.setUrl(url2);

        cache.save(requestProcess1, t1.getBytes(), null, 1);
        String r1 = cache.get(requestProcess1);
        Test.t(r1.equals(t1), true, "Save1");

        cache.save(requestProcess2, t1.getBytes(), "UTF-8".getBytes(), -1);
        String r2 = cache.get(requestProcess2);
        Test.t(r2 == null, true, "Save2");
    }

    void testDelete() throws Exception {
        String dirName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "cache-test-delete";
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        FileOutputStream outputStream = new FileOutputStream(dirName + File.separator + "test.txt");
        outputStream.write("test".getBytes());
        outputStream.close();

        HttpClientCacheDir cache = new HttpClientCacheDir(dir);

        add(cache, "https://www.google.ru/a/b/c/d/e/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        add(cache, "https://www.google.ru/a/b/c/d/e/0.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        add(cache, "https://www.google.ru/a/b/c/d/e/1.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        add(cache, "https://www.google.ru/a/b/c/d/index.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        add(cache, "https://www.google.ru/a/b/c/d/1.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");
        add(cache, "https://www.google.ru/a/b/c/d/in2dex.php?gws_rd=ssl&newwindow=1&q=%D1%8E%D1%82%D1%83%D0%B1%D0%B5");

        cache.clearDir();

        File[] files = new File(dirName).listFiles();
        Test.t(files.length == 1 && files[0].getName().equals("test.txt"), true, "Delete");
    }

    void add(HttpClientCacheDir cache, String u) throws Exception {
        HttpRequestProcess requestProcess = new HttpRequestProcess();
        HttpUrl url = new HttpUrl(u);
        requestProcess.setUrl(url);
        cache.save(requestProcess, "1234567890".getBytes(), "UTF-8".getBytes(), 0);
    }
}
