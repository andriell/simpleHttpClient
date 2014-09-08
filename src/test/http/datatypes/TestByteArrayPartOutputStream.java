package test.http.datatypes;

import http.datatypes._ByteArrayPartOutputStream;

import java.io.IOException;

/**_ByteArrayPartOutputStream
 * Created by Андрей on 10.08.14.
 */
public class TestByteArrayPartOutputStream {
    public static void main(String[] args) {
        _ByteArrayPartOutputStream stream = new _ByteArrayPartOutputStream(1);

        try {
            stream.write(1);
            System.out.println("Test1 Error");
        } catch (IOException e) {
            System.out.println("Test1 Ok");
            //e.printStackTrace();
        }

        try {
            stream.newPart(1);
            System.out.println("Test2 Ok");
        } catch (IOException e) {
            System.out.println("Test2 Error");
            e.printStackTrace();
        }

        try {
            stream.write(65);
            System.out.println("Test3 Ok");
        } catch (IOException e) {
            System.out.println("Test3 Error");
            e.printStackTrace();
        }

        try {
            stream.write(3);
            System.out.println("Test4 Error");
        } catch (IOException e) {
            System.out.println("Test4 Ok");
            //e.printStackTrace();
        }

        try {
            stream.newPart(2);
            System.out.println("Test5 Error");
        } catch (IOException e) {
            System.out.println("Test5 Ok");
            //e.printStackTrace();
        }

        stream = new _ByteArrayPartOutputStream(3);
        try {
            stream.newPart(3);
            stream.write(65);
            stream.write(66);
            stream.write(67);
            stream.newPart(2);
            stream.write(68);
            stream.write(69);
            stream.newPart(1);
            stream.write(70);
            if (new String(stream.toByteArray()).equals("ABCDEF")) {
                System.out.println("Test6 Ok");
            } else {
                throw new IOException("ABCDEF");
            }
        } catch (IOException e) {
            System.out.println("Test6 Error");
            e.printStackTrace();
        }
    }
}
