package http.stream.input;


import http.helper.ArrayHelper;
import http.helper.C;

import java.io.IOException;
import java.io.InputStream;

/**
Transfer-Encoding:chunked

Chunked-Body   = *chunk
 "0" CRLF
 footer
 CRLF

 chunk          = chunk-size [ chunk-ext ] CRLF
 chunk-data CRLF

 hex-no-zero    = <HEX за исключением "0">

 chunk-size     = hex-no-zero *HEX
 chunk-ext      = *( ";" chunk-ext-name [ "=" chunk-ext-value ] )
 chunk-ext-name = token
 chunk-ext-val  = token | quoted-string
 chunk-data     = chunk-size(OCTET)

 footer         = *entity-header

 * Created by Андрей on 10.08.14.
 */
public class HttpChunkedInputStream extends InputStream {
    protected InputStream inputStream;

    private int chunkSize = 0;
    private int ihunkIndex = -1;
    private int lCS = 8;
    private byte[] chunkSizeBytes = new byte[lCS];
    private int iCS = -1;
    private boolean isChunk = true;
    private boolean isChunkSize = true;
    private boolean isEnd = false;

    public HttpChunkedInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        if (isEnd) {
            return -1;
        }

        int b = inputStream.read();

        if (isChunk) {
            // Новая часть
            if (isChunkSize) {
                if (b == C.I_CR || b == C.I_SEMICOLON) {
                    int chunkSize = ArrayHelper.parseInt(chunkSizeBytes, 0, iCS, 16, -1);
                    if (chunkSize > 0) {
                        newChunk(chunkSize);
                        iCS = -1;
                    } else if (chunkSize == 0) {
                        isEnd = true;
                    } else {
                        throw new IOException("Chunk size error " + new String(chunkSizeBytes, 0, iCS + 1));
                    }
                    isChunkSize = false;
                } else {
                    iCS++;
                    if (-1 < iCS && iCS < lCS) {
                        chunkSizeBytes[iCS] = (byte) b;
                    } else {
                        throw new IOException("Chunk size excessive. Max size " + lCS + " char");
                    }
                }
            } else if (b == C.I_LF) {
                isChunk = false;
            }
        } else if (isChunkData()) {
            return b;
        } else {
            if (b == C.I_CR) {

            } else if (b == C.I_LF) {
                isChunk = true;
                isChunkSize = true;
            } else {
                throw new IOException("Is not CRLF chunk-data");
            }
        };

        return read();
    }

    /**
     *
     * @param b
     * @return это конец части
     */
    private boolean isChunkData() {
        ihunkIndex++;
        return ihunkIndex < chunkSize;
    }

    private void newChunk(int size) throws IOException {
        chunkSize = size;
        ihunkIndex = -1;
    }
}
