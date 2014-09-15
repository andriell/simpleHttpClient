package http.client;

import http.cookie.CookieManager;
import http.datatypes.ContentType;
import http.datatypes.HttpUrl;
import http.header.HttpHeaders;
import http.stream.output.HttpPartOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Андрей on 07.09.14.
 */
public class HttpClient {
    private HttpSocketFactory httpSocketFactory = new HttpSoketFactoryDefault();
    private HttpExceptionHandler exceptionHandler = null;
    private CookieManager cookieManager = null;
    private RedirectManager redirectManager = null;
    private HttpClientCache clientCache = null;

    private HttpEventHandler beforeRequest = null;
    private HttpEventHandler afterResponseHeaders = null;
    private HttpEventHandler beforeRedirect = null;
    private HttpEventHandler beforeComplite = null;

    private static HttpClient ourInstance = new HttpClient();
    public static HttpClient getInstance() {
        return ourInstance;
    }
    private HttpClient() {}

    public static HttpRequestProcess request() {
        return getInstance().newRequest();
    }

    public HttpRequestProcess newRequest() {
        HttpRequestProcess httpRequestProcess = new HttpRequestProcess();
        httpRequestProcess.setHttpSocketFactory(httpSocketFactory);
        httpRequestProcess.setCookieManager(cookieManager);
        httpRequestProcess.setExceptionHandler(exceptionHandler);
        httpRequestProcess.setRedirectManager(redirectManager);
        httpRequestProcess.beforeRequest(beforeRequest);
        httpRequestProcess.afterResponseHeaders(afterResponseHeaders);
        httpRequestProcess.beforeRedirect(beforeRedirect);
        httpRequestProcess.beforeComplite(beforeComplite);
        return httpRequestProcess;
    }

    //<editor-fold desc="Download">
    public void download(String user, String url, File file) {
        HttpRequestProcess client = newRequest();
        try {
            client.setUser(user);
            client.setUrl(new HttpUrl(url));
            download(client, file);
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.exception(e, client);
            }
        }
    }

    public void download(HttpRequestProcess client, File file) {
        try {
            if (file.isFile()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            client.setOutputStream(bufferedOutputStream);
            client.run();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.exception(e, client);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getString">
    public String getString(HttpRequestProcess client) {
        return getString(client, 0);
    }

    public String getString(HttpRequestProcess client, int timeMin) {
        try {
            if (clientCache != null && timeMin > 0) {
                String r = clientCache.get(client);
                if (r != null) {
                    return r;
                }
            }

            HttpPartOutputStream httpPartOutputStream = new HttpPartOutputStream();
            client.setOutputStream(httpPartOutputStream);
            client.run();

            byte[] data = httpPartOutputStream.getBytes();
            if (data == null) {
                data = new byte[0];
            }

            byte[] contentType = client.getHeaderResponse().get(HttpHeaders.contentType);
            byte[] charsetName = null;
            if (contentType != null) {
                charsetName = ContentType.getCharsetB(contentType);
            }
            if (clientCache != null && timeMin > 0) {
                clientCache.save(client, data, charsetName, timeMin);
            }
            if (charsetName != null) {
                return new String(data, new String(charsetName));
            }
            return new String(data);
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.exception(e, client);
            }
        }
        return "";
    }

    public String getString(String user, HttpUrl url) {
        return getString(newRequest(), user, url, 0);
    }

    public String getString(String user, String url) {
        return getString(user, url, 0);
    }

    public String getString(String user, HttpUrl url, int timeMin) {
        return getString(newRequest(), user, url, timeMin);
    }

    public String getString(String user, String url, int timeMin) {
        HttpRequestProcess client = newRequest();
        try {
            return getString(client, user, new HttpUrl(url), timeMin);
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.exception(e, client);
            }
        }
        return "";
    }

    public String getString(HttpRequestProcess client, String user, HttpUrl url) {
        client.setUser(user);
        client.setUrl(url);
        return getString(client, 0);
    }

    public String getString(HttpRequestProcess client, String user, HttpUrl url, int timeMin) {
        client.setUser(user);
        client.setUrl(url);
        return getString(client, timeMin);
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    public HttpSocketFactory getHttpSocketFactory() {
        return httpSocketFactory;
    }

    public void setHttpSocketFactory(HttpSocketFactory httpSocketFactory) {
        this.httpSocketFactory = httpSocketFactory;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public HttpExceptionHandler getExceptionHandler() {

        return exceptionHandler;
    }

    public void setExceptionHandler(HttpExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public RedirectManager getRedirectManager() {
        return redirectManager;
    }

    public void setRedirectManager(RedirectManager redirectManager) {
        this.redirectManager = redirectManager;
    }

    public HttpClientCache getClientCache() {
        return clientCache;
    }

    public void setClientCache(HttpClientCache clientCache) {
        this.clientCache = clientCache;
    }
    //</editor-fold>

    //<editor-fold desc="Events">
    /**
     * Запрос полностью сформирован, в следующей строчке он будет отправлен
     * @param beforeRequest
     */
    public void beforeRequest(HttpEventHandler beforeRequest) {
        if (this.beforeRequest == null) {
            this.beforeRequest = beforeRequest;
        } else {
            this.beforeRequest.after(beforeRequest);
        }
    }

    /**
     * После заголовков ответа
     * @param afterResponseHeaders
     */
    public void afterResponseHeaders(HttpEventHandler afterResponseHeaders) {
        if (this.afterResponseHeaders == null) {
            this.afterResponseHeaders = afterResponseHeaders;
        } else {
            this.afterResponseHeaders.after(afterResponseHeaders);
        }
    }

    /**
     * Непосредственно перед редиректом
     * @param beforeRedirect
     */
    public void beforeRedirect(HttpEventHandler beforeRedirect) {
        if (this.beforeRedirect == null) {
            this.beforeRedirect = beforeRedirect;
        } else {
            this.beforeRedirect.after(beforeRedirect);
        }
    }

    /**
     * В конце работы метода
     * @param beforeComplite
     */
    public void beforeComplite(HttpEventHandler beforeComplite) {
        if (this.beforeComplite == null) {
            this.beforeComplite = beforeComplite;
        } else {
            this.beforeComplite.after(beforeComplite);
        }
    }
    //</editor-fold>
}
