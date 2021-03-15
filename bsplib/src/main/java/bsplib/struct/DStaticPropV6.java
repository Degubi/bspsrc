package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV6 extends DStaticPropV5 {

    public int minDXLevel;
    public int maxDXLevel;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 64
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        minDXLevel = in.readUnsignedShort();
        maxDXLevel = in.readUnsignedShort();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeUnsignedShort(minDXLevel);
        out.writeUnsignedShort(maxDXLevel);
    }
}
