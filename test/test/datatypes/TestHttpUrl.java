package test.datatypes;

import http.datatypes.HttpUrl;
import test.Test;

/**
 * Created by Андрей on 03.08.14.
 */
public class TestHttpUrl {

    public static void main(String[] args) throws Exception{
        /**
         * Например следующие три URI эквивалентны:
         http://abc.com:80/~smith/home.html
         http://ABC.com/%7Esmith/home.html
         http://ABC.com:/%7esmith/home.html
         */

        HttpUrl httpUrl1 = new HttpUrl("http://abc.com:80/~smith/home.html?q=%D0%B0");
        HttpUrl httpUrl2 = new HttpUrl("http://ABC.com/%7Esmith/home.html?q=%D0%B0");
        HttpUrl httpUrl3 = new HttpUrl("http://ABC.com:/%7esmith/home.html?q=%D0%B0");

        equals(httpUrl1, httpUrl1);
        equals(httpUrl2, httpUrl2);
        equals(httpUrl3, httpUrl3);
        equals(httpUrl1, httpUrl2);
        equals(httpUrl1, httpUrl3);
        equals(httpUrl2, httpUrl3);

        System.out.println();

        print("http://abc.com:80/~smith/home.html");
        print("http://ABC.com/%7Esmith/home.html");
        print("http://ABC.com:/%7esmith/home.html");
        print("https://www.google.ru:123/search?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&request=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        print("https://www.google.ru:123?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&request=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        print("https://www.google.ru:123#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        print("https://www.google.ru/search?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&request=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        print("https://www.google.ru?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&request=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");
        print("https://www.google.ru#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");

        HttpUrl httpUrl4 = new HttpUrl(httpUrl3, "https://ya.ru/a/a/index.php?q=1#123".getBytes());
        print(httpUrl4);
        System.out.println();
        HttpUrl httpUrl5 = new HttpUrl(httpUrl3, "/a/a/index.php?q=1#123".getBytes());
        print(httpUrl5);
        System.out.println();
        HttpUrl httpUrl6 = new HttpUrl(httpUrl3, "b/index.php?q=1#123".getBytes());
        print(httpUrl6);
        System.out.println();

        HttpUrl httpUrl10 = new HttpUrl("http://www.mamba.ru/search.phtml?t=a&form=1&ia=M&lf=F&af=22&at=29&s_c=3159_3529_3538_0&target%5B%5D=");
        HttpUrl httpUrl11 = new HttpUrl(httpUrl10, "?ia=M&lf=F&af=22&at=29&s_c=3159_3529_3538_0&wp=1&geo=0&t=a&offset=0&nchanged=1410971300&noid=1051171926".getBytes());
        print(httpUrl11);
        System.out.println(httpUrl11);

        HttpUrl httpUrl20 = new HttpUrl("https://ya.ru:8080/a/search.phtml?t=a&form=1#fragment");
        Test.t(httpUrl20.toString(true, true, true, true, true, true), "https://ya.ru:8080/a/search.phtml?t=a&form=1#fragment", "toString 1");
        Test.t(httpUrl20.toString(false, true, false, true, false, true), "ya.ru/a/search.phtml#fragment", "toString 2");
        Test.t(httpUrl20.toString(true, false, true, false, true, false), "https://:8080?t=a&form=1", "toString 3");
    }

    public static void print(HttpUrl urlNew) throws Exception {
        System.out.println("Scheme: " + urlNew.getScheme());
        System.out.println("Domain: " + urlNew.getDomain());
        System.out.println("Port: " + urlNew.getPort());
        System.out.println("Path: " + urlNew.getPath());
        if (urlNew.getQuery() != null) {
            System.out.println("Query: " + new String(urlNew.getQuery()));
        }
        if (urlNew.getFragment() != null) {
            System.out.println("Fragment: " + new String(urlNew.getFragment()));
        }
    }

    public static void print(String url) throws Exception {
        HttpUrl urlNew = new HttpUrl(url);
        print(urlNew);
        System.out.println(url + "\n" + urlNew.toString());
        System.out.println();
    }

    public static void equals(HttpUrl httpUrl1, HttpUrl httpUrl2) {
        if (httpUrl1.equals(httpUrl2)) {
            System.out.println(httpUrl1 + " = " + httpUrl2);
        } else {
            System.out.println(httpUrl1 + " != " + httpUrl2);
        }
    }
}
