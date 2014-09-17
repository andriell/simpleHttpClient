package test.helper;

import http.helper.ArrayHelper;

/**
 * Created by arybalko on 05.09.14.
 */
public class TestArrayHelper {
    public static void main(String[] args) {
        try {
            new TestArrayHelper().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void go() throws Exception {
        test("1", 0, 0, 10, 1, false);
        test("10", 0, 1, 10, 10, false);
        test("+10", 0, 2, 10, 10, false);
        test("-010", 0, 3, 10, -10, false);
        test("+0", 0, 1, 10, 0, false);
        test("-0", 0, 1, 10, 0, false);
        test("-0101", 0, 4, 2, -5, false);
        test("F10", 0, 2, 16, 3856, false);
        test("-F10", 0, 3, 16, -3856, false);
        test("12", 0, 1, 2, 0, true);
        test("12G", 0, 2, 16, 0, true);
        test("[12G", 1, 2, 16, 18, false);
        test("[12G", 1, 1, 16, 1, false);

        intToArry(0, "0");
        intToArry(1, "1");
        intToArry(-1, "-1");
        intToArry(100500, "100500");
        intToArry(-100500, "-100500");
        intToArry(1111, "1111");
        intToArry(-1111, "-1111");
        intToArry(9999, "9999");
        intToArry(-9999, "-9999");
    }

    void test(String s, int stsrt, int end, int radix, int need, boolean error) {
        try {
            int i = ArrayHelper.parseInt(s.getBytes(), stsrt, end, radix);
            if (i == need) {
                System.out.println("Ok. " + s + " stsrt=" + stsrt + " end=" + end + " radix=" + radix);
            } else {
                System.out.println("Error. " + s + " value=" + i + " need=" + need);
            }
        } catch (Exception e) {
            if (error) {
                System.out.println("Ok. " + s);
            } else {
                System.out.println("Error. " + s + " " + e);
            }
        }
    }

    void intToArry(int i, String need) {
        String s = new String(ArrayHelper.intToArry(i));
        if (s.equals(need)) {
            System.out.println("Ok. " + i);
        } else {
            System.out.println("Error. " + i + " value=" + s + " need=" + need);
        }
    }

}
