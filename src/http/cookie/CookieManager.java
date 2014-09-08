package http.cookie;


import http.datatypes.Domain;
import http.datatypes.Path;

/**
 * Created by arybalko on 26.08.14.
 */
public interface CookieManager {
    /**
     * Записать куки
     * @param user
     * @param httpCookie
     */
    public void set(String user, Cookie httpCookie);

    /**
     * Прочитать куки
     * @param user
     * @param domain
     * @param path
     * @return
     */
    public Iterable<Cookie> get(String user, Domain domain, Path path, boolean isHttps);

    /**
     * Окончание сессии
     * @return Rолличество удаленных кук
     */
    public int sessionEnd();
}
