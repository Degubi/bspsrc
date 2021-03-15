package bsplib.struct;

import io.*;
import java.io.*;

/**
 * Occluder polygon data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOccluderPolyData implements DStruct {

    public int firstvertexindex;
    public int vertexcount;
    public int planenum;

    @Override
    public int getSize() {
        return 12;
    }

    @Override
    public void read(DataReader in) throws IOException {
        firstvertexindex = in.readInt();
        vertexcount = in.readInt();
        planenum = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(firstvertexindex);
        out.writeInt(vertexcount);
        out.writeInt(planenum);
    }
}
