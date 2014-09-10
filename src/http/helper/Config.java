package http.helper;

/**
 * Created by arybalko on 10.09.14.
 */
public class Config {
    private static String defaultUserName = "";
    private static String urlCharset = "UTF-8";

    public static String getDefaultUserName() {
        return defaultUserName;
    }

    public static void setDefaultUserName(String defaultUserName) {
        Config.defaultUserName = defaultUserName;
    }

    public static String getUrlCharset() {
        return urlCharset;
    }

    public static void setUrlCharset(String urlCharset) {
        Config.urlCharset = urlCharset;
    }
}
