package http.stream.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Андрей on 06.09.14.
 */
public class HttpEncodingOutputStream extends OutputStream {
    private OutputStreamWriter outputStreamWriter;

    public HttpEncodingOutputStream(OutputStream outputStream, String charsetName) throws UnsupportedEncodingException {
        this.outputStreamWriter = new OutputStreamWriter(outputStream, charsetName);
    }

    @Override
    public void write(int b) throws IOException {
        outputStreamWriter.write(b);
    }
}
