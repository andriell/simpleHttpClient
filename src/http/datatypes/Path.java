package http.datatypes;

import http.helper.ArrayHelper;
import http.helper.C;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by arybalko on 26.08.14.
 */
public class Path implements Comparable<Path>, Cloneable {
    private String charset;
    private byte[] data;

    public Path(byte[] data) {
        this.data = ArrayHelper.toLowerCase(data);
    }

    /**
     * Path /a
     * subPath /a/index.php
     * subPath /a/10
     * @param subPath
     * @return
     */
    public boolean isSubPath(Path subPath, boolean orEqual) {
        if (data == null || subPath.data == null) {
            return false;
        }
        // SubPath должен начанаться со слеша
        if (subPath.data[0] != 47 || data[0] != 47) {
            return false;
        }

        int lPath = data.length;
        if (lPath == 1) {
            return true;
        }

        int lSubPath = subPath.data.length;

        if (lSubPath < lPath) {
            return false;
        }

        int i;
        for (i = 0; i < lPath; i++) {
            if (data[i] != subPath.data[i]) {
                return false;
            }
        }
        if (lPath == lSubPath) {
            return orEqual;
        }

        return i < lSubPath && subPath.data[i] == 47;
    }

    public void add(Path path) {
        add(path.getBytes());
    }

    public void add(byte[] path) {
        if (path == null)
            return;
        if (path.length < 1)
            return;
        if (path.length == 1 && path[0] == C.SOLIDUS)
            return;

        if ((data == null) || (data != null && data.length < 1)) {
            data = path.clone();
        } else {
            if (data[data.length - 1] == C.SOLIDUS) {
                if (path[0] == C.SOLIDUS) {
                    byte[] oldData = data;
                    data = new byte[data.length + path.length - 1];
                    System.arraycopy(oldData, 0, data, 0, oldData.length - 1);
                    System.arraycopy(path, 0, data, oldData.length - 1, path.length);
                } else {
                    data = ArrayHelper.concat(data, path);
                }
            } else {
                if (path[0] == C.SOLIDUS) {
                    data = ArrayHelper.concat(data, path);
                } else {
                    data = ArrayHelper.concat(data, C.BS_SOLIDUS, path);
                }
            }
        }
    }

    public int length() {
        return data.length;
    }

    public byte[] getBytes() {
        return data;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Arrays.equals(data, path.data);
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    @Override
    public int compareTo(Path o) {
        return ArrayHelper.compare(data, o.data);
    }

    @Override
    protected Path clone() throws CloneNotSupportedException {
        Path r = (Path) super.clone();
        r.data = data.clone();
        return r;
    }
}
