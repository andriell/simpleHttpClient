package http.helper;

/**
 * Created by arybalko on 05.09.14.
 */
public class ArrayHelper {
    public static byte[] concat(byte[] a, byte[] b, byte[] c) {
        int aLen = a.length;
        int bLen = b.length;
        int cLen = c.length;
        byte[] r = new byte[aLen + bLen + cLen];
        System.arraycopy(a, 0, r, 0, aLen);
        System.arraycopy(b, 0, r, aLen, bLen);
        System.arraycopy(c, 0, r, aLen + bLen, cLen);
        return r;
    }

    /**
     * Сравнивает массивы без учета регистра
     * Регистр игнорируется только для латиницы
     * @return
     */
    public static boolean equalIgnoreCase(final byte[] a, final byte[] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        int aLen = a.length;
        int bLen = b.length;

        if (aLen != bLen) {
            return false;
        }

        byte a1;
        byte b1;
        for (int i = 0; i < aLen; i++) {
            a1 = a[i];
            b1 = b[i];
            if (64 < a1 && a1 < 91) {
                a1 += 32;
            }
            if (64 < b1 && b1 < 91) {
                b1 += 32;
            }
            if (a1 != b1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Переводит латинские символы в массиве в нижний регистр
     * @param a
     * @return
     */
    public static byte[] toLowerCase(final byte[] a) {
        int aLen = a.length;
        byte[] r = new byte[aLen];
        for (int i = 0; i < aLen; i++) {
            r[i] = a[i];
            if (64 < r[i] && r[i] < 91) {
                r[i] += 32;
            }
        }
        return r;
    }

    public static int compare(byte[] b1, byte[] b2) {
        int len1 = b1.length;
        int len2 = b2.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            if (b1[k] != b2[k]) {
                return b1[k] - b2[k];
            }
            k++;
        }
        return len1 - len2;
    }

    public static int hashCodeIgnoreCase(byte b[]) {
        if (b == null)
            return 0;
        int result = 1;
        for (byte element : b) {
            if (64 < element && element < 91) {
                result = 31 * result + (element + 32);
            } else {
                result = 31 * result + element;
            }
        }
        return result;
    }

    public static byte[] intToArry(int i) {
        byte[] r;
        if (i == 0) {
            r = new byte[1];
            r[0] = C.ZERO;
            return r;
        }
        boolean negative = i < 0;
        int size = negative ? 1 : 0;
        int n = negative ? -i : i;
        while (n > 0) {
            n = n / 10;
            size++;
        }

        r = new byte[size];
        if (negative) {
            r[0] = C.MINUS;
        }
        n = negative ? -i : i;
        while (n > 0) {
            size--;
            r[size] = (byte) (n % 10 + 48);
            n = n / 10;
        }
        return r;
    }

    //<editor-fold desc="parseInt">
    /**
     *
     * @param bytes
     * @param dafault
     * @return
     */
    public static int parseInt(byte[] bytes, int dafault) {
        if (bytes == null) {
            return dafault;
        }
        return parseInt(bytes, 0, bytes.length - 1, 10, dafault);
    }

    /**
     *
     * @param bytes
     * @param radix
     * @param dafault
     * @return
     */
    public static int parseInt(byte[] bytes, int radix, int dafault) {
        if (bytes == null) {
            return dafault;
        }
        return parseInt(bytes, 0, bytes.length - 1, radix, dafault);
    }

    /**
     *
     * @param bytes
     * @param start
     * @param end
     * @param radix
     * @return
     * @throws NumberFormatException
     */
    public static int parseInt(byte[] bytes, int start, int end, int radix) throws NumberFormatException {
        if (bytes == null) {
            throw new NumberFormatException("null");
        }

        if (radix < 2) {
            throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
        }

        if (radix > 36) {
            throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
        }
        int l = bytes.length;
        if (!(0 <= start && start < l)) {
            throw new NumberFormatException("Start index error " + start + " data.length=" + l);
        }
        if (!(start <= end && end < l)) {
            throw new NumberFormatException("End index error " + end + " data.length=" + l);
        }

        int r = 0;
        int i = start;
        byte b;

        boolean negative = false;
        if (bytes[i] == C.PLUS) {
            i++;
        } else if (bytes[i] == C.MINUS) {
            negative = true;
            i++;
        }
        while (i <= end && bytes[i] == C.ZERO) {
            i++;
        }

        for (; i <= end; i++) {
            b = bytes[i];
            if (47 < b && b < 58) {
                b -= C.ZERO;
            } else if (64 < b && b < 91) {
                // Заглавные буквы
                b -= 55;
            } else if (96 < b && b < 123) {
                // Маленькие буквы
                b -= 87;
            } else {
                throw new NumberFormatException("For input string: '" + new String(bytes) + "' index " + i);
            }
            if (b >= radix) {
                throw new NumberFormatException("For input string: '" + new String(bytes) + "' index " + i);
            }
            r = r*radix + b;
        }
        return negative ? -r : r;
    }

    /**
     *
     * @param bytes
     * @param start
     * @param end
     * @param radix
     * @param dafault
     * @return
     */
    public static int parseInt(byte[] bytes, int start, int end, int radix, int dafault) {
        try {
            return parseInt(bytes, start, end, radix);
        } catch (NumberFormatException e) {}
        return dafault;
    }
    //</editor-fold>
}
