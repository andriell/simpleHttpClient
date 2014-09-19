package http.datatypes;

import http.helper.ArrayHelper;
import http.helper.C;
import http.helper.Config;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by Андрей on 03.08.14.
 */
public class HttpUrl implements Comparable<HttpUrl>, Cloneable {
    private HttpUrlSheme scheme;
    private Domain domain = null;
    private Integer port = 0;
    private Path path = null;
    private byte[] pathDecode = null;
    private byte[] query = null;
    private byte[] fragment = null;

    private String charset = Config.getUrlCharset();
    private int hashCode = 0;

    public HttpUrl(String url, String charset) throws Exception {
        this.charset = charset;
        parseUrl(url.getBytes(this.charset));
    }

    public HttpUrl(String url) throws ParseException, UnsupportedEncodingException {
        parseUrl(url.getBytes(this.charset));
    }

    public HttpUrl(byte[] url) throws ParseException {
        parseUrl(url);
    }

    public HttpUrl(byte[] url, String charset) throws ParseException {
        this.charset = charset;
        parseUrl(url);
    }

    public HttpUrl(HttpUrl httpUrl, byte[] url) throws ParseException, CloneNotSupportedException {
        parseUrl(httpUrl, url);
    }

    public HttpUrl(HttpUrl httpUrl, byte[] url, String charset) throws ParseException, CloneNotSupportedException {
        this.charset = charset;
        parseUrl(httpUrl, url);
    }

    public HttpUrl(HttpUrl httpUrl, String url) throws ParseException, CloneNotSupportedException {
        parseUrl(httpUrl, url.getBytes());
    }

    public HttpUrl(HttpUrl httpUrl, String url, String charset) throws ParseException, UnsupportedEncodingException, CloneNotSupportedException {
        this.charset = charset;
        parseUrl(httpUrl, url.getBytes(charset));
    }

    private void parseUrl(HttpUrl httpUrl, byte[] url) throws ParseException, CloneNotSupportedException {
        if (url == null) {
            return;
        }

        int startIndex = parseScheme(url);
        if (scheme == null) {
            scheme = httpUrl.scheme;
            domain = httpUrl.domain;
            if (url[0] == C.SOLIDUS) {
                startIndex = parsePath(url, 0);
                parseQuery(url, startIndex);
            } else {
                startIndex = parsePath(url, 0);
                Path newPath = httpUrl.path.clone();
                newPath.add(path);
                path = newPath;
                parseQuery(url, startIndex);
            }
        } else {
            startIndex = parseDomain(url, startIndex);
            if (domain == null) {
                throw new ParseException(new String(url, 0, 10) + " - unknown domain", startIndex);
            }
            startIndex = parsePath(url, startIndex);
            parseQuery(url, startIndex);
        }
    }

    private void parseUrl(byte[] url) throws ParseException {
        if (url == null) {
            return;
        }

        int l = url.length;
        // http://a.ru
        if (l < 11) {
            throw new ParseException(new String(url) + ".length < 11", l);
        }

        int startIndex = parseScheme(url);
        if (scheme == null) {
            throw new ParseException(new String(url, 0, 10) + " - unknown scheme", 0);
        }
        startIndex = parseDomain(url, startIndex);
        if (domain == null) {
            throw new ParseException(new String(url, 0, 10) + " - unknown domain", startIndex);
        }
        startIndex = parsePath(url, startIndex);
        parseQuery(url, startIndex);
    }

    private int parseScheme(byte[] url) {
        if (url.length < 9) {
            return 0;
        }
        //             h    t    t    p    s    :   /   /
        // HTTPS:// = {72,  84,  84,  80,  83,  58, 47, 47}
        // https:// = {104, 116, 116, 112, 115, 58, 47, 47}
        if (
            (url[0] == 72 || url[0] == 104)
            && (url[1] == 84 || url[1] == 116)
            && (url[2] == 84 || url[2] == 116)
            && (url[3] == 80 || url[3] == 112)
        ) {
            if (
                url[4] == 58
                && url[5] == 47
                && url[6] == 47
            ) {
                scheme = HttpUrlSheme.http;
                return 7;
            }
            if (
                (url[4] == 83 || url[4] == 115)
                && url[5] == 58
                && url[6] == 47
                && url[7] == 47
            ) {
                scheme = HttpUrlSheme.https;
                return 8;
            }
        }
        return 0;
    }

    private int parseDomain(byte[] url, int startIndex) {
        int l = url.length;
        if (!(-1 < startIndex && startIndex < l)) {
            return startIndex;
        }

        int startPort = -1;
        for (int i = startIndex; i < l; i++) {
            if (url[i] == C.COLON) { // : COLON:58
                domain = new Domain(Arrays.copyOfRange(url, startIndex, i));
                startPort = i;
            } else if (url[i] == C.NUMBER_SIGN || url[i] == C.SOLIDUS || url[i] == C.QUESTION) {
                // # NUMBER SIGN:35
                // / SOLIDUS:47
                // ? QUESTION MARK:63
                if (-1 < startPort) {
                    port = ArrayHelper.parseInt(url, startPort + 1, i - 1, 10, -1);
                } else {
                    domain = new Domain(Arrays.copyOfRange(url, startIndex, i));
                }
                return i;
            }
        }
        if (-1 < startPort) {
            port = ArrayHelper.parseInt(url, startPort + 1, l - 1, 10, -1);
        } else {
            domain = new Domain(Arrays.copyOfRange(url, startIndex, l));
        }
        return l;
    }

    private int parsePath(byte[] url, int startIndex) {
        int l = url.length;
        if (!(-1 < startIndex && startIndex < l)) {
            return startIndex;
        }
        if (url[startIndex] == C.NUMBER_SIGN || url[startIndex] == C.QUESTION) {
            // # NUMBER SIGN:35
            // ? QUESTION MARK:63
            path = new Path(C.BS_SOLIDUS);
            return startIndex;
        }

        for (int i = startIndex; i < l; i++) {
            if (url[i] == C.NUMBER_SIGN || url[i] == C.QUESTION) {
                // # NUMBER SIGN:35
                // ? QUESTION MARK:63
                path = new Path(Arrays.copyOfRange(url, startIndex, i));
                return i;
            }
        }
        path = new Path(Arrays.copyOfRange(url, startIndex, l));
        return l;
    }

    private void parseQuery(byte[] url, int startIndex) {
        int l = url.length;
        if (!(-1 < startIndex && startIndex < l)) {
            return;
        }
        if (url[startIndex] == C.NUMBER_SIGN) {
            // # NUMBER SIGN:35
            fragment = Arrays.copyOfRange(url, startIndex + 1, l);
            return;
        }

        for (int i = startIndex; i < l; i++) {
            if (url[i] == C.NUMBER_SIGN) { // # NUMBER SIGN:35
                query = Arrays.copyOfRange(url, startIndex + 1, i);
                fragment = Arrays.copyOfRange(url, i + 1, l);
                return;
            }
        }
        query = Arrays.copyOfRange(url, startIndex + 1, l);
        return;
    }

    public HttpUrlSheme getScheme() {
        return scheme;
    }

    public Domain getDomain() {
        return domain;
    }

    public int getPort() {
        if (port > 0) {
            return port;
        } else if (HttpUrlSheme.https.equals(scheme)) {
            return 443;
        }
        return 80;
    }

    public Path getPath() {
        return path;
    }

    public byte[] getPathDecode() {
        if (pathDecode == null) {
            try {
                pathDecode = URLDecoder.decode(toString(false, false, false, true, true, false), charset).getBytes();
            } catch (UnsupportedEncodingException e) {}
        }
        return pathDecode;
    }

    public byte[] getQuery() {
        return query;
    }

    public byte[] getFragment() {
        return fragment;
    }

    public String getCharset() {
        return charset;
    }

    public byte[] getBytes(boolean scheme, boolean domain, boolean port, boolean path, boolean query, boolean fragment) {
        int l = 0;
        if (scheme) {
            l += this.scheme.name().length() + C.BS_COLON_SS.length;
        }
        if (domain) {
            l += this.domain.length();
        }
        byte[] portByte = null;
        if (port && this.port > 0) {
            portByte = ArrayHelper.intToArry(this.port);
            l += C.BS_COLON.length + portByte.length;
        }
        if (path) {
            l += this.path.length();
        }
        if (query && this.query != null) {
            l += C.BS_QUESTION.length + this.query.length;
        }
        if (fragment && this.fragment != null) {
            l += C.BS_NUMBER_SIGN.length + this.fragment.length;
        }

        byte[] r = new byte[l];
        l = 0;
        if (scheme) {
            System.arraycopy(this.scheme.name().getBytes(), 0, r, l, this.scheme.name().length());
            l += this.scheme.name().length();
            System.arraycopy(C.BS_COLON_SS, 0, r, l, C.BS_COLON_SS.length);
            l += C.BS_COLON_SS.length;
        }
        if (domain) {
            System.arraycopy(this.domain.getBytes(), 0, r, l, this.domain.length());
            l += this.domain.length();
        }
        if (portByte != null) {
            System.arraycopy(C.BS_COLON, 0, r, l, C.BS_COLON.length);
            l += C.BS_COLON.length;
            System.arraycopy(portByte, 0, r, l, portByte.length);
            l += portByte.length;
        }
        if (path) {
            System.arraycopy(this.path.getBytes(), 0, r, l, this.path.length());
            l += this.path.length();
        }
        if (query && this.query != null) {
            System.arraycopy(C.BS_QUESTION, 0, r, l, C.BS_QUESTION.length);
            l += C.BS_QUESTION.length;
            System.arraycopy(this.query, 0, r, l, this.query.length);
            l += this.query.length;
        }
        if (fragment && this.fragment != null) {
            System.arraycopy(C.BS_NUMBER_SIGN, 0, r, l, C.BS_NUMBER_SIGN.length);
            l += C.BS_NUMBER_SIGN.length;
            System.arraycopy(this.fragment, 0, r, l, this.fragment.length);
            l += this.fragment.length;
        }
        return r;
    }

    public byte[] getBytes() {
        return getBytes(true, true, true, true, true, true);
    }

    public String toString(boolean scheme, boolean domain, boolean port, boolean path, boolean query, boolean fragment) {
        return new String(getBytes(scheme, domain, port, path, query, fragment));
    }

    @Override
    public String toString() {
        return new String(getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpUrl that = (HttpUrl) o;

        if (scheme != that.scheme) return false;
        if (!domain.equals(that.domain)) return false;
        if (getPort() != that.getPort()) return false;
        if (!Arrays.equals(getPathDecode(), that.getPathDecode())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = scheme.hashCode();
            hashCode = 31 * hashCode + domain.hashCode();
            hashCode = 31 * hashCode + getPort();
            hashCode = 31 * hashCode + Arrays.hashCode(getPathDecode());
            hashCode = 31 * hashCode + (fragment != null ? Arrays.hashCode(fragment) : 0);
        }
        return hashCode;
    }

    @Override
    public int compareTo(HttpUrl o) {
        if (this == o) {
            return 0;
        }
        int r = 0;
        //<editor-fold desc="scheme">
        if (scheme == null) {
            if (o.scheme == null) {
                r = 0;
            } else {
                r = 1;
            }
        } else {
            if (o.scheme == null) {
                r = -1;
            } else {
                r = scheme.compareTo(o.scheme);
            }
        }
        if (r != 0) {
            return r;
        }
        //</editor-fold>
        //<editor-fold desc="domain">
        if (domain == null) {
            if (o.domain == null) {
                r = 0;
            } else {
                r = 1;
            }
        } else {
            if (o.domain == null) {
                r = -1;
            } else {
                r = domain.compareTo(o.domain);
            }
        }
        if (r != 0) {
            return r;
        }
        //</editor-fold>
        //<editor-fold desc="port">
        if (port != o.port) {
            return port - o.port;
        }
        //</editor-fold>
        //<editor-fold desc="getPathDecode">
        r = ArrayHelper.compare(getPathDecode(), o.getPathDecode());
        if (r != 0) {
            return r;
        }
        //</editor-fold>
        return ArrayHelper.compare(fragment, o.fragment);
    }

    @Override
    protected HttpUrl clone() throws CloneNotSupportedException {
        HttpUrl r = (HttpUrl) super.clone();
        r.scheme = scheme;
        r.domain = domain.clone();
        r.port = port;
        r.path = path.clone();
        r.pathDecode = pathDecode.clone();
        r.query = query.clone();
        r.fragment = fragment.clone();

        r.charset = charset;
        r.hashCode = hashCode;

        return r;
    }
}
