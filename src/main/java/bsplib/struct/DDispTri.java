package bsplib.struct;

import io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DDispTri implements DStruct {

    public int tags;

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public void read(DataReader in) throws IOException {
        tags = in.readUnsignedShort();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedShort(tags);
    }
}
