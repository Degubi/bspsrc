package bsplib.struct;

import bsplib.io.*;
import bsplib.vector.*;
import java.io.*;

/**
 * DModel structure variant for Dark Messiah (single player only)
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DModelDM extends DModel {

    protected int unknown;

    @Override
    public int getSize() {
        return 52;
    }

    @Override
    public void read(DataReader in) throws IOException {
        mins = Vector3f.read(in);
        maxs = Vector3f.read(in);
        origin = Vector3f.read(in);
        unknown = in.readInt();
        headnode = in.readInt();
        fstface = in.readInt();
        numface = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector3f.write(out, mins);
        Vector3f.write(out, maxs);
        Vector3f.write(out, origin);
        out.writeInt(unknown);
        out.writeInt(headnode);
        out.writeInt(fstface);
        out.writeInt(numface);
    }
}
