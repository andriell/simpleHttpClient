package test.client;

import http.client.LoopException;
import http.client.RedirectManagerSerial;
import http.datatypes.HttpUrl;

import java.io.File;

/**
 * Created by arybalko on 08.09.14.
 */
public class TestRedirectManagerSerial {
    public static void main(String[] args) {
        try {
            new TestRedirectManagerSerial().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        String fileRedirect = System.getProperty("user.dir") + File.separator + "data" + File.separator + "TestRedirectManagerSerial-redirect.bin";
        RedirectManagerSerial redirectManager = new RedirectManagerSerial(fileRedirect);
        if (new File(fileRedirect).isFile()) {
            redirectManager.load();
            redirectManager.printAll();
        }
        HttpUrl a1 = new HttpUrl("http://a.ru");
        HttpUrl a2 = new HttpUrl("http://a.ru/");
        HttpUrl b = new HttpUrl("http://b.ru/");
        HttpUrl c = new HttpUrl("http://c.ru/");
        HttpUrl d = new HttpUrl("http://d.ru/");

        if (redirectManager.get(d).equals(d)) {
            System.out.println("Ok.");
        } else {
            System.out.println("Error.");
        }

        redirectManager.set(a1, b);
        redirectManager.set(b, c);
        redirectManager.set(c, d);
        try {
            redirectManager.set(d, a2);
            System.out.println("Error.");
        } catch (LoopException e) {
            System.out.println("Ok. " + e.getMessage());
        }

        try {
            redirectManager.setFindLoop(false);
            redirectManager.set(d, a2);
            System.out.println("Ok.");
        } catch (LoopException e) {
            System.out.println("Error.");
        }

        if (redirectManager.get(d).equals(a1)) {
            System.out.println("Ok.");
        } else {
            System.out.println("Error.");
        }

        redirectManager.save();
    }
}
