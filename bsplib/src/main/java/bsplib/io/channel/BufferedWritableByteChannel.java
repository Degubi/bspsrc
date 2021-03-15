package bsplib.io.channel;

import bsplib.io.buffer.source.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedWritableByteChannel
    extends BufferedChannel<WritableByteChannel, WritableByteChannelSource>
    implements WritableByteChannel {

    public static final int DEFAULT_BUFFER_SIZE = 1 << 20; // 1 MiB

    public BufferedWritableByteChannel(WritableByteChannel out, int bufferSize) {
        super(out, new WritableByteChannelSource(ByteBuffer.allocateDirect(bufferSize), out));
    }

    public BufferedWritableByteChannel(WritableByteChannel chan) {
        this(chan, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return buf.write(src);
    }
}
