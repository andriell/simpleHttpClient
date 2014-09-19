package test.datatypes;

import http.datatypes.Path;

/**
 * Created by arybalko on 26.08.14.
 */
public class TestPath {
    public static void main(String[] args) {
        /*
        path example.com
        subpath cs.example.com
        subpath www.example.com
         */
        Path path = new Path(new String("/").getBytes());
        Path path0 = new Path(new String("/").getBytes());
        Path path1 = new Path(new String("/abs/dabs").getBytes());
        Path path2 = new Path(new String("/abs/dabs/").getBytes());
        Path path3 = new Path(new String("/abs/dabs/12").getBytes());
        Path path4 = new Path(new String("/abs/dabs/index.php").getBytes());
        Path path5 = new Path(new String("/abs/dabs2").getBytes());
        Path path6 = new Path(new String("/abs/d/").getBytes());
        test(path1.isSubPath(path1, true), true);
        test(path1.isSubPath(path1, false), false);
        test(path1.isSubPath(path2, false), true);
        test(path1.isSubPath(path3, false), true);
        test(path1.isSubPath(path4, false), true);
        test(path1.isSubPath(path5, false), false);
        test(path1.isSubPath(path6, false), false);

        test(path0.isSubPath(path, false), true);
        test(path0.isSubPath(path1, false), true);
        test(path0.isSubPath(path2, false), true);
        test(path0.isSubPath(path3, false), true);
        test(path0.isSubPath(path4, false), true);
        test(path0.isSubPath(path5, false), true);
        test(path0.isSubPath(path6, false), true);


        Path path10 = new Path(new String("/abs/dabs").getBytes());
        path10.add("/A/a".getBytes());
        System.out.println(path10);
        Path path11 = new Path(new String("/abs/dabs").getBytes());
        path11.add("A/a".getBytes());
        System.out.println(path11);
        Path path20 = new Path(new String("/abs/dabs/").getBytes());
        path20.add("/A/a".getBytes());
        System.out.println(path20);
        Path path21 = new Path(new String("/abs/dabs/").getBytes());
        path21.add("A/a".getBytes());
        System.out.println(path21);

        Path path30 = new Path(new String("/").getBytes());
        path30.add("/".getBytes());
        System.out.println(path30);

        Path path50 = new Path(new String(".txt").getBytes());
        System.out.println(new String(path50.filenameExtension()));
        System.out.println(path50.filenameExtension().length);
    }

    public static void test(boolean val, boolean good) {
        if (val == good) {
            System.out.println("Ok");
        } else {
            System.out.println("Error");
        }
    }
}
