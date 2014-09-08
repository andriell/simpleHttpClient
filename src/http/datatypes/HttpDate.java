package http.datatypes;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by arybalko on 25.08.14.
 */
public class HttpDate implements Comparable<HttpDate> {
    Date date;

    public HttpDate() {
        date = new Date();
    }

    public HttpDate(byte[] date) throws ParseException {
        parse(new String(date));
    }

    public HttpDate(String date) throws ParseException {
        parse(date);
    }

    private void parse(String date) throws ParseException {
        try {
            this.date = HttpDateFormat.rfc1123Format.parse(date);
            return;
        } catch (ParseException e) {}
        try {
            this.date = HttpDateFormat.rfc1036Format.parse(date);
            return;
        } catch (ParseException e) {}
        try {
            this.date = HttpDateFormat.asctimeFormat.parse(date);
            return;
        } catch (ParseException e) {}
        this.date = HttpDateFormat.oldCookieFormat.parse(date);
    }

    public void add(long l) {
        date.setTime(date.getTime() + l);
    }

    public String format(DateFormat df) {
        return df.format(date);
    }

    public String toRfc1123() {
        return HttpDateFormat.rfc1123Format.format(date);
    }

    public String toRfc1036() {
        return HttpDateFormat.rfc1036Format.format(date);
    }

    public String toAsctime() {
        return HttpDateFormat.asctimeFormat.format(date);
    }

    public long getTime() {
        return this.date.getTime();
    }

    @Override
    public String toString() {
        return HttpDateFormat.rfc1123Format.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpDate httpDate = (HttpDate) o;
        return date.equals(httpDate.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public int compareTo(HttpDate o) {
        long l = getTime() - o.getTime();
        if (l < 0) {
            return -1;
        } else if (l > 0) {
            return 1;
        }
        return 0;
    }
}
