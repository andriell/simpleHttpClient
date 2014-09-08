package test.datatypes;

import http.datatypes.HttpUrl;

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
        print("https://www.google.ru:123/search?q=Coocie&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:ru:official&client=firefox-a&channel=sb&gfe_rd=cr&ei=eZQFVJf_J8zEYNSOgdgM#channel=sb&newwindow=1&q=cookie+specification&rls=org.mozilla:ru:official");

    }

    public static void print(String url) throws Exception {
        HttpUrl urlNew = new HttpUrl(url);
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
