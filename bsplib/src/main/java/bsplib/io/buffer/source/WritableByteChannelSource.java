package bsplib.io.buffer.source;

import bsplib.log.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.logging.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class WritableByteChannelSource extends ChannelSource<WritableByteChannel> {

    private static final Logger L = LogUtils.getLogger();

    public WritableByteChannelSource(ByteBuffer buf, WritableByteChannel chan) {
        super(buf, chan);
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean canGrow() {
        return true;
    }

    @Override
    public boolean canSeek() {
        return false;
    }

    @Override
    public void flush() throws IOException {
        // stop here and start from the beginning
        buf.flip();

        int start = buf.position();

        // write buffer to channel
        while (chan.write(buf) > 0);

        L.log(Level.FINEST, "flush: {0} bytes written", buf.position() - start);

        buf.clear();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        throw new NonReadableSourceException();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int n = chanBuf.write(src);

        // check if buffer is empty
        if (n == -1) {
            L.finest("write: buffer full");

            flush();

            if (src.remaining() > buf.capacity()) {
                L.finest("write: write buffer directly");

                // src buffer larger than internal buffer, write directly
                n = chan.write(src);
            } else {
                // write buffered
                n = chanBuf.write(src);
            }
        }

        return n;
    }

    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        throw new NonReadableSourceException();
    }

    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        // check if additional bytes need to be buffered
        if (buf.remaining() < required) {
            flush();

            // if there are still not enough bytes available, throw exception
            if (buf.remaining() < required) {
                throw new EOFException();
            }
        }

        return buf;
    }
}
