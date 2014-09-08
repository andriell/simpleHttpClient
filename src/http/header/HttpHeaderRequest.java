package http.header;

import http.cookie.Cookie;
import http.datatypes.HttpRequestMethod;
import http.datatypes.HttpUrl;
import http.helper.ArrayHelper;
import http.helper.C;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by arybalko on 02.09.14.
 */
public class HttpHeaderRequest {
    private HttpRequestMethod method = HttpRequestMethod.GET;
    private byte[] requestURI = new byte[0];
    private byte[] version = C.HTTP1P1;
    private byte[] data = null;

    private TreeMap<HttpFieldName, byte[]> headers = new TreeMap<HttpFieldName, byte[]>(HttpHeaders.comparator);
    private ArrayList<Cookie> cookie = new ArrayList<Cookie>();

    public HttpHeaderRequest(HttpRequestMethod method, HttpUrl url) {
        this.method = method;

        requestURI = url.domainPathParam();
        set(HttpHeaders.host, url.getDomain().getByte());
        set(HttpHeaders.accept, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        set(HttpHeaders.acceptEncoding, "gzip,deflate,sdch");
        set(HttpHeaders.acceptLanguage, "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
        set(HttpHeaders.cacheControl, "no-cache");
        set(HttpHeaders.connection, "keep-alive");

        set(HttpHeaders.userAgent, "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
    }

    public void set(HttpFieldName name, String value) {
        set(name, value.getBytes());
    }

    public void set(HttpFieldName name, byte[] value) {
        headers.put(name, value);
    }

    public void setCookie(Cookie cookie) {
        this.cookie.add(cookie);
    }

    public void setData(byte[] data) {
        set(HttpHeaders.contentLength, ArrayHelper.intToArry(data.length));
        this.data = data;
    }

    public byte[] getByte() {
        byte[] method = this.method.toString().getBytes();

        int l = method.length + C.BS_SP.length + requestURI.length + C.BS_SP.length + version.length + C.BS_CRLF.length;

        for (Map.Entry<HttpFieldName, byte[]> entry: headers.entrySet()) {
            l += entry.getKey().length() + C.BS_COLON_SP.length + entry.getValue().length + C.BS_CRLF.length;
        }
        // SID=31d4d96e407aad42; lang=en-US
        byte[] separator;
        if (!this.cookie.isEmpty()) {
            l += HttpHeaders.cookie.length() + C.BS_COLON_SP.length;
            separator = new byte[0];
            for (Cookie cookie: this.cookie) {
                l += separator.length + cookie.getKey().length + C.BS_EQUALS.length + cookie.getVal().length;
                separator = C.BS_SEMICOLON_SP;
            }
            l += C.BS_CRLF.length;
        }
        l += C.BS_CRLF.length;
        if (data != null) {
            l += data.length;
        }

        byte[] r = new byte[l];
        l = 0;

        System.arraycopy(method, 0, r, l, method.length);
        l += method.length;
        System.arraycopy(C.BS_SP, 0, r, l, C.BS_SP.length);
        l += C.BS_SP.length;
        System.arraycopy(requestURI, 0, r, l, requestURI.length);
        l += requestURI.length;
        System.arraycopy(C.BS_SP, 0, r, l, C.BS_SP.length);
        l += C.BS_SP.length;
        System.arraycopy(version, 0, r, l, version.length);
        l += version.length;
        System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
        l += C.BS_CRLF.length;

        for (Map.Entry<HttpFieldName, byte[]> entry: headers.entrySet()) {
            System.arraycopy(entry.getKey().toString().getBytes(), 0, r, l, entry.getKey().length());
            l += entry.getKey().length();
            System.arraycopy(C.BS_COLON_SP, 0, r, l, C.BS_COLON_SP.length);
            l += C.BS_COLON_SP.length;
            System.arraycopy(entry.getValue(), 0, r, l, entry.getValue().length);
            l += entry.getValue().length;
            System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
            l += C.BS_CRLF.length;
        }

        // SID=31d4d96e407aad42; lang=en-US
        if (!this.cookie.isEmpty()) {
            System.arraycopy(HttpHeaders.cookie.getBytes(), 0, r, l, HttpHeaders.cookie.length());
            l += HttpHeaders.cookie.length();
            System.arraycopy(C.BS_COLON_SP, 0, r, l, C.BS_COLON_SP.length);
            l += C.BS_COLON_SP.length;
            separator = new byte[0];
            for (Cookie cookie: this.cookie) {
                System.arraycopy(separator, 0, r, l, separator.length);
                l += separator.length;
                System.arraycopy(cookie.getKey(), 0, r, l, cookie.getKey().length);
                l += cookie.getKey().length;
                System.arraycopy(C.BS_EQUALS, 0, r, l, C.BS_EQUALS.length);
                l += C.BS_EQUALS.length;
                System.arraycopy(cookie.getVal(), 0, r, l, cookie.getVal().length);
                l += cookie.getVal().length;

                separator = C.BS_SEMICOLON_SP;
            }
            System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
            l += C.BS_CRLF.length;
        }
        System.arraycopy(C.BS_CRLF, 0, r, l, C.BS_CRLF.length);
        l += C.BS_CRLF.length;
        if (data != null) {
            System.arraycopy(data, 0, r, l, data.length);
            l += data.length;
        }

        return r;
    }

    @Override
    public String toString() {
        return new String(getByte());
    }
}
