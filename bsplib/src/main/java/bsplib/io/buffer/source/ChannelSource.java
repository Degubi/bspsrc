package bsplib.io.buffer.source;

import bsplib.io.buffer.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @param <T>
 */
public abstract class ChannelSource<T extends Channel> implements BufferedSource {

    protected final ByteBuffer buf;
    protected final T chan;
    protected final ByteBufferChannel chanBuf;

    public ChannelSource(ByteBuffer buf, T chan) {
        this.buf = buf;
        this.buf.limit(0);
        this.chan = chan;
        this.chanBuf = new ByteBufferChannel(buf);
    }

    @Override
    public void position(long newPos) throws IOException {
        throw new NonSeekableSourceException();
    }

    @Override
    public long position() throws IOException {
        throw new NonSeekableSourceException();
    }

    @Override
    public long size() throws IOException {
        throw new NonSeekableSourceException();
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
    public boolean canSeek() {
        return false;
    }

    @Override
    public void close() throws IOException {
        flush();
        chan.close();
    }
}
