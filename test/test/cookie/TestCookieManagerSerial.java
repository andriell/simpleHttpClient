package test.cookie;

import http.cookie.Cookie;
import http.cookie.CookieManagerSerial;
import http.datatypes.Domain;
import http.datatypes.Path;

import java.io.File;


/**
 * Created by arybalko on 29.08.14.
 */
public class TestCookieManagerSerial {
    CookieManagerSerial cookieManager;

    public static void main(String[] args) {
        try {
            new TestCookieManagerSerial().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "go.bin";
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.createNewFile();

        String user1 = "user1";
        String user2 = "user2";

        Domain domain0 = new Domain("example.com".getBytes());
        Domain domain1 = new Domain("www.example.com".getBytes());
        Domain subdomain1 = new Domain("ru.example.com".getBytes());


        cookieManager = new CookieManagerSerial(fileName);

        cookieManager.set(user1, domain0, new Cookie("SIP=31d4d96e407aad42; Domain=.example.com; Path=/; Secure; HttpOnly".getBytes()));
        cookieManager.set(user1, domain0, new Cookie("LID2=31d4d96e407aad42; Domain=.example.com; Path=/;".getBytes()));
        cookieManager.set(user1, domain0, new Cookie("LID=31d4d96e407aad42; Domain=.example.com; Path=/a;".getBytes()));
        cookieManager.set(user1, domain0, new Cookie("LID=31d4d96e407aad42; Domain=.example.com; Path=/ab;".getBytes()));
        cookieManager.set(user1, domain0, new Cookie("LID=31d4d96e407aad42; Domain=.example.com; Path=/a/b;".getBytes()));
        cookieManager.set(user1, subdomain1, new Cookie("ruPID=31d4d96e407aad42; Domain=ru.example.com; Path=/;".getBytes()));
        cookieManager.set(user1, subdomain1, new Cookie("ruLID=31d4d96e407aad42; Domain=ru.example.com; Path=/;".getBytes()));
        cookieManager.set(user1, domain0, new Cookie("lang=en-US; Domain=.example.com; Path=/; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        cookieManager.set(user1, subdomain1, new Cookie("lang=en-US; Domain=ru.example.com; Path=/a; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));
        cookieManager.set(user2, domain0, new Cookie("lang=en-US; Domain=.example.com; Path=/; Expires=Wed, 09 Jun 2021 10:18:14 GMT".getBytes()));

        cookieManager.printAll();
        System.out.println();

        Iterable<Cookie> cookieIterator;

        // User
        cookieIterator = cookieManager.get(user2, new Domain("www.example.com".getBytes()), new Path("/".getBytes()), false);
        printTest(cookieIterator, 1, "User");

        // Secure
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/".getBytes()), true);
        printTest(cookieIterator, 3, "Secure");

        // Subdomain
        cookieIterator = cookieManager.get(user1, new Domain("ru.example.com".getBytes()), new Path("/".getBytes()), false);
        printTest(cookieIterator, 4, "Subdomain");

        // Domain
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/".getBytes()), false);
        printTest(cookieIterator, 2, "Domain");

        // Subpath
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/a".getBytes()), false);
        printTest(cookieIterator, 3, "Subpath 1");
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/a/".getBytes()), false);
        printTest(cookieIterator, 3, "Subpath 2");
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/a/0".getBytes()), false);
        printTest(cookieIterator, 3, "Subpath 3");
        cookieIterator = cookieManager.get(user1, new Domain("www.example.com".getBytes()), new Path("/a/b".getBytes()), false);
        printTest(cookieIterator, 4, "Subpath 4");

        // Session end
        printTest(cookieManager.sessionEnd(), 7, "Session end");

        // Save
        printTest(cookieManager.save(), 3, "Save");

        // Load
        cookieManager.load();
        cookieIterator = cookieManager.get(user1, new Domain("ru.example.com".getBytes()), new Path("/a".getBytes()), false);
        printTest(cookieIterator, 2, "Load 1");
        cookieIterator = cookieManager.get(user2, new Domain("www.example.com".getBytes()), new Path("/".getBytes()), false);
        printTest(cookieIterator, 1, "Load 2");

        System.out.println();
        //cookieManager.printAll();
        System.out.println();
    }

    void printTest(boolean val, boolean need) {
        printTest(val, need, "");
    }

    void printTest(Iterable<Cookie> cookieIterable, int need, String comment) {
        int count = count(cookieIterable);
        printTest(count, need, comment);
        if (count != need) {
            print(cookieIterable);
            System.out.println();
        }
    }

    void printTest(int val, int need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment + " Need: " + need  + " Value: " + val);
        }
    }

    void printTest(boolean val, boolean need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment);
        }
    }

    int count(Iterable<Cookie> iterator) {
        int i = 0;
        for (Cookie cookie: iterator) {
            i++;
        }
        return i;
    }

    void print(Iterable<Cookie> cookieIterable) {
        for (Cookie cookie: cookieIterable) {
            System.out.println(cookie);
        }
    }

    void print(String user, Domain domain, Path path, boolean isHttps) {
        System.out.println();
        System.out.println("Get. User=" + user + " Domain=" + domain + " Path=" + path + " isHttp=" + isHttps);
        print(cookieManager.get(user, domain, path, isHttps));
    }
}
