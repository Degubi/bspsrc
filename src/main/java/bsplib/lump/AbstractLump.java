package bsplib.lump;

import bsplib.io.*;
import io.buffer.*;
import java.io.*;
import java.nio.*;
import java.util.logging.*;
import log.*;

/**
 * A generic lump class for the normal lump and the game lump.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class AbstractLump {

    private static final Logger L = LogUtils.getLogger();

    private ByteBuffer buffer = ByteBuffer.allocate(0);
    private int offset;
    private int version = 0;
    private int fourCC = 0;
    private boolean compressed = false;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns the number of bytes present in this lump. If the lump is
     * compressed, the uncompressed size will be returned.
     *
     * @return lump length
     */
    public int getLength() {
        return buffer.limit();
    }

    /**
     * Returns the buffer for this lump.
     *
     * @return byte buffer of this lump
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buf) {
        buffer = buf;
        buffer.rewind();
        setCompressed(LzmaUtil.isCompressed(buffer));
    }

    public InputStream getInputStream() {
        ByteBuffer buf = getBuffer();
        buf.rewind();
        return new ByteBufferInputStream(buf);
    }

    public OutputStream getOutputStream() {
        ByteBuffer buf = getBuffer();
        buf.rewind();
        return new ByteBufferOutputStream(buf);
    }

    public void setVersion(int vers) {
        this.version = vers;
    }

    public int getVersion() {
        return version;
    }

    public int getFourCC() {
        return fourCC;
    }

    public void setFourCC(int fourCC) {
        this.fourCC = fourCC;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void compress() {
        if (compressed) {
            return;
        }

        try {
            buffer = LzmaUtil.compress(buffer);
        } catch (IOException ex) {
            L.log(Level.SEVERE, "Couldn't compress lump " + this, ex);
        }

        setCompressed(true);
    }

    public void uncompress() {
        if (!compressed) {
            return;
        }

        try {
            buffer = LzmaUtil.uncompress(buffer);
        } catch (IOException ex) {
            L.log(Level.SEVERE, "Couldn't uncompress lump " + this, ex);
        }

        setCompressed(false);
    }

    protected void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
