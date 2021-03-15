package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DEdge variant for Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DEdgeVin extends DEdge {

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void read(DataReader in) throws IOException {
        v[0] = in.readInt();
        v[1] = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(v[0]);
        out.writeInt(v[1]);
    }
}
