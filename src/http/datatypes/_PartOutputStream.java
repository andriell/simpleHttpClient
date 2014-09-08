package http.datatypes;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Андрей on 10.08.14.
 */
public abstract class _PartOutputStream extends OutputStream {
    abstract public void newPart(int size) throws IOException;
}
