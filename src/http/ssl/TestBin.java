package http.ssl;

/**
 * Created by arybalko on 22.08.14.
 */
public class TestBin {
    public static void main(String args[]) {

        new TestBin().go();

    }

    public void go() {
        byte[] bytes = Ssl.send("CLIENT-HELLO");
        Ssl.strToBin(bytes);
    }
}
