package io;

import java.nio.*;

/**
 * Interface for IO classes that can dynamically swap the byte order.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Swappable {

    public ByteOrder order();

    public void order(ByteOrder order);
}
