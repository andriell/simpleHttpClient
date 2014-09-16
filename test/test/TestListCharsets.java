package test;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Андрей on 06.09.14.
 */
public class TestListCharsets {
    static public void main(String args[]) throws Exception {
        SortedMap<String, Charset> charsets = Charset.availableCharsets();

        for (Map.Entry<String, Charset> entry: charsets.entrySet()) {
            String name = entry.getKey();
            System.out.print(name + "\t");
            for (String aliase: entry.getValue().aliases()) {
                System.out.print(aliase + "\t");
            }
            System.out.println();
        }
    }
}
