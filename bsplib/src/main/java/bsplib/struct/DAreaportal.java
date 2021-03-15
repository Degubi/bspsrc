package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * Areaportal data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DAreaportal implements DStruct {

    public short portalKey;
    public short otherportal;
    public short firstClipPortalVert;
    public short clipPortalVerts;
    public int planenum;

    @Override
    public int getSize() {
        return 12;
    }

    @Override
    public void read(DataReader in) throws IOException {
        portalKey = in.readShort();
        otherportal = in.readShort();
        firstClipPortalVert = in.readShort();
        clipPortalVerts = in.readShort();
        planenum = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeShort(portalKey);
        out.writeShort(otherportal);
        out.writeShort(firstClipPortalVert);
        out.writeShort(clipPortalVerts);
        out.writeInt(planenum);
    }
}
