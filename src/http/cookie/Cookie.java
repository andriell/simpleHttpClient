package http.cookie;

import http.datatypes.Domain;
import http.datatypes.HttpDate;
import http.datatypes.HttpDateFormat;
import http.datatypes.Path;
import http.helper.ArrayHelper;
import http.helper.C;

import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by arybalko on 25.08.14.
 */
public class Cookie {
    byte[] data;

    private byte[] key = null;
    private byte[] val = null;

    // Дата когда кука закончит действие. Если не указана, то действие заканчивается в конце сессии
    private HttpDate expires = null;
    private Domain domain = null;
    private Path path = new Path(C.BS_SOLIDUS);
    private boolean secure = false;
    private boolean httpOnly  = false;

    public Cookie(byte[] data) throws ParseException {
        this.data = data;
        parse();
    }

    public void parse() throws ParseException {
        int l = data.length;
        int semicolon = -1;
        int equals = 0;
        for (int i = 0; i < l; i++) {
            if (data[i] == C.EQUALS && equals == 0) {
                equals = i;
            }
            if (data[i] == C.SEMICOLON) {
                addAttribute(semicolon, equals, i);
                semicolon = i;
                equals = 0;
            }
        }
        addAttribute(semicolon, equals, l);
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

    private void addAttribute(int semicolon, int equals, int end) throws ParseException {
        int startKey, endKey, startVal, endVal;
        byte[] key;
        byte[] val;

        if (semicolon < equals && equals < end) {
            // Remove any leading or trailing WSP characters from the attribute-
            // name string and the attribute-value string.
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

            // If a cookie has both the Max-Age and the Expires attribute, the Max-
            // Age attribute has precedence and controls the expiration date of the
            // cookie.
            if (ArrayHelper.equalIgnoreCase(C.BS_EXPIRES, key) && expires == null) {
                expires = new HttpDate(val);
            } else if (ArrayHelper.equalIgnoreCase(C.BS_MAX_AGE, key)) {
                expires = new HttpDate();
                expires.add(ArrayHelper.parseInt(val, 0) * 1000);
            } else if (ArrayHelper.equalIgnoreCase(C.BS_DOMAIN, key)) {
                domain = new Domain(val);
            } else if (ArrayHelper.equalIgnoreCase(C.BS_PATH, key)) {
                path = new Path(val);
            } else if (this.key == null && this.val == null) {
                this.key = key;
                this.val = val;
            } else {
                throw new ParseException("Unknown attribute " + new String(key), 0);
            }
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

            if (ArrayHelper.equalIgnoreCase(C.BS_SECURE, key)) {
                secure = true;
            } else if (ArrayHelper.equalIgnoreCase(C.BS_HTTP_ONLY, key)) {
                httpOnly = true;
            } else {
                throw new ParseException("Unknown attribute " + new String(key), 0);
            }
        }
    }

    public byte[] buildSetCookie() {
        int l = key.length + C.BS_EQUALS.length + val.length;
        byte[] expiresByte = null;
        if (expires != null) {
            expiresByte = expires.toRfc1123().getBytes();
            l += C.BS_SEMICOLON_SP.length + C.BS_EXPIRES.length + C.BS_EQUALS.length + expiresByte.length;
        }
        if (domain != null) {
            l += C.BS_SEMICOLON_SP.length + C.BS_DOMAIN.length + C.BS_EQUALS.length + domain.length();
        }
        if (path != null) {
            l += C.BS_SEMICOLON_SP.length + C.BS_PATH.length + C.BS_EQUALS.length + path.length();
        }
        if (secure) {
            l += C.BS_SEMICOLON_SP.length + C.BS_SECURE.length;
        }
        if (httpOnly) {
            l += C.BS_SEMICOLON_SP.length + C.BS_HTTP_ONLY.length;
        }

        byte[] r = new byte[l];
        l = 0;
        System.arraycopy(key, 0, r, l, key.length);
        l += key.length;
        System.arraycopy(C.BS_EQUALS, 0, r, l, C.BS_EQUALS.length);
        l += C.BS_EQUALS.length;
        System.arraycopy(val, 0, r, l, val.length);
        l += val.length;

        if (expires != null) {
            System.arraycopy(C.BS_SEMICOLON_SP, 0, r, l, C.BS_SEMICOLON_SP.length);
            l += C.BS_SEMICOLON_SP.length;
            System.arraycopy(C.BS_EXPIRES, 0, r, l, C.BS_EXPIRES.length);
            l += C.BS_EXPIRES.length;
            System.arraycopy(C.BS_EQUALS, 0, r, l, C.BS_EQUALS.length);
            l += C.BS_EQUALS.length;
            System.arraycopy(expiresByte, 0, r, l, expiresByte.length);
            l += expiresByte.length;
        }
        if (domain != null) {
            System.arraycopy(C.BS_SEMICOLON_SP, 0, r, l, C.BS_SEMICOLON_SP.length);
            l += C.BS_SEMICOLON_SP.length;
            System.arraycopy(C.BS_DOMAIN, 0, r, l, C.BS_DOMAIN.length);
            l += C.BS_DOMAIN.length;
            System.arraycopy(C.BS_EQUALS, 0, r, l, C.BS_EQUALS.length);
            l += C.BS_EQUALS.length;
            System.arraycopy(domain.getByte(), 0, r, l, domain.length());
            l += domain.length();
        }
        if (path != null) {
            System.arraycopy(C.BS_SEMICOLON_SP, 0, r, l, C.BS_SEMICOLON_SP.length);
            l += C.BS_SEMICOLON_SP.length;
            System.arraycopy(C.BS_PATH, 0, r, l, C.BS_PATH.length);
            l += C.BS_PATH.length;
            System.arraycopy(C.BS_EQUALS, 0, r, l, C.BS_EQUALS.length);
            l += C.BS_EQUALS.length;
            System.arraycopy(path.getByte(), 0, r, l, path.length());
            l += path.length();
        }
        if (secure) {
            System.arraycopy(C.BS_SEMICOLON_SP, 0, r, l, C.BS_SEMICOLON_SP.length);
            l += C.BS_SEMICOLON_SP.length;
            System.arraycopy(C.BS_SECURE, 0, r, l, C.BS_SECURE.length);
            l += C.BS_SECURE.length;
        }
        if (httpOnly) {
            System.arraycopy(C.BS_SEMICOLON_SP, 0, r, l, C.BS_SEMICOLON_SP.length);
            l += C.BS_SEMICOLON_SP.length;
            System.arraycopy(C.BS_HTTP_ONLY, 0, r, l, C.BS_HTTP_ONLY.length);
            l += C.BS_HTTP_ONLY.length;
        }
        return r;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getVal() {
        return val;
    }

    public HttpDate getExpires() {
        return expires;
    }

    public Domain getDomain() {
        return domain;
    }

    public Path getPath() {
        return path;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public String toString() {
        return new String(buildSetCookie());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cookie cookie = (Cookie) o;

        if (secure != cookie.secure) return false;
        if (domain != null ? !domain.equals(cookie.domain) : cookie.domain != null) return false;
        if (key != null ? !Arrays.equals(key, cookie.key) : cookie.key != null) return false;
        if (path != null ? !path.equals(cookie.path) : cookie.path != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? Arrays.hashCode(key) : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (secure ? 1 : 0);
        return result;
    }
}
