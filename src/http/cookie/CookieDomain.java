package http.cookie;

import http.datatypes.Domain;
import http.helper.ArrayHelper;
import http.helper.C;

import java.util.Arrays;

/**
 * От обычных доменов отличается тем, что может начинаться с точки и потому применяются другие правила сравнения
 * Created by arybalko on 09.09.14.
 */
public class CookieDomain {
    private byte[] data;

    public CookieDomain(byte[] data) {
        this.data = ArrayHelper.toLowerCase(data);
    }

    /**
     * На subdomain можно отправлять куки с этого домена
     *
     * CookieDomain .example.com
     * Domain example.com
     * Domain www.example.com
     * Domain cs.example.com
     *
     * OR
     *
     * CookieDomain example.com
     * Domain example.com
     *
     * @param domain
     * @return
     */
    public boolean forDomain(Domain domain) {
        byte[] dataDomain = domain.getBytes();
        if (data == null || dataDomain == null) {
            return false;
        }
        // Домен не должен начанаться с точки
        if (dataDomain[0] == 46) {
            return false;
        }

        int l = data.length;
        int lDomain = dataDomain.length;

        // точка в начале домена означает, что можно отдавать информацию всем поддоменам этого домена
        if (data[0] == C.FULL_STOP) {
            if (lDomain < l - 1) {
                return false;
            }

            boolean c46 = false;
            while(0 < l && 0 < lDomain) {
                l--;
                lDomain--;
                if (data[l] != dataDomain[lDomain]) {
                    return false;
                }
                if (data[l] == C.FULL_STOP) {
                    c46 = true;
                }
            }
            if (lDomain == 0) {
                return c46;
            }
            return dataDomain[lDomain] == C.FULL_STOP && c46;
        } else {
            if (lDomain != l) {
                return false;
            }

            boolean c46 = false;
            while(0 < l) {
                l--;
                if (dataDomain[l] != data[l]) {
                    return false;
                }
                if (data[l] == C.FULL_STOP) {
                    c46 = true;
                }
            }
            return c46;
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
        CookieDomain domain = (CookieDomain) o;
        return Arrays.equals(data, domain.data);
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    public int compareTo(CookieDomain o) {
        return ArrayHelper.compare(data, o.data);
    }
}
