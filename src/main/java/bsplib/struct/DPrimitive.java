package bsplib.struct;

import io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DPrimitive implements DStruct {

    public int type;
    public int firstIndex;
    public int indexCount;
    public int firstVert;
    public int vertCount;

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public void read(DataReader in) throws IOException {
        type = in.readUnsignedShort();
        firstIndex = in.readUnsignedShort();
        indexCount = in.readUnsignedShort();
        firstVert = in.readUnsignedShort();
        vertCount = in.readUnsignedShort();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedShort(type);
        out.writeUnsignedShort(firstIndex);
        out.writeUnsignedShort(indexCount);
        out.writeUnsignedShort(firstVert);
        out.writeUnsignedShort(vertCount);
    }
}
