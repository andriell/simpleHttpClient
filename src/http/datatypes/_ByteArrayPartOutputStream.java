package http.datatypes;

import java.io.IOException;

/**
 * Created by Андрей on 10.08.14.
 */
public class _ByteArrayPartOutputStream extends _PartOutputStream {
    byte[][] data;
    int totalSize = 0;
    int maxIndex1 = 0;
    int maxIndex2 = 0;
    int index1 = -1;
    int index2 = -1;

    public _ByteArrayPartOutputStream(int maxPart) {
        maxIndex1 = maxPart - 1;
        data = new byte[maxPart][];
    }

    @Override
    public void write(int b) throws IOException {
        index2++;
        if (0 > index1 || index1 > maxIndex1  || data[index1] == null || 0 > index2 || index2 > maxIndex2) {
            throw new IOException("IOException");
        }
        data[index1][index2] = (byte) b;
    }

    @Override
    public void newPart(int size) throws IOException {
        index1++;
        if (0 > size || 0 > index1 || index1 > maxIndex1) {
            throw new IOException("IOException");
        }
        data[index1] = new byte[size];
        maxIndex2 = size - 1;
        totalSize += size;
        index2 = -1;
    }

    public byte[] toByteArray() {
        byte[] r = new byte[totalSize];
        int l = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                break;
            }
            System.arraycopy(data[i], 0, r, l, data[i].length);
            l += data[i].length;
        }
        return r;
    }

    @Override
    public String toString() {
        return new String(toByteArray());
    }
}
