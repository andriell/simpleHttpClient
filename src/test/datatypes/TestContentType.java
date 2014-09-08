package test.datatypes;

import http.datatypes.ContentType;
import test.Test;

/**
 * Created by Андрей on 06.09.14.
 */
public class TestContentType {
    public static void main(String[] args) {
        new TestContentType().go();
    }

    void go() {
        /*image/svg+xml
                * application/vnd.oasis.opendocument.text
                * text/plain; charset=utf-8
                * video/mp4; codecs="avc1.640028"*/

        Test.t(ContentType.getCharset(null) == null, true, "null");

        test("", null);
        test("image/svg+xml", null);
        test("application/vnd.oasis.opendocument.text", null);
        test("video/mp4; codecs=\"avc1.640028\"*/", null);
        test("text/plain; charset=utf-8", "utf-8");
        test("text/;charset=utf-8", "utf-8");
        test("text/;charset =   utf-8   ", "utf-8");
        test("text/;charset =   \"utf-8\"   ", "utf-8");
        test("text/;charset =   'utf-8'   ", "utf-8");

    }

    void test(String s, String need) {
        String s2 = ContentType.getCharset(s.getBytes());
        if (
            (s2 != null && need != null && need.equals(s2))
            || (s2 == null && need == null)
        ) {
            System.out.println("Ok. " + s);
        } else {
            System.out.println("Error. " + s + " " + s2 + " " + need);
        }
    }
}
