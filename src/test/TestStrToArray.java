package test;

/**
 * Created by arybalko on 26.08.14.
 */
public class TestStrToArray {
    static byte[] bytes1 = new byte[1];
    public static void main(String[] args) {
        print("Expires");
        print("Max-Age");
        print("Domain");
        print("Path");
        print("Secure");
        print("HttpOnly");
        print("; ");
        print("=");
        print("HTTPS://");
        print("https://");
        print("HTTP/1.1");
        print("HTTP/1.0");
        print("chunked");
        print("+-");
        print("ABCDEF");
        print("abcdef");
        print("0123456789");
        print("gzip");
        print("CHARSET");
        print("charset");
        print("TEXT/");
        print("text/");
    }

    static void print(String s) {
        byte[] bytes = s.getBytes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        String delimiter = "";
        for (byte b : bytes) {
            buffer.append(delimiter);
            buffer.append(b);
            delimiter = ", ";
        }
        buffer.append("}");
        System.out.println(s + " = " + buffer);
    }
}
