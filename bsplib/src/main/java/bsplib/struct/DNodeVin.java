package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DNode variant for Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DNodeVin extends DNode {

    @Override
    public int getSize() {
        return 48;
    }

    @Override
    public void read(DataReader in) throws IOException {
        planenum = in.readInt();
        children[0] = in.readInt();
        children[1] = in.readInt();
        mins[0] = (short) in.readInt();
        mins[1] = (short) in.readInt();
        mins[2] = (short) in.readInt();
        maxs[0] = (short) in.readInt();
        maxs[1] = (short) in.readInt();
        maxs[2] = (short) in.readInt();
        fstface = in.readInt();
        numface = in.readInt();
        in.readInt(); // paddding
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(planenum);
        out.writeInt(children[0]);
        out.writeInt(children[1]);
        out.writeInt(mins[0]);
        out.writeInt(mins[1]);
        out.writeInt(mins[2]);
        out.writeInt(maxs[0]);
        out.writeInt(maxs[1]);
        out.writeInt(maxs[2]);
        out.writeInt(fstface);
        out.writeInt(numface);
        out.writeInt(0); // paddding
    }
}
