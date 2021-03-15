package bspinfo.gui;

import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspFileException extends IOException {

    /**
     * Creates a new instance of
     * <code>BspFileException</code> without detail message.
     */
    public BspFileException() {
    }

    /**
     * Constructs an instance of
     * <code>BspFileException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BspFileException(String msg) {
        super(msg);
    }

    public BspFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
