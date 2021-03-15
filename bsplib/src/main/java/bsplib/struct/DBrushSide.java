package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * Brush side data stucture.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DBrushSide implements DStruct {

    public int pnum;
    public short texinfo;
    public short dispinfo;
    public boolean bevel;

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void read(DataReader in) throws IOException {
        pnum = in.readUnsignedShort();
        texinfo = in.readShort();
        dispinfo = in.readShort();
        bevel = in.readShort() == 1;
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedShort(pnum);
        out.writeShort(texinfo);
        out.writeShort(dispinfo);
        out.writeUnsignedShort(bevel ? 1 : 0);
    }
}
