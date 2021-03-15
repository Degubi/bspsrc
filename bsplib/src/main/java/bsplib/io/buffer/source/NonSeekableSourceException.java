package bsplib.io.buffer.source;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class NonSeekableSourceException extends UnsupportedOperationException {

    /**
     * Creates a new instance of <code>NonSeekableSourceException</code> without
     * detail message.
     */
    public NonSeekableSourceException() {
    }

    /**
     * Constructs an instance of <code>NonSeekableSourceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NonSeekableSourceException(String msg) {
        super(msg);
    }
}
