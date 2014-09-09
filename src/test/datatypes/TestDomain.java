package test.datatypes;

import http.datatypes.Domain;

/**
 * Created by arybalko on 26.08.14.
 */
public class TestDomain {
    public static void main(String[] args) {
        new TestDomain().go();
    }

    void go() {
        /*
        domain example.com
        subdomain cs.example.com
        subdomain www.example.com
         */
        Domain d10 = new Domain("localhost".getBytes());
        Domain d20 = new Domain("example.com".getBytes());
        Domain d30 = new Domain(".example.com".getBytes());
        Domain d40 = new Domain("ru.example.com".getBytes());
        Domain d50 = new Domain("my-example.com".getBytes());
        Domain d60 = new Domain("com".getBytes());
        Domain d70 = new Domain(".com".getBytes());

        printTest(d10.isSubdomain(d10, true), true, "10 - 10");
        printTest(d10.isSubdomain(d10, false), false, "10 - 10");
        printTest(d10.isSubdomain(d20, true), false, "10 - 20");

        printTest(d20.isSubdomain(d20, true), true, "20 - 20");
        printTest(d20.isSubdomain(d20, false), false, "20 - 20");
        printTest(d20.isSubdomain(d30, true), true, "20 - 30");
        printTest(d20.isSubdomain(d40, true), true, "20 - 40");
        printTest(d20.isSubdomain(d50, true), false, "20 - 50");
        printTest(d20.isSubdomain(d60, true), false, "20 - 60");
        printTest(d20.isSubdomain(d70, true), false, "20 - 70");

        printTest(d60.isSubdomain(d10, true), false, "60 - 10");
        printTest(d60.isSubdomain(d20, true), true, "60 - 20");
        printTest(d60.isSubdomain(d30, true), true, "60 - 30");
        printTest(d60.isSubdomain(d40, true), true, "60 - 40");
        printTest(d60.isSubdomain(d50, true), true, "60 - 50");
        printTest(d60.isSubdomain(d60, true), true, "60 - 60");
        printTest(d60.isSubdomain(d60, false), false, "60 - 60");
        printTest(d60.isSubdomain(d60, true), true, "60 - 70");

    }

    void printTest(boolean val, boolean need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment);
        }
    }
}
