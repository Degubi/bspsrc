package bsplib.io.channel;

import bsplib.io.buffer.source.*;
import java.io.*;
import java.nio.channels.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @param <C>
 * @param <B>
 */
public abstract class BufferedChannel <C extends Channel, B extends BufferedSource> implements Channel {

    protected final C chan;
    protected final B buf;

    public BufferedChannel(C chan, B buf) {
        this.chan = chan;
        this.buf = buf;
    }

    @Override
    public boolean isOpen() {
        return chan.isOpen();
    }

    @Override
    public void close() throws IOException {
        buf.close();
        chan.close();
    }
}
