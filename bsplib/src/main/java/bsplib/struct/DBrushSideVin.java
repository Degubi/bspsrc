package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DBrushSide variant for Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DBrushSideVin extends DBrushSide {

    @Override
    public int getSize() {
        return 16;
    }

    @Override
    public void read(DataReader in) throws IOException {
        pnum = in.readInt();
        texinfo = (short) in.readInt();
        dispinfo = (short) in.readInt();
        bevel = (short) in.readInt() == 1;
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(pnum);
        out.writeInt(texinfo);
        out.writeInt(dispinfo);
        out.writeInt(bevel ? 1 : 0);
    }
}
