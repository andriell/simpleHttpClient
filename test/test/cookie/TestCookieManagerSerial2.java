package test.cookie;

import http.cookie.Cookie;
import http.cookie.CookieManagerSerial;
import http.datatypes.Domain;
import http.datatypes.Path;

import java.io.File;


/**
 * Created by arybalko on 29.08.14.
 */
public class TestCookieManagerSerial2 {
    CookieManagerSerial cookieManager;

    public static void main(String[] args) {
        try {
            new TestCookieManagerSerial2().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator + "data" + File.separator + "go2.bin";
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.createNewFile();

        String user = "user1";


        /*
Set-Cookie: return_token=kFHKNBACsLzb9yIXd5sxvBeOp4qtrUCC; expires=Wed, 23-Sep-2015 14:35:03 GMT; Max-Age=31536000; path=/; domain=.mamba.ru; httponly
Set-Cookie: mmbUID=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly
Set-Cookie: UID=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly
Set-Cookie: mmbSECRET=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly
Set-Cookie: SECRET=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly
Set-Cookie: LEVEL=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly
         */

        Domain domain = new Domain("mamba.ru".getBytes());
        Cookie cookie10 =  new Cookie("UID=1209419715; expires=Wed, 23-Sep-2015 13:21:15 GMT; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie12 =  new Cookie("SECRET=N8HZdaGISj18CGIo; expires=Wed, 23-Sep-2015 13:21:15 GMT; path=/; domain=.mamba.ru; httponly".getBytes());

        Cookie cookie21 =  new Cookie("return_token=kFHKNBACsLzb9yIXd5sxvBeOp4qtrUCC; expires=Wed, 23-Sep-2015 14:35:03 GMT; Max-Age=31536000; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie22 =  new Cookie("mmbUID=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie23 =  new Cookie("UID=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie24 =  new Cookie("mmbSECRET=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie25 =  new Cookie("SECRET=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly".getBytes());
        Cookie cookie26 =  new Cookie("LEVEL=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; domain=.mamba.ru; httponly".getBytes());



        cookieManager = new CookieManagerSerial(fileName);

        cookieManager.set(user, domain, cookie10);
        cookieManager.set(user, domain, cookie12);

        cookieManager.set(user, domain, cookie21);
        cookieManager.set(user, domain, cookie22);
        cookieManager.set(user, domain, cookie23);
        cookieManager.set(user, domain, cookie24);
        cookieManager.set(user, domain, cookie25);
        cookieManager.set(user, domain, cookie26);

        Iterable<Cookie> cookieIterable = cookieManager.get(user, domain, new Path("/".getBytes()), false);
        for (Cookie cookie: cookieIterable) {
            System.out.println(cookie);
        }

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
