package http.datatypes;

import http.helper.ArrayHelper;
import http.helper.C;

import java.util.Arrays;

/**
 * Created by arybalko on 26.08.14.
 */
public class Domain implements Comparable<Domain> {
    private byte[] data;

    public Domain(byte[] data) {
        this.data = ArrayHelper.toLowerCase(data);
    }


    /**
     * На subdomain можно отправлять куки с этого домена
     *
     * domain example.com
     * subdomain example.com
     * subdomain www.example.com
     * subdomain cs.example.com
     * @param subdomain
     * @return
     */
    public boolean isSubdomain(Domain subdomain, boolean orEqual) {
        if (data == null || subdomain.data == null) {
            return false;
        }

        int lDomain = data.length;
        int lSubdomain = subdomain.data.length;

        if ((lSubdomain < lDomain) || (lSubdomain == lDomain && !orEqual)) {
            return false;
        }

        while(0 < lDomain) {
            lDomain--;
            lSubdomain--;
            if (data[lDomain] != subdomain.data[lSubdomain]) {
                return false;
            }
        }

        if (lSubdomain == lDomain) {
            return orEqual;
        }
        lSubdomain--;
        return subdomain.data[lSubdomain] == C.FULL_STOP;
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
