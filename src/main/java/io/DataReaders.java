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
public class DataReaders {

    public static DataReader forByteBuffer(ByteBuffer bb) {
        return new DataReader(new ByteBufferSource(bb));
    }

    public static DataReader forReadableByteChannel(ReadableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 18);
        BufferedSource buf = new ReadableByteChannelSource(bb, chan);
        return new DataReader(buf);
    }

    public static DataReader forSeekableByteChannel(SeekableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 16);
        BufferedSource buf = new SeekableByteChannelSource(bb, chan);
        return new DataReader(buf);
    }

    public static DataReader forInputStream(InputStream is) throws IOException {
        return forReadableByteChannel(Channels.newChannel(is));
    }

    public static DataReader forFile(Path path, OpenOption... options) throws IOException {
        return forSeekableByteChannel(Files.newByteChannel(path, options));
    }

    private DataReaders() {
    }
}
