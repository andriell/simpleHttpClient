package http.stream.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Андрей on 06.09.14.
 */
public class HttpPartOutputStream extends OutputStream {
    byte[][] data;
    int l1;
    int l2;
    int i1 = 0;
    int i2 = -1;

    public HttpPartOutputStream() {
        init(1000, 100000);
    }

    public HttpPartOutputStream(int l1, int l2) {
        init(l1, l2);
    }

    private void init(int l1, int l2) {
        this.l1 = l1;
        this.l2 = l2;
        data = new byte[this.l1][];
        data[i1] = new byte[this.l2];
    }

    @Override
    public void write(int b) throws IOException {
        if (b < 0) {
            return;
        }

        i2++;
        if (i2 >= l2) {
            i1++;
            if (i1 >= l1) {
                throw  new IOException("Data is too large");
            }
            data[i1] = new byte[l2];
            i2 = 0;
        }
        data[i1][i2] = (byte) b;
    }

    public byte[] getBytes() {
        if (data[0] == null) {
            return null;
        }
        int l = i1 * l1 + i2 + 1;
        byte[] r = new byte[l];
        l = 0;
        for (int i = 0; i <= i1; i++) {
            if (i == i1) {
                System.arraycopy(data[i], 0, r, l, i2 + 1);
            } else {
                System.arraycopy(data[i], 0, r, l, l1);
                l += l1;
            }
        }
        return r;
    }
}
