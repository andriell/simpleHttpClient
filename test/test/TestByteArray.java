package test;

import http.helper.ArrayHelper;

/**
 * Created by arybalko on 26.08.14.
 */
public class TestByteArray {
    static byte[] bytes1 = new byte[1];
    public static void main(String[] args) {

        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {

            bytes[i] = (byte) (i + Byte.MIN_VALUE);
            bytes1[0] = bytes[i];
            print(new String(bytes1));
        }


        System.out.println(new String(bytes));
        System.out.println(new String(ArrayHelper.toLowerCase(bytes)));

        byte[] bytes2 = new String("ABCD0EF").getBytes();
        byte[] bytes3 = new String("abcd0ef").getBytes();
        byte[] bytes4 = new String("abcdefg").getBytes();
        System.out.println(ArrayHelper.equalIgnoreCase(bytes2, bytes2));
        System.out.println(ArrayHelper.equalIgnoreCase(bytes2, bytes3));
        System.out.println(ArrayHelper.equalIgnoreCase(bytes2, bytes4));

        System.out.println(new String("Attribute"));

        byte[] bytes5 = new byte[0];
        System.out.println(bytes5.length);
    }

    static void print(String c) {
        int code = Character.codePointAt(c, 0);
        String name = Character.getName(code);
        System.out.println(name + ":" + code);
    }
}
