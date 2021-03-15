package bsplib.struct;

import io.*;
import java.io.*;

/**
 * DLeaf structure variant used in Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DLeafVin extends DLeaf {

    @Override
    public int getSize() {
        return 56;
    }

    @Override
    public void read(DataReader in) throws IOException {
        contents = in.readInt();
        cluster = (short) in.readInt();
        areaFlags = (short) in.readInt();
        mins[0] = (short) in.readInt();
        mins[1] = (short) in.readInt();
        mins[2] = (short) in.readInt();
        maxs[0] = (short) in.readInt();
        maxs[1] = (short) in.readInt();
        maxs[2] = (short) in.readInt();
        fstleafface = in.readInt();
        numleafface = in.readInt();
        fstleafbrush = in.readInt();
        numleafbrush = in.readInt();
        leafWaterDataID = (short) in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(contents);
        out.writeInt(cluster);
        out.writeInt(areaFlags);
        out.writeInt(mins[0]);
        out.writeInt(mins[1]);
        out.writeInt(mins[2]);
        out.writeInt(maxs[0]);
        out.writeInt(maxs[1]);
        out.writeInt(maxs[2]);
        out.writeInt(fstleafface);
        out.writeInt(numleafface);
        out.writeInt(fstleafbrush);
        out.writeInt(numleafbrush);
        out.writeInt(leafWaterDataID);
    }
}
