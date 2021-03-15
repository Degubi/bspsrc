package bsplib.lump;

import java.io.*;

/**
 * Thrown to indicate reading errors in lump file structures
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class LumpException extends IOException {

    public LumpException() {
        super();
    }

    public LumpException(String message) {
        super(message);
    }

    public LumpException(String message, Throwable cause) {
        super(message, cause);
    }

    public LumpException(Throwable cause) {
        super(cause);
    }
}
