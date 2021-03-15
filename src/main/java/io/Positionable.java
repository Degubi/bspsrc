package io;

import java.io.*;

/**
 * Interface for IO classes that provide random access.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Positionable {

    /**
     * Sets a new absolute position.
     *
     * @param newPos The new position to set.
     * @throws IOException
     */
    public void position(long newPos) throws IOException;

    /**
     * Obtain the current position.
     *
     * @return
     * @throws IOException
     */
    public long position() throws IOException;

    /**
     * Returns the total size in bytes.
     *
     * @return
     * @throws IOException
     */
    public long size() throws IOException;
}
