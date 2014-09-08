package http.client;

import http.datatypes.HttpUrl;

/**
 * Created by arybalko on 08.09.14.
 */
public interface RedirectManager {
    /**
     * Returns this or new url
     * @param url
     * @return
     */
    public HttpUrl get(HttpUrl url);
    public void set(HttpUrl from, HttpUrl to) throws LoopException;
}
