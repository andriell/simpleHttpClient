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
public class HttpUrl implements Comparable<HttpUrl> {
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
        parseUrl(url);
        this.charset = charset;
    }

    private HttpUrlSheme parseScheme(byte[] url) throws ParseException {
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
                return HttpUrlSheme.http;
            }
            if (
                (url[4] == 83 || url[4] == 115)
                && url[5] == 58
                && url[6] == 47
                && url[7] == 47
            ) {
                return HttpUrlSheme.https;
            }
        }
        return null;
    }

    private void parseUrl(byte[] url) throws ParseException {
        int l = url.length;

        // http://a.ru
        if (l < 11) {
            throw new ParseException(new String(url) + ".length < 11", l);
        }

        int startDomain = 0;

        //<editor-fold desc="scheme">
        scheme = parseScheme(url);
        if (scheme == HttpUrlSheme.http) {
            startDomain = 7;
        } else if (scheme == HttpUrlSheme.https) {
            startDomain = 8;
        } else {
            throw new ParseException(new String(url, 0, 10) + " - unknown scheme", 0);
        }
        //</editor-fold>

        int startPort = 0;
        int startPath = 0;
        int startQuery = 0;
        int startFragment = 0;
        for (int i = startDomain; i < l; i++) {
            if (url[i] == 58 && domain == null) { // : COLON:58
                domain = new Domain(Arrays.copyOfRange(url, startDomain, i));
                startPort = i;
            } else if (url[i] == 47) { // / SOLIDUS:47
                if (startPath < 1) {
                    startPath = i;
                }
                if (startPort > 0) {
                    port = ArrayHelper.parseInt(url, startPort + 1, i - 1, 10, -1);
                    startPort = 0;
                } else if (domain == null) {
                    domain = new Domain(Arrays.copyOfRange(url, startDomain, i));
                }
            } else if (url[i] == 63) { // ? QUESTION MARK:63
                if (startQuery < 1) {
                    startQuery = i + 1;
                }
                if (path == null && 0 < startPath) {
                    path = new Path(Arrays.copyOfRange(url, startPath, i));
                }
            } else if (url[i] == 35) { // # NUMBER SIGN:35
                fragment = Arrays.copyOfRange(url, i + 1, l);
                if (query == null && 0 < startQuery) {
                    query = Arrays.copyOfRange(url, startQuery, i);
                }
                if (path == null && 0 < startPath) {
                    path = new Path(Arrays.copyOfRange(url, startPath, i));
                }
            }
        }
        if (domain == null && 0 < startDomain) {
            domain = new Domain(Arrays.copyOfRange(url, startDomain, l));
        }
        if (query == null && 0 < startQuery) {
            query = Arrays.copyOfRange(url, startQuery, l);
        }
        if (path == null && 0 < startPath) {
            path = new Path(Arrays.copyOfRange(url, startPath, l));
        }
        if (path == null) {
            path = new Path(C.BS_SOLIDUS);
        }
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
            int l = path.length();
            if (query != null) {
                l += C.BS_QUESTION.length + query.length;
            }

            pathDecode = new byte[l];

            l = 0;
            System.arraycopy(path.getBytes(), 0, pathDecode, l, path.length());
            l += path.length();
            if (query != null) {
                System.arraycopy(C.BS_QUESTION, 0, pathDecode, l, C.BS_QUESTION.length);
                l += C.BS_QUESTION.length;
                System.arraycopy(query, 0, pathDecode, l, query.length);
                l += query.length;
            }

            try {
                pathDecode = URLDecoder.decode(new String(pathDecode), charset).getBytes();
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

    public byte[] domainPort() {
        int l = domain.length();
        byte[] port = null;
        if (this.port > 0) {
            port = ArrayHelper.intToArry(this.port);
            l += C.BS_COLON.length + port.length;
        }

        byte[] r = new byte[l];
        l = 0;

        System.arraycopy(domain.getBytes(), 0, r, l, domain.length());
        l += domain.length();
        if (port != null) {
            System.arraycopy(C.BS_COLON, 0, r, l, C.BS_COLON.length);
            l += C.BS_COLON.length;
            System.arraycopy(port, 0, r, l, port.length);
            l += port.length;
        }

        return r;
    }

    public byte[] domainPathParam() {
        int l = scheme.name().length()
                + C.BS_COLON_SS.length
                + domain.length();
        byte[] port = null;
        if (this.port > 0) {
            port = ArrayHelper.intToArry(this.port);
            l += C.BS_COLON.length + port.length;
        }
        l += path.length();
        if (query != null) {
            l += C.BS_QUESTION.length + query.length;
        }

        byte[] r = new byte[l];
        l = 0;

        System.arraycopy(scheme.name().getBytes(), 0, r, l, scheme.name().length());
        l += scheme.name().length();
        System.arraycopy(C.BS_COLON_SS, 0, r, l, C.BS_COLON_SS.length);
        l += C.BS_COLON_SS.length;
        System.arraycopy(domain.getBytes(), 0, r, l, domain.length());
        l += domain.length();
        if (this.port > 0) {
            System.arraycopy(C.BS_COLON, 0, r, l, C.BS_COLON.length);
            l += C.BS_COLON.length;
            System.arraycopy(port, 0, r, l, port.length);
            l += port.length;
        }
        System.arraycopy(path.getBytes(), 0, r, l, path.length());
        l += path.length();
        if (query != null) {
            System.arraycopy(C.BS_QUESTION, 0, r, l, C.BS_QUESTION.length);
            l += C.BS_QUESTION.length;
            System.arraycopy(query, 0, r, l, query.length);
            l += query.length;
        }

        return r;
    }

    public byte[] getBytes() {
        byte[] domainPath = domainPathParam();
        int l = domainPath.length;
        if (fragment != null) {
            l += C.BS_NUMBER_SIGN.length + fragment.length;
        }

        byte[] r = new byte[l];
        l = 0;

        System.arraycopy(domainPath, 0, r, l, domainPath.length);
        l += domainPath.length;
        if (fragment != null) {
            System.arraycopy(C.BS_NUMBER_SIGN, 0, r, l, C.BS_NUMBER_SIGN.length);
            l += C.BS_NUMBER_SIGN.length;
            System.arraycopy(fragment, 0, r, l, fragment.length);
            l += fragment.length;
        }
        return r;
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
        int r = 0;
        if (scheme != o.scheme) {
            if (scheme == null) {
                return 1;
            }
            if (o.scheme == null) {
                return -1;
            }
            return scheme.compareTo(o.scheme);
        }
        if (domain != o.domain) {
            if (domain == null) {
                return 1;
            }
            if (o.domain == null) {
                return -1;
            }
            return domain.compareTo(o.domain);
        }
        if (port != o.port) {
            return port - o.port;
        }
        r = ArrayHelper.compare(getPathDecode(), o.getPathDecode());
        if (r != 0) {
            return r;
        }
        if (fragment != o.fragment) {
            if (fragment == null) {
                return 1;
            }
            if (o.fragment == null) {
                return -1;
            }
            return ArrayHelper.compare(fragment, o.fragment);
        }
        return 0;
    }
}
