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
public class DataReader extends DataBridge {

    private static final BigInteger TWO_COMPL_REF = BigInteger.ONE.shiftLeft(Long.SIZE);

    public DataReader(BufferedSource buf) {
        super(buf);
    }

    public void readStruct(Struct struct) throws IOException {
        struct.read(this);
    }

    public InputStream stream() {
        return Channels.newInputStream(new BufferedSourceChannel(buf));
    }

    ///////////////
    // DataInput //
    ///////////////

    public void readBytes(byte[] b, int off, int len) throws IOException {
        buf.requestRead(len).get(b, off, len);
    }

    public void readBuffer(ByteBuffer dst) throws IOException {
        while (dst.hasRemaining() && buf.read(dst) > 0);
        if (dst.hasRemaining()) {
            throw new EOFException();
        }
    }

    public boolean readBoolean() throws IOException {
        return buf.requestRead(1).get() != 0;
    }

    public byte readByte() throws IOException {
        return buf.requestRead(1).get();
    }

    public short readShort() throws IOException {
        return buf.requestRead(2).getShort();
    }

    public char readChar() throws IOException {
        return buf.requestRead(2).getChar();
    }

    public int readInt() throws IOException {
        return buf.requestRead(4).getInt();
    }

    public long readLong() throws IOException {
        return buf.requestRead(8).getLong();
    }

    public float readFloat() throws IOException {
        return buf.requestRead(4).getFloat();
    }

    public double readDouble() throws IOException {
        return buf.requestRead(8).getDouble();
    }

    public void readBytes(byte[] b) throws IOException {
        readBytes(b, 0, b.length);
    }

    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }

    public BigInteger readUnsignedLong() throws IOException {
        BigInteger v = BigInteger.valueOf(readLong());

        // convert to unsigned
        if (v.compareTo(BigInteger.ZERO) < 0) {
            v = v.add(TWO_COMPL_REF);
        }

        return v;
    }

    public float readHalf() throws IOException {
        int hbits = readUnsignedShort();
        return HalfFloat.intBitsToFloat(hbits);
    }

    /////////////////
    // StringInput //
    /////////////////

    public String readStringFixed(int length, Charset charset) throws IOException {
        // read raw string including padding
        byte[] raw = new byte[length];
        readBytes(raw);

        // find offset to the first null byte, which is also the length of the
        // string
        length = 0;
        while (length < raw.length && raw[length] != 0) {
            length++;
        }

        return new String(raw, 0, length, charset);
    }

    public String readStringFixed(int length) throws IOException {
        return readStringFixed(length, StandardCharsets.US_ASCII);
    }

    public String readStringNull(int limit, Charset charset) throws IOException {
        // read bytes until the first null byte
        byte[] raw = new byte[limit];
        int length = 0;
        while (length < raw.length && (raw[length] = readByte()) != 0) {
            length++;
        }

        return new String(raw, 0, length, charset);
    }

    public String readStringNull(int limit) throws IOException {
        return readStringNull(limit, StandardCharsets.US_ASCII);
    }

    public String readStringNull() throws IOException {
        return readStringNull(256);
    }

    public <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit, Charset charset) throws IOException {
        Number length;
        if (prefixType == Byte.TYPE) {
            length = readUnsignedByte();
        } else if (prefixType == Short.TYPE) {
            length = readUnsignedShort();
        } else if (prefixType == Integer.TYPE) {
            length = readUnsignedInt();
        } else {
            throw new IllegalArgumentException("Wrong prefix data type");
        }

        final int len = length.intValue();
        if (len == 0) {
            return "";
        } else {
            byte[] raw = new byte[len];
            readBytes(raw);
            return new String(raw, 0, len, charset);
        }
    }

    public <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit) throws IOException {
        return readStringPrefixed(prefixType, limit, StandardCharsets.US_ASCII);
    }
}
