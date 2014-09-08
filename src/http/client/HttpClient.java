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
    private HttpExceptionHandler exceptionHandler = null;
    private CookieManager cookieManager = null;
    private RedirectManager redirectManager = null;

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
        httpRequestProcess.setCookieManager(cookieManager);
        httpRequestProcess.setExceptionHandler(exceptionHandler);
        httpRequestProcess.setRedirectManager(redirectManager);
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
        try {
            HttpPartOutputStream httpPartOutputStream = new HttpPartOutputStream();
            client.setOutputStream(httpPartOutputStream);
            client.run();

            byte[] data = httpPartOutputStream.getBytes();
            if (data == null) {
                data = new byte[0];
            }

            byte[] contentType = client.getLastTransaction().getHeaderResponse().get(HttpHeaders.contentType);
            if (contentType != null) {
                String charsetName = ContentType.getCharset(contentType);
                if (charsetName != null) {
                    return new String(data, charsetName);
                }
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
        return getString(newRequest(), user, url);
    }

    public String getString(String user, String url) {
        HttpRequestProcess client = newRequest();
        try {
            return getString(client, user, new HttpUrl(url));
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
        return getString(client);
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
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
    //</editor-fold>
}
