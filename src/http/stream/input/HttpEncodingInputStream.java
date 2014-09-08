package http.stream.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Андрей on 06.09.14.
 */
public class HttpEncodingInputStream  extends InputStream {
    protected InputStreamReader inputStreamReader;

    public HttpEncodingInputStream(InputStream inputStream, String charsetName) throws UnsupportedEncodingException {
        this.inputStreamReader = new InputStreamReader(inputStream, charsetName);
    }

    @Override
    public int read() throws IOException {
        return inputStreamReader.read();
    }
}
