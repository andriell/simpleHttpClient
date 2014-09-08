package test;

/**
 * Created by arybalko on 25.08.14.
 */
public class TestCharCode {

    public static void main(String[] args) {
        print("\n");
        print("\r");
        print("\t");
        print(" ");
        print("=");
        print(":");
        print(";");
        print(",");
        print(".");
        print("/");
        print("\\");
        print("?");
        print("#");
        print("+");
        print("-");
        print("0");
        print("\"");
        print("'");
    }


    static void print(String c) {
        int code = Character.codePointAt(c, 0);
        String name = Character.getName(code);
        System.out.println(c + " " + name + ":" + code);
    }

}
