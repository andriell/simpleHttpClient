package test.http.datatypes;

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
        Domain domain0 = new Domain(new String(".example.com").getBytes());
        Domain domain1 = new Domain(new String("example.com").getBytes());
        Domain domaina0 = new Domain(new String(".com").getBytes());
        Domain domaina1 = new Domain(new String("com").getBytes());
        Domain domain2 = new Domain(new String("cs.example.com").getBytes());
        Domain domain3 = new Domain(new String("www.example.com").getBytes());
        Domain domain4 = new Domain(new String("my-example.com").getBytes());
        Domain domain5 = new Domain(new String("com").getBytes());
        Domain domain6 = new Domain(new String(".com").getBytes());

        printTest(domain0.isSubDomain(domain0), false, "0 - 0");
        printTest(domain0.isSubDomain(domain1), true, "0 - 1");
        printTest(domain0.isSubDomain(domain2), true, "0 - 2");
        printTest(domain0.isSubDomain(domain3), true, "0 - 3");
        printTest(domain0.isSubDomain(domain4), false, "0 - 4");
        printTest(domain0.isSubDomain(domain5), false, "0 - 5");
        printTest(domain0.isSubDomain(domain6), false, "0 - 6");

        printTest(domain1.isSubDomain(domain0), false, "1 - 0");
        printTest(domain1.isSubDomain(domain1), true, "1 - 1");
        printTest(domain1.isSubDomain(domain2), false, "1 - 2");
        printTest(domain1.isSubDomain(domain3), false, "1 - 3");
        printTest(domain1.isSubDomain(domain4), false, "1 - 4");
        printTest(domain1.isSubDomain(domain5), false, "1 - 5");
        printTest(domain1.isSubDomain(domain6), false, "1 - 6");

        printTest(domaina0.isSubDomain(domain0), false, "a0 - 0");
        printTest(domaina0.isSubDomain(domain1), false, "a0 - 1");
        printTest(domaina0.isSubDomain(domain2), false, "a0 - 2");
        printTest(domaina0.isSubDomain(domain3), false, "a0 - 3");
        printTest(domaina0.isSubDomain(domain4), false, "a0 - 4");
        printTest(domaina0.isSubDomain(domain5), false, "a0 - 5");
        printTest(domaina0.isSubDomain(domain6), false, "a0 - 6");

        printTest(domaina1.isSubDomain(domain0), false, "a1 - 0");
        printTest(domaina1.isSubDomain(domain1), false, "a1 - 1");
        printTest(domaina1.isSubDomain(domain2), false, "a1 - 2");
        printTest(domaina1.isSubDomain(domain3), false, "a1 - 3");
        printTest(domaina1.isSubDomain(domain4), false, "a1 - 4");
        printTest(domaina1.isSubDomain(domain5), false, "a1 - 5");
        printTest(domaina1.isSubDomain(domain6), false, "a1 - 6");
    }

    void printTest(boolean val, boolean need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment);
        }
    }
}
