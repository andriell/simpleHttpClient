package http.datatypes;

/**
 * Created by arybalko on 25.08.14.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 *  Common place for datatypes utils.
 *
 * @author dac@eng.sun.com
 * @author Jason Hunter [jch@eng.sun.com]
 * @author James Todd [gonzo@eng.sun.com]
 * @author Costin Manolache
 */
public class HttpDateFormat {
    // Всегда эта локаль
    public final static Locale locale = Locale.US;
    // Все без исключений форматы HTTP даты/времени ДОЛЖНЫ быть представлены в Greenwich Mean Time (GMT)
    public final static TimeZone timeZone = TimeZone.getTimeZone("GMT");

    // RFC 822, дополненный в RFC 1123 "Sun, 06 Nov 1994 08:49:37 GMT"
    public final static String rfc1123Pattern = "EEE, dd MMM yyyy HH:mm:ss z";
    public final static int rfc1123PatternLength = 29;
    // RFC 850, переписанный как RFC 1036 "Sunday, 06-Nov-94 08:49:37 GMT"
    private final static String rfc1036Pattern = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";
    // формат asctime() ANSI C "Sun Nov  6 08:49:37 1994"
    private final static String asctimePattern = "EEE MMM d HH:mm:ss yyyy";
    // может использоваться в старых cookies
    public final static String oldCookiePattern = "EEE, dd-MMM-yyyy HH:mm:ss z";

    public final static DateFormat rfc1123Format = new SimpleDateFormat(rfc1123Pattern, locale);
    public final static DateFormat rfc1036Format = new SimpleDateFormat(rfc1036Pattern, locale);
    public final static DateFormat asctimeFormat = new SimpleDateFormat(asctimePattern, locale);
    public final static DateFormat oldCookieFormat = new SimpleDateFormat(oldCookiePattern, locale);

    static {
        rfc1123Format.setTimeZone(timeZone);
        rfc1036Format.setTimeZone(timeZone);
        asctimeFormat.setTimeZone(timeZone);
        oldCookieFormat.setTimeZone(timeZone);
    }
}
