package http.datatypes;

import http.helper.ArrayHelper;

import java.util.Arrays;

/**
 * Created by arybalko on 26.08.14.
 */
public class Domain implements Comparable<Domain> {
    // "."
    private static byte FULL_STOP = 46;

    private byte[] data;

    public Domain(byte[] data) {
        this.data = ArrayHelper.toLowerCase(data);
    }


    /**
     * На subdomain можно отправлять куки с этого домена
     *
     * domain example.com
     * subdomain example.com
     *
     * domain .example.com
     * subdomain example.com
     * subdomain www.example.com
     * subdomain cs.example.com
     * @param subdomain
     * @return
     */
    public boolean isSubDomain(Domain subdomain) {
        if (data == null || subdomain.data == null) {
            return false;
        }
        // субдомен не должен начанаться с точки
        if (subdomain.data[0] == 46) {
            return false;
        }

        // точка в начале домена означает, что можно отдавать информацию всем поддоменам этого домена
        if (data[0] == FULL_STOP) {
            int lDomain = data.length;
            int lSubdomain = subdomain.data.length;

            if (lSubdomain < lDomain - 1) {
                return false;
            }

            lDomain--;
            lSubdomain--;
            boolean c46 = false;
            while(0 < lDomain) {
                if (data[lDomain] != subdomain.data[lSubdomain]) {
                    return false;
                }
                if (data[lDomain] == FULL_STOP) {
                    c46 = true;
                }
                lDomain--;
                lSubdomain--;
            }
            if (lSubdomain < 0) {
                return c46;
            }
            return subdomain.data[lSubdomain] == FULL_STOP && c46;
        } else {
            if (this == subdomain) {
                return true;
            }
            int length = data.length;
            if (subdomain.data.length != length)
                return false;

            boolean c46 = false;
            for (int i=0; i<length; i++) {
                if (subdomain.data[i] != data[i]) {
                    return false;
                }
                if (data[i] == FULL_STOP) {
                    c46 = true;
                }
            }
            return c46;
        }
    }

    public int length() {
        return data.length;
    }

    public byte[] getByte() {
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
        Domain domain = (Domain) o;
        return Arrays.equals(data, domain.data);
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    @Override
    public int compareTo(Domain o) {
        return ArrayHelper.compare(data, o.data);
    }
}
