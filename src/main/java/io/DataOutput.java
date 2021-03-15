package io;

import java.io.*;
import java.math.*;
import java.nio.*;

/**
 * Custom DataOutput interface with better handling for unsigned data types and
 * configurable byte order.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface DataOutput extends Swappable {

    public void writeBytes(byte b[]) throws IOException;

    public void writeBytes(byte b[], int off, int len) throws IOException;

    public void writeBuffer(ByteBuffer buf) throws IOException;

    public void writeByte(byte b) throws IOException;

    public void writeUnsignedByte(int b) throws IOException;

    public void writeBoolean(boolean b) throws IOException;

    public void writeChar(char c) throws IOException;

    public void writeShort(short s) throws IOException;

    public void writeUnsignedShort(int s) throws IOException;

    public void writeInt(int i) throws IOException;

    public void writeUnsignedInt(long i) throws IOException;

    public void writeLong(long l) throws IOException;

    public void writeUnsignedLong(BigInteger l) throws IOException;

    /**
     * Writes a float as half-precision 16 bit floating-point number according to
     * IEEE 754-2008.
     *
     * @param f float value
     * @throws IOException
     */
    public void writeHalf(float h) throws IOException;

    public void writeFloat(float f) throws IOException;

    public void writeDouble(double d) throws IOException;
}
