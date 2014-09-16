package test.cookie;

import http.cookie.CookieDomain;
import http.datatypes.Domain;

/**
 * Created by arybalko on 09.09.14.
 */
public class TestCookieDomain {
    public static void main(String[] args) {
        try {
            new TestCookieDomain().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws Exception {

        /*
        * CookieDomain .example.com
        * Domain example.com
        * Domain www.example.com
        * Domain cs.example.com
        *
        * OR
        *
        * CookieDomain example.com
        * Domain example.com
        */
        CookieDomain cd10 = new CookieDomain(".example.com".getBytes());
        CookieDomain cd20 = new CookieDomain("example.com".getBytes());
        CookieDomain cd30 = new CookieDomain("ru.example.com".getBytes());
        CookieDomain cd40 = new CookieDomain(".com".getBytes());
        CookieDomain cd50 = new CookieDomain("com".getBytes());
        CookieDomain cd60 = new CookieDomain("my-example.com".getBytes());

        Domain d10 = new Domain("localhost".getBytes());
        Domain d20 = new Domain("example.com".getBytes());
        Domain d30 = new Domain(".example.com".getBytes());
        Domain d40 = new Domain("ru.example.com".getBytes());
        Domain d50 = new Domain("my-example.com".getBytes());
        Domain d60 = new Domain("com".getBytes());
        Domain d70 = new Domain(".com".getBytes());

        printTest(cd10.forDomain(d10), false, "10 - 10");
        printTest(cd10.forDomain(d20), true, "10 - 20");
        printTest(cd10.forDomain(d30), false, "10 - 30");
        printTest(cd10.forDomain(d40), true, "10 - 40");
        printTest(cd10.forDomain(d50), false, "10 - 50");
        printTest(cd10.forDomain(d60), false, "10 - 60");
        printTest(cd10.forDomain(d70), false, "10 - 70");

        printTest(cd20.forDomain(d10), false, "20 - 10");
        printTest(cd20.forDomain(d20), true, "20 - 20");
        printTest(cd20.forDomain(d30), false, "20 - 30");
        printTest(cd20.forDomain(d40), false, "20 - 40");
        printTest(cd20.forDomain(d50), false, "20 - 50");
        printTest(cd20.forDomain(d60), false, "20 - 60");
        printTest(cd20.forDomain(d70), false, "20 - 70");

        printTest(cd30.forDomain(d10), false, "30 - 10");
        printTest(cd30.forDomain(d20), false, "30 - 20");
        printTest(cd30.forDomain(d30), false, "30 - 30");
        printTest(cd30.forDomain(d40), true, "30 - 40");
        printTest(cd30.forDomain(d50), false, "30 - 50");
        printTest(cd30.forDomain(d60), false, "30 - 60");
        printTest(cd30.forDomain(d70), false, "30 - 70");
    }

    void printTest(boolean val, boolean need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment);
        }
    }
}
