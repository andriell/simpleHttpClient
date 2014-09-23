package http.header;

import http.cookie.Cookie;
import http.datatypes.HttpRequestMethod;
import http.datatypes.HttpUrl;
import http.helper.ArrayHelper;
import http.helper.C;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by arybalko on 02.09.14.
 */
public class HttpHeaderRequest {
    private static String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static String acceptEncoding = "gzip,deflate,sdch";
    private static String acceptLanguage = "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4";
    private static String cacheControl = "no-cache";
    private static String connection = "close";
    private static String userAgent = "simpleHttpClient v1.0";

    private HttpRequestMethod method = HttpRequestMethod.GET;
    private byte[] requestURI = new byte[0];
    private byte[] version = C.HTTP1P1;
    private byte[] data = null;

    private TreeMap<HttpFieldName, byte[]> headers = new TreeMap<HttpFieldName, byte[]>(HttpHeaders.comparator);
    private HashSet<Cookie> cookie = new HashSet<Cookie>(10);

    //<editor-fold desc="Constructors">
    public HttpHeaderRequest() {
        setDefaultHeaders();
    }

    public HttpHeaderRequest(HttpRequestMethod method) {
        setMethod(method);
        setDefaultHeaders();
    }

    public HttpHeaderRequest(HttpUrl url) {
        url(url);
        setDefaultHeaders();
    }

    public HttpHeaderRequest(HttpRequestMethod method, HttpUrl url) {
        setMethod(method);
        url(url);
        setDefaultHeaders();
    }
    //</editor-fold>

    public void setDefaultHeaders() {
        set(HttpHeaders.accept, accept);
        set(HttpHeaders.acceptEncoding, acceptEncoding);
        set(HttpHeaders.acceptLanguage, acceptLanguage);
        set(HttpHeaders.cacheControl, cacheControl);
        set(HttpHeaders.connection, connection);
        set(HttpHeaders.userAgent, userAgent);
    }

    public void url(HttpUrl url) {
        requestURI = url.getBytes(true, true, true, true, true, false);
        set(HttpHeaders.host, url.getBytes(false, true, true, false, false, false));
    }

    //<editor-fold desc="Getters and Setters Static">
    public static String getAccept() {
        return accept;
    }

    public static void setAccept(String accept) {
        HttpHeaderRequest.accept = accept;
    }

    public static String getAcceptEncoding() {
        return acceptEncoding;
    }

    public static void setAcceptEncoding(String acceptEncoding) {
        HttpHeaderRequest.acceptEncoding = acceptEncoding;
    }

    public static String getAcceptLanguage() {
        return acceptLanguage;
    }

    public static void setAcceptLanguage(String acceptLanguage) {
        HttpHeaderRequest.acceptLanguage = acceptLanguage;
    }

    public static String getCacheControl() {
        return cacheControl;
    }

    public static void setCacheControl(String cacheControl) {
        HttpHeaderRequest.cacheControl = cacheControl;
    }

    public static String getConnection() {
        return connection;
    }

    public static void setConnection(String connection) {
        HttpHeaderRequest.connection = connection;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(String userAgent) {
        HttpHeaderRequest.userAgent = userAgent;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public byte[] getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(byte[] requestURI) {
        this.requestURI = requestURI;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public byte[] get(HttpFieldName name) {
        return headers.get(name);
    }

    public void set(HttpFieldName name, byte[] value) {
        headers.put(name, value);
    }

    public void set(HttpFieldName name, String value) {
        set(name, value.getBytes());
    }

    public void addCookie(Cookie cookie) {
        this.cookie.add(cookie);
    }

    public Iterator<Cookie> cookieIterator() {
        return cookie.iterator();
    }

    public void deleteCookie() {
        cookie.clear();
    }

    public void setData(byte[] data) {
        set(HttpHeaders.contentLength, ArrayHelper.intToArry(data.length));
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
    //</editor-fold>

    public byte[] getBytes() {
        return getBytes(true);
    }

    public byte[] getBytes(boolean returnData) {
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
        if (returnData && data != null) {
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
        if (returnData && data != null) {
            System.arraycopy(data, 0, r, l, data.length);
            l += data.length;
        }

        return r;
    }

    @Override
    public String toString() {
        return new String(getBytes(false));
    }
}
