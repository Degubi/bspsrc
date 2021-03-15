package bsplib.io.buffer.source;

import bsplib.io.buffer.*;
import java.io.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelUtils {

    public static boolean isReadable(Channel c) {
        if (!(c instanceof ReadableByteChannel)) {
            return false;
        }

        try {
            // try to read an empty buffer
            ReadableByteChannel rbc = (ReadableByteChannel) c;
            rbc.read(ByteBufferUtils.EMPTY);
            return true;
        } catch (NonReadableChannelException ex) {
            return false;
        } catch (IOException ex) {
            throw new RuntimeException("Broken channel", ex);
        }
    }

    public static boolean isWritable(Channel c) {
        if (!(c instanceof WritableByteChannel)) {
            return false;
        }

        try {
            // try to write an empty buffer
            WritableByteChannel wbc = (WritableByteChannel) c;
            wbc.write(ByteBufferUtils.EMPTY);
            return true;
        } catch (NonWritableChannelException ex) {
            return false;
        } catch (IOException ex) {
            throw new RuntimeException("Broken channel", ex);
        }
    }

    private ChannelUtils() {
    }
}
