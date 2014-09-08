package http.ssl;

/**
 * Created by arybalko on 22.08.14.
 */
public class Ssl {

    static public byte[] send(String s) {
        int l = s.length();
        byte[] bytes = new byte[l + 3];
        byte[] sb = s.getBytes();

        int i = 3;
        bytes[0] = (byte) 0x0;
        bytes[1] = (byte) l;
        bytes[2] = (byte) 0x0;

        for (byte b : sb) {
            bytes[i++] = b;
        }


        return bytes;
    }

    static public void strToBin(byte[] bytes) {
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        //System.out.println(binary);
    }
}
