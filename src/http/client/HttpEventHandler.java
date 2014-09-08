package http.client;

/**
 * Created by Андрей on 07.09.14.
 */
public abstract class HttpEventHandler {
    private HttpEventHandler after;

    public void go(HttpRequestProcess httpRequestProcess) {
        on(httpRequestProcess);
        if (after != null) {
            after.go(httpRequestProcess);
        }
    }

    public HttpEventHandler getAfter() {
        return after;
    }

    private void after(HttpEventHandler current, HttpEventHandler after) {
        if (current == after) {
            return;
        }
        if (current.after == null) {
            current.after = after;
        } else {
            after(current.after, after);
        }
    }

    public void after(HttpEventHandler after) {
        after(this, after);
    }

    abstract public void on(HttpRequestProcess httpRequestProcess);
}
