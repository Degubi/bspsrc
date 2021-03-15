package bsplib.struct;

import bsplib.vector.*;
import io.*;
import java.io.*;

/**
 * Model data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DModel implements DStruct {

    public Vector3f mins, maxs;
    public Vector3f origin;
    public int headnode;    // the head node of the model's BSP tree
    public int fstface;
    public int numface;

    @Override
    public int getSize() {
        return 48;
    }

    @Override
    public void read(DataReader in) throws IOException {
        mins = Vector3f.read(in);
        maxs = Vector3f.read(in);
        origin = Vector3f.read(in);
        headnode = in.readInt();
        fstface = in.readInt();
        numface = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector3f.write(out, mins);
        Vector3f.write(out, maxs);
        Vector3f.write(out, origin);
        out.writeInt(headnode);
        out.writeInt(fstface);
        out.writeInt(numface);
    }
}
