package http.client;

import java.io.IOException;

/**
 * Created by Андрей on 15.09.14.
 */
public interface HttpClientCache {
    public String get(HttpRequestProcess client) throws IOException;
    public void save(HttpRequestProcess client, byte[] data, byte[] charset, int timeMin) throws IOException;
}
