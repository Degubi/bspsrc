package io.channel;

import io.buffer.source.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedReadableByteChannel
    extends BufferedChannel<ReadableByteChannel, ReadableByteChannelSource>
    implements ReadableByteChannel {

    public static final int DEFAULT_BUFFER_SIZE = 1 << 20; // 1 MiB

    public BufferedReadableByteChannel(ReadableByteChannel in, int bufferSize) {
        super(in, new ReadableByteChannelSource(ByteBuffer.allocateDirect(bufferSize), in));
    }

    public BufferedReadableByteChannel(ReadableByteChannel in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return buf.read(dst);
    }
}
