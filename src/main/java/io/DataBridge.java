package io;

import io.buffer.source.*;
import java.io.*;
import java.nio.*;

/**
 * Base class for both DataReader and DataWriter.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DataBridge implements Seekable, Closeable, Swappable {

    protected final BufferedSource buf;

    public DataBridge(BufferedSource buf) {
        this.buf = buf;
    }

    ///////////////
    // Swappable //
    ///////////////

    @Override
    public ByteOrder order() {
        return buf.order();
    }

    @Override
    public void order(ByteOrder order) {
        buf.order(order);
    }

    //////////////////
    // Positionable //
    //////////////////

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public void position(long newPos) throws IOException {
        buf.position(newPos);
    }

    @Override
    public long size() throws IOException {
        return buf.size();
    }

    //////////////
    // Seekable //
    //////////////

    @Override
    public void seek(long where, Seekable.Origin whence) throws IOException {
        long pos = 0;
        switch (whence) {
            case BEGINNING:
                pos = where;
                break;

            case CURRENT:
                pos = position() + where;
                break;

            case END:
                pos = size() - where;
                break;
        }
        position(pos);
    }

    @Override
    public long remaining() throws IOException {
        return size() - position();
    }

    @Override
    public boolean hasRemaining() throws IOException {
        return remaining() > 0;
    }

    @Override
    public void align(int align) throws IOException {
        if (align < 0) {
            throw new IllegalArgumentException();
        } else if (align == 0) {
            return;
        }

        long pos = position();
        long rem = pos % align;
        if (rem != 0) {
            position(pos + align - rem);
        }
    }

    ///////////////
    // Closeable //
    ///////////////

    @Override
    public void close() throws IOException {
        buf.close();
    }
}
