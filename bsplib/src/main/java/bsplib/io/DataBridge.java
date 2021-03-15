package bsplib.io;

import bsplib.io.buffer.source.*;
import java.io.*;
import java.nio.*;

/**
 * Base class for both DataReader and DataWriter.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DataBridge implements Closeable, Swappable {

    public static enum Origin {
        BEGINNING, CURRENT, END
    }

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

    public long position() throws IOException {
        return buf.position();
    }

    public void position(long newPos) throws IOException {
        buf.position(newPos);
    }

    public long size() throws IOException {
        return buf.size();
    }

    //////////////
    // Seekable //
    //////////////

    public void seek(long where, Origin whence) throws IOException {
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

    public long remaining() throws IOException {
        return size() - position();
    }

    public boolean hasRemaining() throws IOException {
        return remaining() > 0;
    }

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
