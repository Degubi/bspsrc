package io;

import io.buffer.source.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataWriters {

    public static DataWriter forByteBuffer(ByteBuffer bb) {
        return new DataWriter(new ByteBufferSource(bb));
    }

    public static DataWriter forWritableByteChannel(WritableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 18);
        BufferedSource buf = new WritableByteChannelSource(bb, chan);
        return new DataWriter(buf);
    }

    public static DataWriter forSeekableByteChannel(SeekableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 16);
        BufferedSource buf = new SeekableByteChannelSource(bb, chan);
        return new DataWriter(buf);
    }

    public static DataWriter forOutputStream(OutputStream os) throws IOException {
        return forWritableByteChannel(Channels.newChannel(os));
    }

    public static DataWriter forFile(Path path, OpenOption... options) throws IOException {
        return forSeekableByteChannel(Files.newByteChannel(path, options));
    }

    private DataWriters() {
    }
}
