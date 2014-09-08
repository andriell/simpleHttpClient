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
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class HttpRequestProcess implements Runnable {
    private Socket socket;
    private OutputStream socketOutputStream;
    private InputStream socketInputStream;

    private HttpExceptionHandler exceptionHandler = null;
    private CookieManager cookieManager = null;

    private HttpRequestMethod method = HttpRequestMethod.GET;
    private HttpUrl url;
    private OutputStream userOutputStream;
    private String user = "";
    private int maxRequest = 6;

    private TreeMap<HttpUrl, HttpUrl> redirect301 = new TreeMap<HttpUrl, HttpUrl>();

    ArrayList<HttpTransaction> transactions = new ArrayList<HttpTransaction>(maxRequest);
    HttpTransaction lastTransaction = null;

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
        HttpUrl urlReq = url;

        // Перемещен ли этот URL
        url = redirect301.get(url);
        if (url == null) {
            url = urlReq;
        }

        socket = SocketManager.get(url);
        socketOutputStream = socket.getOutputStream();
        socketInputStream = socket.getInputStream();

        HttpHeaderRequest headerRequest = new HttpHeaderRequest(method, url);
        lastTransaction = new HttpTransaction();
        lastTransaction.setHeaderRequest(headerRequest);

        if (cookieManager != null) {
            Iterable<Cookie> cookies = cookieManager.get(user, url.getDomain(), url.getPath(), url.getScheme() == HttpUrlSheme.https);
            for (Cookie cookie: cookies) {
                headerRequest.setCookie(cookie);
            }
        }
        socketOutputStream.write(headerRequest.getByte());

        HttpHeaderOutputStream headerResponse = new HttpHeaderOutputStream(1000, 1000);
        // Читаем заголовок ответа
        while (! headerResponse.isEnd()) {
            headerResponse.write(socketInputStream.read());
        }

        lastTransaction.setHeaderResponse(headerResponse);
        transactions.add(lastTransaction);

        // Записываем куки
        if (cookieManager != null) {
            Iterator<byte[]> cookies = headerResponse.cookieIterator();
            while (cookies.hasNext()) {
                cookieManager.set(user, new Cookie(cookies.next()));
            }
        }

        // Редиректы
        if (transactions.size() > maxRequest) {
            socket.close();
            return;
        }
        HttpUrl location = null;
        if (headerResponse.getStatusCode() == 301) { // Постоянно перемещен
            location = new HttpUrl(headerResponse.get(HttpHeaders.location));
            redirect301.put(url, location);
        } else if (headerResponse.getStatusCode() == 302) {// Временно перемещен
            location = new HttpUrl(headerResponse.get(HttpHeaders.location));
        }
        if (location != null) {
            url = location;
            socket.close();
            run();
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
            || method.equals(HttpRequestMethod.HEAD)
        ) {
            socket.close();
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
        return;
    }

    //<editor-fold desc="Getters and Setters">
    public HttpExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(HttpExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public HttpUrl getUrl() {
        return url;
    }

    public void setUrl(HttpUrl url) {
        this.url = url;
    }

    public OutputStream getUserOutputStream() {
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

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public HttpTransaction getLastTransaction() {
        return lastTransaction;
    }

    public Iterator<HttpTransaction> getTransactions() {
        return transactions.iterator();
    }
    //</editor-fold>
}