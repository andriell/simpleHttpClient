package http.stream.output;

import http.header.HttpFieldName;
import http.header.HttpHeaders;
import http.helper.ArrayHelper;
import http.helper.C;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by arybalko on 02.09.14.
 */
public class HttpHeaderOutputStream extends OutputStream {
    private byte[] data;
    private int lenPart;

    private byte[] httpVersion = null;
    private int statusCode = 0;
    private byte[] reasonPhrase = null;


    // Порядок, в котором получены поля заголовка с различными именами не имеет значения. Однако "хорошая практика"
    // заключается в том, что сначала посылаются поля общих заголовков, затем поля заголовков запроса или заголовков
    // ответа, и, наконец, поля заголовков объекта.
    private TreeMap<HttpFieldName, byte[]> headers = new TreeMap<HttpFieldName, byte[]>(HttpHeaders.comparator);

    // Origin servers SHOULD NOT fold multiple Set-Cookie header fields into
    // a single header field.  The usual mechanism for folding HTTP headers
    // fields (i.e., as defined in [RFC2616]) might change the semantics of
    // the Set-Cookie header field because the %x2C (",") character is used
    // by Set-Cookie in a way that conflicts with such folding.
    private ArrayList<byte[]> setCookie = new ArrayList<byte[]>();

    // Переменные процесса парсинга
    private boolean isEnd = false;
    private boolean isFirstLine = true;
    private int writePosition = -1;
    private int c10 = 0;
    private int c32 = 0;
    private int c58 = 0;
    private HttpFieldName lastField = null;

    public HttpHeaderOutputStream(int startSize, int lenPart) {
        this.data = new byte[startSize];
        this.lenPart = lenPart;
    }

    private boolean checkKey(int i) {
        return -1 < i && i <= writePosition;
    }

    /**
     *
     * @param start
     * @param colon
     * @param end
     * @return isEnd
     */
    private void addHeader(int start, int colon, int end) {
        int endKey, startVal, endVal;
        byte[] value;

        // Убираем переходы на строчки из начала и конца строки
        while (checkKey(start) && (data[start] == C.LF || data[start] == C.CR)) {
            start++;
        }
        while (checkKey(end) && (data[end] == C.LF || data[end] == C.CR)) {
            end--;
        }

        // Поля заголовка могут занимать несколько строк.
        // При этом каждая следующая строка начинается по крайней мере одним SP_ARRAY или HT.
        if(lastField != null && checkKey(start) && (data[start] == C.SP || data[start] == C.HT)) {
            while (data[start] == C.SP || data[start] == C.HT) {
                start++;
            }
            value = Arrays.copyOfRange(data, start, end + 1);
            if (lastField.equals(HttpHeaders.setCookie)) {
                setCookie.add(value);
                return;
            }
            if (headers.containsKey(lastField)) {
                value = ArrayHelper.concat(headers.get(lastField), C.BS_COMMA_SP, value);
            }

            headers.put(lastField, value);
        } else if (start < colon && colon < end) {
            endKey = colon - 1;
            if (data[endKey] == C.SP) {
                endKey--;
            }

            startVal = colon + 1;
            // Значению поля может предшествовать любое число LWS, хотя предпочтителен одиночный SP
            while (data[startVal] == C.SP || data[startVal] == C.HT) {
                startVal++;
            }

            lastField = new HttpFieldName(data, start, endKey + 1);
            value = Arrays.copyOfRange(data, startVal, end + 1);

            if (lastField.equals(HttpHeaders.setCookie)) {
                setCookie.add(value);
                return;
            }
            if (headers.containsKey(lastField)) {
                value = ArrayHelper.concat(headers.get(lastField), C.BS_COMMA_SP, value);
            }
            headers.put(lastField, value);
        } else if (end < start) {
            isEnd = true;
        }
    }

    public byte[] rebuild() {
        byte[] statusCode = ArrayHelper.intToArry(this.statusCode);
        byte[] setCookie = HttpHeaders.setCookie.getBytes();

        int l = httpVersion.length + C.BS_SP.length + statusCode.length + C.BS_SP.length + reasonPhrase.length + C.BS_CRLF.length;

        for(Map.Entry<HttpFieldName, byte[]> entry : headers.entrySet()) {
            l += entry.getKey().length() + C.BS_COMMA_SP.length + entry.getValue().length + C.BS_CRLF.length;
        }
        for(byte[] value: this.setCookie) {
            l += setCookie.length + C.BS_COMMA_SP.length + value.length + C.BS_CRLF.length;
        }
        l += C.BS_CRLF.length;

        byte[] r = new byte[l];
        l = 0;

        System.arraycopy(httpVersion, 0, r, l, httpVersion.length);
        l += httpVersion.length;
        System.arraycopy(C.BS_SP, 0, r, l, C.BS_SP.length);
        l += C.BS_SP.length;
        System.arraycopy(statusCode, 0, r, l, statusCode.length);
        l += statusCode.length;
        System.arraycopy(C.BS_SP, 0, r, l, C.BS_SP.length);
        l += C.BS_SP.length;
        System.arraycopy(reasonPhrase, 0, r, l, reasonPhrase.length);
        l += reasonPhrase.length;
        System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
        l += C.BS_CRLF.length;

        headers.put(HttpHeaders.setCookie, null);
        for(Map.Entry<HttpFieldName, byte[]> entry : headers.entrySet()) {
            HttpFieldName key = entry.getKey();
            if (HttpHeaders.setCookie.equals(key)) {
                for(byte[] value: this.setCookie) {
                    System.arraycopy(setCookie, 0, r, l, setCookie.length);
                    l += setCookie.length;
                    System.arraycopy(C.BS_COLON_SP, 0, r, l, C.BS_COLON_SP.length);
                    l += C.BS_COLON_SP.length;
                    System.arraycopy(value, 0, r, l, value.length);
                    l += value.length;
                    System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
                    l += C.BS_CRLF.length;
                }
            } else {
                System.arraycopy(key.getBytes(), 0, r, l, key.length());
                l += key.length();
                System.arraycopy(C.BS_COLON_SP, 0, r, l, C.BS_COLON_SP.length);
                l += C.BS_COLON_SP.length;
                System.arraycopy(entry.getValue(), 0, r, l, entry.getValue().length);
                l += entry.getValue().length;
                System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
                l += C.BS_CRLF.length;
            }
        }
        headers.remove(HttpHeaders.setCookie);
        System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
        l += C.BS_CRLF.length;
        return r;
    }

    @Override
    public String toString() {
        return new String(getData());
    }

    //<editor-fold desc="Getters and setters">
    public byte[] getData() {
        if (data.length > (writePosition + 1)) {
            byte[] r = new byte[writePosition + 1];
            System.arraycopy(data, 0, r, 0, writePosition + 1);
            data = r;
        }
        return data;
    }

    public int getDataLength() {
        return writePosition + 1;
    }

    public byte[] getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getReasonPhrase() {
        return reasonPhrase;
    }

    public byte[] get(HttpFieldName fieldName) {
        return headers.get(fieldName);
    }

    public String getString(HttpFieldName fieldName) {
        byte[] bytes = get(fieldName);
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }

    public int getInt(HttpFieldName fieldName, int dafault) {
        return ArrayHelper.parseInt(get(fieldName), dafault);
    }

    public int getInt(HttpFieldName fieldName, int radix, int dafault) {
        return ArrayHelper.parseInt(get(fieldName), radix, dafault);
    }

    public Iterator<byte[]> cookieIterator() {
        return setCookie.iterator();
    }
    //</editor-fold>

    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void write(int i) throws IOException {
        if (isEnd()) {
            return;
        }

        writePosition++;
        // Расширяем память
        if (data.length <= writePosition) {
            byte[] bytes = new byte[data.length + lenPart];
            System.arraycopy(data, 0, bytes, 0, data.length);
            data = bytes;
        }
        byte b = (byte) i;

        data[writePosition] = b;

        if (b == C.CR) {
            return;
        }

        if (isFirstLine) {
            if (b == C.SP) {
                if (httpVersion == null) {
                    httpVersion = Arrays.copyOfRange(data, 0, writePosition);
                } else if (statusCode == 0) {
                    statusCode = ArrayHelper.parseInt(Arrays.copyOfRange(data, c32 + 1, writePosition), 10, -1);
                }
                c32 = writePosition;
            } else if (b == C.LF) {
                if (data[writePosition-1] == C.CR) {
                    reasonPhrase = Arrays.copyOfRange(data, c32 + 1, writePosition - 1);
                } else {
                    reasonPhrase = Arrays.copyOfRange(data, c32 + 1, writePosition);
                }
                c10 = writePosition;
                isFirstLine = false;
            }
        } else {
            if (b == C.LF) {
                addHeader(c10, c58, writePosition);
                c10 = writePosition;
                c58 = 0;
            } else if (b == C.COLON && c58 == 0) {
                c58 = writePosition;
            }
        }
    }
}
