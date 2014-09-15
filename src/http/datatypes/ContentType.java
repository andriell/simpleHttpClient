package http.datatypes;

import http.header.HttpFieldName;
import http.header.HttpHeaders;
import http.helper.ArrayHelper;
import http.helper.C;

import java.text.ParseException;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Created by Андрей on 06.09.14.
 */
public class ContentType {
    private byte[] data;

    private byte[] type = null;
    private byte[] subtype = null;
    private TreeMap<byte[], byte[]> parameters = null;

    private ContentType(byte[] data) {
        this.data = data;
        parse();
    }

    private void parse() {
        /*
         * image/svg+xml
         * application/vnd.oasis.opendocument.text
         * text/plain; charset=utf-8
         * video/mp4; codecs="avc1.640028"
         */
        // "/"
        int l = data.length;

        int solidus = 0;
        int semicolon = 0;
        int equals = 0;
        int start = 0;
        int end = 0;
        for (int i = 0; i < l; i++) {
            if (data[i] == C.SOLIDUS && type == null) { // /
                while (data[start] == C.SP) {
                    start++;
                }
                end = i - 1;
                while (data[end] == C.SP) {
                    end--;
                }
                type = Arrays.copyOfRange(data, start, end + 1);
                solidus = i;
            } else if (data[i] == C.SEMICOLON) { // ;
                if (subtype == null && solidus > 0) {
                    start = solidus + 1;
                    while (data[start] == C.SP) {
                        start++;
                    }
                    end = i - 1;
                    while (data[end] == C.SP) {
                        end--;
                    }
                    subtype = Arrays.copyOfRange(data, start, end + 1);
                } else {
                    addParameter(semicolon, equals, i);
                }
                semicolon = i;
            } else if (data[i] == C.EQUALS) { // =
                equals = i;
            }
        }

        if (solidus > 0) {
            addParameter(semicolon, equals, l);
        }

    }

    private boolean checkKey(int i, int b) {
        if (checkKey(i)) {
            return data[i] == b;
        }
        return false;
    }

    private boolean checkKey(int i) {
        return -1 < i && i < data.length;
    }

    private void addParameter(int semicolon, int equals, int end) {
        int startKey, endKey, startVal, endVal;
        byte[] key;
        byte[] val;

        if (parameters == null) {
            parameters = new TreeMap<byte[], byte[]>();
        }

        if (semicolon < equals && equals < end) {
            startKey = semicolon + 1;
            while (checkKey(startKey, C.SP)) {
                startKey++;
            }
            endKey = equals - 1;
            while (checkKey(endKey, C.SP)) {
                endKey--;
            }

            startVal = equals + 1;
            while (checkKey(startVal, C.SP)) {
                startVal++;
            }
            endVal = end - 1;
            while (checkKey(endVal, C.SP)) {
                endVal--;
            }

            key = Arrays.copyOfRange(data, startKey, endKey + 1);
            val = Arrays.copyOfRange(data, startVal, endVal + 1);

            parameters.put(key, val);
        } else if (end - semicolon > 2) {
            startKey = semicolon + 1;
            while (checkKey(startKey, C.SP)) {
                startKey++;
            }
            endKey = end - 1;
            while (checkKey(endKey, C.SP)) {
                endKey--;
            }

            key = Arrays.copyOfRange(data, startKey, endKey + 1);

            parameters.put(key, null);
        }
    }

    public static String getCharset(byte[] data) {
        return new String(getCharsetB(data));
    }

    public static byte[] getCharsetB(byte[] data) {
        if (data == null) {
            return null;
        }

        int l = data.length;
        // text/a;charset=a
        if (l < 16) {
            return null;
        }
        // text/ = {116, 101, 120, 116, 47}
        // TEXT/ = {84,  69,  88,  84,  47}
        if (! (
            (data[0] == 116 || data[0] == 84)
            && (data[1] == 101 || data[1] == 69)
            && (data[2] == 120 || data[2] == 88)
            && (data[3] == 116 || data[3] == 84)
            && (data[4] == 47)
        )) {
            return null;
        }


        for (int i = 6; i < l - 9; i++) {
            // charset = {99, 104, 97, 114, 115, 101, 116}
            // CHARSET = {67, 72,  65, 82,  83,  69,  84}
            if (
                (data[i] == 99 || data[i] == 67)
                && (data[i + 1] == 104 || data[i + 1] == 72)
                && (data[i + 2] == 97 || data[i + 2] == 65)
                && (data[i + 3] == 114 || data[i + 3] == 82)
                && (data[i + 4] == 115 || data[i + 4] == 83)
                && (data[i + 5] == 101 || data[i + 5] == 69)
                && (data[i + 6] == 116 || data[i + 6] == 84)
            ) {
                int start = i + 8;
                while (
                    start < l
                    && (data[start] == C.EQUALS
                    || data[start] == C.QUOTATION
                    || data[start] == C.APOSTROPHE
                    || data[start] == C.SP
                    || data[start] == C.HT)
                ) {
                    start++;
                }

                int end = start;
                while (
                    end < l
                    && data[end] != C.EQUALS
                    && data[end] != C.QUOTATION
                    && data[end] != C.APOSTROPHE
                    && data[end] != C.SP
                    && data[end] != C.HT
                ) {
                    end++;
                }
                return Arrays.copyOfRange(data, start, end);
            }
        }

        return null;
    }
}
