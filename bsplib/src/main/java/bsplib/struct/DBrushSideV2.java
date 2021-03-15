package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DBrushSideV2 extends DBrushSide {

    public boolean thin;

    @Override
    public void read(DataReader in) throws IOException {
        pnum = in.readUnsignedShort();
        texinfo = in.readShort();
        dispinfo = in.readShort();
        bevel = in.readBoolean();
        thin = in.readBoolean();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedShort(pnum);
        out.writeShort(texinfo);
        out.writeShort(dispinfo);
        out.writeBoolean(bevel);
        out.writeBoolean(thin);
    }
}
