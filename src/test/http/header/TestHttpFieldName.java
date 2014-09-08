package test.http.header;

import http.header.HttpFieldName;
import http.header.HttpHeaders;

import java.util.TreeMap;

/**
 * Created by arybalko on 25.08.14.
 */
public class TestHttpFieldName {
    public static void main(String[] args) {
        HttpFieldName fieldName1 = new HttpFieldName("Test");
        HttpFieldName fieldName2 = new HttpFieldName("TeST");
        HttpFieldName fieldName3 = new HttpFieldName("Test3");
        if (fieldName1.equals(fieldName2)) {
            System.out.println("Ok");
        } else {
            System.out.println("Error");
        }

        if (fieldName1.equals(fieldName3)) {
            System.out.println("Error");
        } else {
            System.out.println("Ok");
        }

        System.out.println(fieldName1.hashCode());
        System.out.println(fieldName2.hashCode());
        System.out.println(fieldName3.hashCode());

        TreeMap<HttpFieldName, String> treeMap = new TreeMap<HttpFieldName, String>(HttpHeaders.getComparator());

        treeMap.put(fieldName1, "1");
        treeMap.put(fieldName2, "2");
        treeMap.put(fieldName3, "3");

        System.out.println(treeMap);
        System.out.println(treeMap.get(fieldName1));
        System.out.println(treeMap.get(fieldName3));
    }
}
