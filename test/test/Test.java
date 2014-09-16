package test;

/**
 * Created by arybalko on 05.09.14.
 */
public class Test {

    public static void t(String val, String need, String comment) {
        if (val.equals(need)) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment + "\nNeed: " + need  + "\nValue: " + val);
        }
    }

    public static void t(int val, int need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment + " Need: " + need  + " Value: " + val);
        }
    }

    public static void t(boolean val, boolean need, String comment) {
        if (val == need) {
            System.out.println("Ok. " + comment);
        } else {
            System.out.println("Error. " + comment);
        }
    }
}
