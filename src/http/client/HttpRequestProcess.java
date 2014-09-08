package http.client;

import http.datatypes.HttpUrl;
import http.stream.input.HttpChunkedInputStream;
import http.stream.input.HttpContentLengthInputStream;
import http.stream.output.HttpHeaderOutputStream;
import http.cookie.Cookie;
import http.cookie.CookieManager;
import http.datatypes.HttpRequestMethod;
import http.datatypes.HttpUrlSheme;
import http.header.HttpHeaderRequest;
import http.header.HttpHeaders;
import http.helper.ArrayHelper;
import http.helper.C;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class HttpRequestProcess implements Runnable {
    private Socket socket;
    private OutputStream socketOutputStream;
    private InputStream socketInputStream;

    private HttpExceptionHandler exceptionHandler = null;
    private CookieManager cookieManager = null;
    private RedirectManager redirectManager = null;

    private HttpUrl url;
    private HttpHeaderRequest headerRequest = new HttpHeaderRequest();
    private HttpHeaderOutputStream headerResponse;
    private OutputStream userOutputStream;
    private String user = "";
    private int maxRequest = 6;
    private int requestCount = 0;

    // Запрос полностью сформирован, в следующей строчке он будет отправлен
    private HttpEventHandler beforeRequest = null;
    // После заголовков ответа
    private HttpEventHandler afterResponseHeaders = null;
    // Непосредственно перед редиректом
    private HttpEventHandler beforeRedirect = null;
    // В конце работы метода
    private HttpEventHandler beforeComplite = null;

    @Override
    public void run() {
        try {
            query();
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.exception(e, this);
            }
        }
    }

    private void query() throws Exception {
        if (url == null) {
            throw new NullPointerException("Url is null");
        }
        requestCount++;
        if (requestCount > maxRequest) {
            throw new Exception("Too many redirects " + requestCount + " Max=" + maxRequest);
        }

        // Перемещен ли этот URL
        if (redirectManager != null) {
            url = redirectManager.get(url);
        }

        socket = SocketManager.get(url);
        socketOutputStream = socket.getOutputStream();
        socketInputStream = socket.getInputStream();

        headerRequest.url(url);
        if (cookieManager != null && url != null) {
            Iterable<Cookie> cookies = cookieManager.get(user, url.getDomain(), url.getPath(), url.getScheme() == HttpUrlSheme.https);
            for (Cookie cookie: cookies) {
                headerRequest.addCookie(cookie);
            }
        }
        if (beforeRequest != null) {
            beforeRequest.on(this);
        }
        socketOutputStream.write(headerRequest.getBytes());

        headerResponse = new HttpHeaderOutputStream(1000, 1000);
        // Читаем заголовок ответа
        while (! headerResponse.isEnd()) {
            headerResponse.write(socketInputStream.read());
        }
        if (afterResponseHeaders != null) {
            afterResponseHeaders.on(this);
        }

        // Записываем куки
        if (cookieManager != null) {
            Iterator<byte[]> cookies = headerResponse.cookieIterator();
            while (cookies.hasNext()) {
                cookieManager.set(user, new Cookie(cookies.next()));
            }
        }

        // Редиректы
        HttpUrl location = null;
        if (headerResponse.getStatusCode() == 301) { // Постоянно перемещен
            location = new HttpUrl(headerResponse.get(HttpHeaders.location));
            if (redirectManager != null) {
                redirectManager.set(url, location);
            }
        } else if (headerResponse.getStatusCode() == 302) { // Временно перемещен
            location = new HttpUrl(headerResponse.get(HttpHeaders.location));
        }
        if (location != null) {
            url = location;
            socket.close();
            if (beforeRedirect != null) {
                beforeRedirect.on(this);
            }
            query();
            return;
        }

        /* Тело
         * 1. Любое сообщение ответа, которое НЕ ДОЛЖНО включать тело сообщения (message-body) (например ответы с кодами
         * состояния 1xx, 204, 304 и все ответы на запрос HEAD) всегда завершается пустой строкой после полей заголовка,
         * независимо от полей заголовка объекта (entity-header fields), представленных в сообщении.
         */
        if (
            headerResponse.getStatusCode() < 200
            || headerResponse.getStatusCode() == 204
            || headerResponse.getStatusCode() == 304
            || headerRequest.getMethod().equals(HttpRequestMethod.HEAD)
        ) {
            socket.close();
            if (beforeComplite != null) {
                beforeComplite.on(this);
            }
            return;
        }

        InputStream bodyInputStream = null;

        /*
         * 2. Если поле заголовка Transfer-Encoding (раздел 14.40) присутствует и указывает на применение кодирования
         * передачи "chunked", то длина определяется кодированием по кускам (chunked encoding) (раздел 3.6).
         */
        byte[] transferEncoding = headerResponse.get(HttpHeaders.transferEncoding);
        if (transferEncoding != null && ArrayHelper.equalIgnoreCase(transferEncoding, C.BS_CHUNKED)) {
            bodyInputStream = new HttpChunkedInputStream(socketInputStream);
        }
        /*
         * 3. Если поле заголовка Content-Length (раздел 14.14) присутствует,
         * то его значение представляет длину тела сообщения (message-body) в байтах.
         */
        int contentLength = headerResponse.getInt(HttpHeaders.contentLength, -1);
        if (contentLength > 0) {
            bodyInputStream = new HttpContentLengthInputStream(socketInputStream, contentLength);
        }

        if (bodyInputStream == null) {
            socket.close();
            return;
        }

        // Content-Encoding
        byte[] contentEncoding = headerResponse.get(HttpHeaders.contentEncoding);
        if (contentEncoding != null && ArrayHelper.equalIgnoreCase(contentEncoding, C.BS_GZIP)) {
            bodyInputStream = new GZIPInputStream(bodyInputStream);
        }

        // contentType
        if (userOutputStream != null) {
            while (true) {
                int b = bodyInputStream.read();
                if (b < 0) {
                    break;
                }
                userOutputStream.write(b);
            }
        }

        socket.close();
        if (beforeComplite != null) {
            beforeComplite.on(this);
        }
        return;
    }

    //<editor-fold desc="Events">
    public void beforeRequest(HttpEventHandler beforeRequest) {
        this.beforeRequest = beforeRequest;
    }

    public void afterResponseHeaders(HttpEventHandler afterResponseHeaders) {
        this.afterResponseHeaders = afterResponseHeaders;
    }

    public void beforeRedirect(HttpEventHandler beforeRedirect) {
        this.beforeRedirect = beforeRedirect;
    }

    public void beforeComplite(HttpEventHandler beforeComplite) {
        this.beforeComplite = beforeComplite;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    public HttpRequestMethod getMethod() {
        return headerRequest.getMethod();
    }

    public void setMethod(HttpRequestMethod method) {
        headerRequest.setMethod(method);
    }

    public HttpExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(HttpExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public RedirectManager getRedirectManager() {
        return redirectManager;
    }

    public void setRedirectManager(RedirectManager redirectManager) {
        this.redirectManager = redirectManager;
    }

    public HttpUrl getUrl() {
        return url;
    }

    public void setUrl(HttpUrl url) {
        this.url = url;
    }

    public HttpHeaderRequest getHeaderRequest() {
        return headerRequest;
    }

    public void setHeaderRequest(HttpHeaderRequest headerRequest) {
        this.headerRequest = headerRequest;
    }

    public HttpHeaderOutputStream getHeaderResponse() {
        return headerResponse;
    }

    public OutputStream getOutputStream() {
        return userOutputStream;
    }

    public void setOutputStream(OutputStream userOutputStream) {
        this.userOutputStream = userOutputStream;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getMaxRequest() {
        return maxRequest;
    }

    public void setMaxRequest(int maxRequest) {
        this.maxRequest = maxRequest;
    }
    //</editor-fold>
}