package io.buffer.source;

import io.buffer.*;
import java.io.*;
import java.nio.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferSource implements BufferedSource {

    private final ByteBuffer buf;
    private final ByteBufferChannel chanBuf;

    public ByteBufferSource(ByteBuffer buffer) {
        buf = buffer;
        chanBuf = new ByteBufferChannel(buffer);
    }

    @Override
    public void position(long newPos) throws IOException {
        if (newPos < 0 || newPos > buf.limit()) {
            throw new IllegalArgumentException();
        }
        buf.position((int) newPos);
    }

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public long size() throws IOException {
        return buf.limit();
    }

    @Override
    public ByteOrder order() {
        return buf.order();
    }

    @Override
    public void order(ByteOrder order) {
        buf.order(order);
    }

    @Override
    public int bufferSize() {
        return buf.capacity();
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return !buf.isReadOnly();
    }

    @Override
    public boolean canGrow() {
        return false;
    }

    @Override
    public boolean canSeek() {
        return true;
    }

    @Override
    public void flush() {
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return chanBuf.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!canWrite()) {
            throw new NonWritableSourceException();
        }
        return chanBuf.write(src);
    }

    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        if (buf.remaining() < required) {
            throw new EOFException();
        }
        return buf;
    }

    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        if (!canWrite()) {
            throw new NonWritableSourceException();
        }
        return requestRead(required);
    }

    @Override
    public void close() throws IOException {
    }
}
