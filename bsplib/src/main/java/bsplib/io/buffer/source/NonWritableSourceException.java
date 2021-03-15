package bsplib.io.buffer.source;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class NonWritableSourceException extends UnsupportedOperationException {

    /**
     * Creates a new instance of <code>NonWritableSourceException</code> without detail
     * message.
     */
    public NonWritableSourceException() {
    }

    /**
     * Constructs an instance of <code>NonWritableSourceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NonWritableSourceException(String msg) {
        super(msg);
    }
}
