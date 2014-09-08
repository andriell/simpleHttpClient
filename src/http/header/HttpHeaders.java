package http.header;

import http.helper.ArrayHelper;

import java.util.Comparator;

/**
 * Created by arybalko on 25.08.14.
 */
public class HttpHeaders {
    // general-header - 7
    public static final HttpFieldName cacheControl = new HttpFieldName("Cache-Control");
    public static final HttpFieldName connection = new HttpFieldName("Connection");
    public static final HttpFieldName date = new HttpFieldName("Date");
    public static final HttpFieldName pragma = new HttpFieldName("Pragma");
    public static final HttpFieldName transferEncoding = new HttpFieldName("Transfer-Encoding");
    public static final HttpFieldName upgrade = new HttpFieldName("Upgrade");
    public static final HttpFieldName via = new HttpFieldName("Via");

    // request-header - 18
    public static final HttpFieldName accept = new HttpFieldName("Accept");
    public static final HttpFieldName acceptCharset = new HttpFieldName("Accept-Charset");
    public static final HttpFieldName acceptEncoding = new HttpFieldName("Accept-Encoding");
    public static final HttpFieldName acceptLanguage = new HttpFieldName("Accept-Language");
    public static final HttpFieldName authorization = new HttpFieldName("Authorization");
    public static final HttpFieldName cookie = new HttpFieldName("Cookie");
    public static final HttpFieldName from = new HttpFieldName("From");
    public static final HttpFieldName host = new HttpFieldName("Host");
    public static final HttpFieldName ifMatch = new HttpFieldName("If-Match");
    public static final HttpFieldName ifModifiedSince = new HttpFieldName("If-Modified-Since");
    public static final HttpFieldName ifNoneMatch = new HttpFieldName("If-None-Match");
    public static final HttpFieldName ifRange = new HttpFieldName("If-Range");
    public static final HttpFieldName ifUnmodifiedSince = new HttpFieldName("If-Unmodified-Since");
    public static final HttpFieldName maxForwards = new HttpFieldName("Max-Forwards");
    public static final HttpFieldName proxyAuthorization = new HttpFieldName("Proxy-Authorization");
    public static final HttpFieldName range = new HttpFieldName("Range");
    public static final HttpFieldName referer = new HttpFieldName("Referer");
    public static final HttpFieldName userAgent = new HttpFieldName("User-Agent");

    // response-header - 10
    public static final HttpFieldName age = new HttpFieldName("Age");
    public static final HttpFieldName location = new HttpFieldName("Location");
    public static final HttpFieldName proxyAuthenticate = new HttpFieldName("Proxy-Authenticate");
    public static final HttpFieldName public_ = new HttpFieldName("Public");
    public static final HttpFieldName retryAfter = new HttpFieldName("Retry-After");
    public static final HttpFieldName server = new HttpFieldName("Server");
    public static final HttpFieldName setCookie = new HttpFieldName("Set-Cookie");
    public static final HttpFieldName vary = new HttpFieldName("Vary");
    public static final HttpFieldName warning = new HttpFieldName("Warning");
    public static final HttpFieldName wwwAuthenticate = new HttpFieldName("WWW-Authenticate");

    // entity-header - 12
    public static final HttpFieldName allow = new HttpFieldName("Allow");
    public static final HttpFieldName contentBase = new HttpFieldName("Content-Base");
    public static final HttpFieldName contentEncoding = new HttpFieldName("Content-Encoding");
    public static final HttpFieldName contentLanguage = new HttpFieldName("Content-Language");
    public static final HttpFieldName contentLength = new HttpFieldName("Content-Length");
    public static final HttpFieldName contentLocation = new HttpFieldName("Content-Location");
    public static final HttpFieldName contentMD5 = new HttpFieldName("Content-MD5");
    public static final HttpFieldName contentRange = new HttpFieldName("Content-Range");
    public static final HttpFieldName contentType = new HttpFieldName("Content-Type");
    public static final HttpFieldName eTag = new HttpFieldName("ETag");
    public static final HttpFieldName expires = new HttpFieldName("Expires");
    public static final HttpFieldName lastModified = new HttpFieldName("Last-Modified");

    public static HttpFieldName[] generalHeader;
    public static HttpFieldName[] requestHeader;
    public static HttpFieldName[] responseHeader;
    public static HttpFieldName[] entityHeader;

    public static final HttpHeadersComparator comparator;

    static {
        generalHeader = new HttpFieldName[7];
        generalHeader[0] = cacheControl;
        generalHeader[1] = connection;
        generalHeader[2] = date;
        generalHeader[3] = pragma;
        generalHeader[4] = transferEncoding;
        generalHeader[5] = upgrade;
        generalHeader[6] = via;

        requestHeader = new HttpFieldName[18];
        requestHeader[0] = accept;
        requestHeader[1] = acceptCharset;
        requestHeader[2] = acceptEncoding;
        requestHeader[3] = acceptLanguage;
        requestHeader[4] = authorization;
        requestHeader[5] = cookie;
        requestHeader[6] = from;
        requestHeader[7] = host;
        requestHeader[8] = ifModifiedSince;
        requestHeader[9] = ifMatch;
        requestHeader[10] = ifNoneMatch;
        requestHeader[11] = ifRange;
        requestHeader[12] = ifUnmodifiedSince;
        requestHeader[13] = maxForwards;
        requestHeader[14] = proxyAuthorization;
        requestHeader[15] = range;
        requestHeader[16] = referer;
        requestHeader[17] = userAgent;

        responseHeader = new HttpFieldName[10];
        responseHeader[0] = age;
        responseHeader[1] = location;
        responseHeader[2] = proxyAuthenticate;
        responseHeader[3] = public_;
        responseHeader[4] = retryAfter;
        responseHeader[5] = server;
        responseHeader[6] = setCookie;
        responseHeader[7] = vary;
        responseHeader[8] = warning;
        responseHeader[9] = wwwAuthenticate;

        entityHeader = new HttpFieldName[12];
        entityHeader[0] = allow;
        entityHeader[1] = contentBase;
        entityHeader[2] = contentEncoding;
        entityHeader[3] = contentLanguage;
        entityHeader[4] = contentLength;
        entityHeader[5] = contentLocation;
        entityHeader[6] = contentMD5;
        entityHeader[7] = contentRange;
        entityHeader[8] = contentType;
        entityHeader[9] = eTag;
        entityHeader[10] = expires;
        entityHeader[11] = lastModified;

        comparator = new HttpHeadersComparator();
    }

    private static int getOrder(HttpFieldName fieldName) {
        int i = 0;
        for (HttpFieldName name : generalHeader) {
            if (fieldName.equals(name)) {
                return i;
            }
            i++;
        }
        for (HttpFieldName name : requestHeader) {
            if (fieldName.equals(name)) {
                return i;
            }
            i++;
        }
        for (HttpFieldName name : responseHeader) {
            if (fieldName.equals(name)) {
                return i;
            }
            i++;
        }
        for (HttpFieldName name : entityHeader) {
            if (fieldName.equals(name)) {
                return i;
            }
            i++;
        }

        return i;
    }

    private static class HttpHeadersComparator implements Comparator<HttpFieldName> {
        @Override
        public int compare(HttpFieldName o1, HttpFieldName o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            int rez = getOrder(o1) - getOrder(o2);
            if (rez == 0) {
                return ArrayHelper.compare(o1.data, o2.data);
            }
            return rez;
        }
    }

    public static HttpHeadersComparator getComparator() {
        return comparator;
    }
}
