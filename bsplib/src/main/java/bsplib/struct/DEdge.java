package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DEdge implements DStruct {

    public int[] v = new int[2]; // vertex numbers

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public void read(DataReader in) throws IOException {
        v[0] = in.readUnsignedShort();
        v[1] = in.readUnsignedShort();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedShort(v[0]);
        out.writeUnsignedShort(v[1]);
    }
}
