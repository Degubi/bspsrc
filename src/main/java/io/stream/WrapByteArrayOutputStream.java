package io.stream;

import java.io.*;
import java.nio.*;

/**
 * ByteArrayOutputStream extension that can wrap its working array directly into
 * a ByteBuffer.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class WrapByteArrayOutputStream extends ByteArrayOutputStream {

    public WrapByteArrayOutputStream() {
        super();
    }

    public WrapByteArrayOutputStream(int size) {
        super(size);
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }
}
