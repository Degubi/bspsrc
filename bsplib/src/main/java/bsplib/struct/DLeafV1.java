package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DLeafV1 extends DLeaf  {

    @Override
    public int getSize() {
        return super.getSize() + 2;
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        in.readUnsignedShort(); // padding
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeUnsignedShort(0); // padding
    }
}
