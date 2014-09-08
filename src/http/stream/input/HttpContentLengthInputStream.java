package http.stream.input;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Андрей on 06.09.14.
 */
public class HttpContentLengthInputStream extends InputStream {
    protected InputStream inputStream;
    protected int contentLength;

    public HttpContentLengthInputStream(InputStream inputStream, int contentLength) {
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    @Override
    public int read() throws IOException {
        if (contentLength <= 0) {
            return -1;
        }
        contentLength--;
        return inputStream.read();
    }
}
