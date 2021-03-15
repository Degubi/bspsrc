package bsplib.io;

import bsplib.io.buffer.source.*;
import bsplib.io.util.*;
import java.io.*;
import java.math.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataWriter extends DataBridge {

    public DataWriter(BufferedSource buf) {
        super(buf);
    }

    public void writeStruct(Struct struct) throws IOException {
        struct.write(this);
    }

    public OutputStream stream() {
        return Channels.newOutputStream(new BufferedSourceChannel(buf));
    }

    ////////////////
    // DataOutput //
    ////////////////

    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    public void writeBytes(byte[] b, int off, int len) throws IOException {
        buf.requestWrite(len).put(b, off, len);
    }

    public void writeBuffer(ByteBuffer src) throws IOException {
        while (src.hasRemaining() && buf.write(src) > 0);
        if (src.hasRemaining()) {
            throw new EOFException();
        }
    }

    public void writeByte(byte b) throws IOException {
        buf.requestWrite(1).put(b);
    }

    public void writeBoolean(boolean v) throws IOException {
        buf.requestWrite(1).put((byte) (v ? 1 : 0));
    }

    public void writeShort(short v) throws IOException {
        buf.requestWrite(2).putShort(v);
    }

    public void writeChar(char v) throws IOException {
        buf.requestWrite(2).putChar(v);
    }

    public void writeInt(int v) throws IOException {
        buf.requestWrite(4).putInt(v);
    }

    public void writeLong(long v) throws IOException {
        buf.requestWrite(8).putLong(v);
    }

    public void writeFloat(float v) throws IOException {
        buf.requestWrite(4).putFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        buf.requestWrite(8).putDouble(v);
    }

    public void writeUnsignedByte(int v) throws IOException {
        writeByte((byte) (v & 0xff));
    }

    public void writeUnsignedShort(int v) throws IOException {
        writeShort((short) (v & 0xffff));
    }

    public void writeUnsignedInt(long v) throws IOException {
        writeInt((int) (v & 0xffffffffl));
    }

    public void writeUnsignedLong(BigInteger v) throws IOException {
        writeLong(v.longValue());
    }

    public void writeHalf(float f) throws IOException {
        int sval = HalfFloat.floatToIntBits(f);
        writeUnsignedShort(sval);
    }

    //////////////////
    // StringOutput //
    //////////////////

    public void writeStringNull(String str, Charset charset) throws IOException {
        writeStringFixed(str, charset);
        writeUnsignedByte(0);
    }

    public void writeStringNull(String str) throws IOException {
        writeStringNull(str, StandardCharsets.US_ASCII);
    }

    public void writeStringFixed(String str, int length, Charset charset) throws IOException {
        byte[] raw = str.getBytes(charset);
        writeBytes(raw);
        int padding = length - raw.length;
        for (int i = 0; i < padding; i++) {
            writeUnsignedByte(0);
        }
    }

    public void writeStringFixed(String str, int length) throws IOException {
        writeStringFixed(str, length, StandardCharsets.US_ASCII);
    }

    public void writeStringFixed(String str, Charset charset) throws IOException {
        writeBytes(str.getBytes(charset));
    }

    public void writeStringFixed(String str) throws IOException {
        writeStringFixed(str, StandardCharsets.US_ASCII);
    }

    public void writeStringPrefixed(String str, Class<? extends Number> prefixType, Charset charset) throws IOException {
        int len = str.length();
        if (prefixType == Byte.TYPE) {
            writeUnsignedByte(len);
        } else if (prefixType == Short.TYPE) {
            writeUnsignedShort(len);
        } else if (prefixType == Integer.TYPE) {
            writeUnsignedInt(len);
        } else {
            throw new IllegalArgumentException("Wrong prefix data type");
        }

        writeStringFixed(str, charset);
    }

    public void writeStringPrefixed(String str, Class<? extends Number> prefixType) throws IOException {
        writeStringPrefixed(str, prefixType, StandardCharsets.US_ASCII);
    }

    @Override
    public void align(int align) throws IOException {
        // avoid positioning and write null bytes, since it's pretty slow to
        // flush the buffer every time after correcting the position
        long pos = position();
        long rem = pos % align;
        if (rem != 0) {
            int pad = (int) (align - rem);
            while (pad > 0) {
                int padWrite = Math.min(4096, pad);
                writeBytes(new byte[padWrite]);
                pad -= padWrite;
            }
        }
    }
}
