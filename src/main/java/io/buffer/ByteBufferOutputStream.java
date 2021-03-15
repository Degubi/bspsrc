package io.buffer;

import java.io.*;
import java.nio.*;

/**
 * OutputStream adapter for a ByteBuffer.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        if (buf.isReadOnly()) {
            throw new IllegalArgumentException("Buffer is read-only");
        }

        this.buf = buf;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            buf.put((byte) b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized void write(byte[] bytes, int off, int len) throws IOException {
        try {
            buf.put(bytes, off, len);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }
}
