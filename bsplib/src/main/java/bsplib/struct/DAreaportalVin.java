package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DAreaportal variant for Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DAreaportalVin extends DAreaportal {

    @Override
    public int getSize() {
        return 20;
    }

    @Override
    public void read(DataReader in) throws IOException {
        portalKey = (short) in.readInt();
        otherportal = (short) in.readInt();
        firstClipPortalVert = (short) in.readInt();
        clipPortalVerts = (short) in.readInt();
        planenum = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(portalKey);
        out.writeInt(otherportal);
        out.writeInt(firstClipPortalVert);
        out.writeInt(clipPortalVerts);
        out.writeInt(planenum);
    }
}
