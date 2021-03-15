package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * Cubemap data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DCubemapSample implements DStruct {

    public int[] origin = new int[3];
    public byte size;

    @Override
    public int getSize() {
        return 16;
    }

    @Override
    public void read(DataReader in) throws IOException {
        origin[0] = in.readInt();
        origin[1] = in.readInt();
        origin[2] = in.readInt();
        size = (byte) in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(origin[0]);
        out.writeInt(origin[1]);
        out.writeInt(origin[2]);
        out.writeInt(size);
    }
}
