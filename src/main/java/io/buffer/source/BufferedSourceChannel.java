package io.buffer.source;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedSourceChannel implements ByteChannel {

    private final BufferedSource buf;
    private boolean closed;

    public BufferedSourceChannel(BufferedSource buf) {
        this.buf = buf;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }

        if (!buf.canRead()) {
            throw new NonReadableChannelException();
        }

        return buf.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }

        if (!buf.canWrite()) {
            throw new NonWritableChannelException();
        }

        return buf.write(src);
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        buf.flush();
    }
}
