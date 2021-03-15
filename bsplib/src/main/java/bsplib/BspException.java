package bsplib;

import java.io.*;

/**
 * Thrown to indicate reading errors in BSP file structures.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspException extends IOException {

    public BspException() {
        super();
    }

    public BspException(String message) {
        super(message);
    }

    public BspException(String message, Throwable cause) {
        super(message, cause);
    }

    public BspException(Throwable cause) {
        super(cause);
    }

}
