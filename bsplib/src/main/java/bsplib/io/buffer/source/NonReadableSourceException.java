package bsplib.io.buffer.source;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class NonReadableSourceException extends UnsupportedOperationException {

    /**
     * Creates a new instance of <code>NonReadableSourceException</code> without
     * detail message.
     */
    public NonReadableSourceException() {
    }

    /**
     * Constructs an instance of <code>NonReadableSourceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NonReadableSourceException(String msg) {
        super(msg);
    }
}
