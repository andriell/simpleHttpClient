package http.header;

import http.helper.ArrayHelper;

import java.util.Arrays;

/**
 * Created by arybalko on 25.08.14.
 */
public class HttpFieldName {
    byte[] data;
    int hashCode = 0;

    public HttpFieldName(byte[] data) {
        this.data = data;
    }

    public HttpFieldName(String data) {
        this.data = data.getBytes();
    }

    public HttpFieldName(byte[] data, int from, int to) {
        this.data = Arrays.copyOfRange(data, from, to);
    }

    public int length() {
        return data.length;
    }

    public byte[] getBytes() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (hashCode() != o.hashCode()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = ArrayHelper.hashCodeIgnoreCase(data);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
