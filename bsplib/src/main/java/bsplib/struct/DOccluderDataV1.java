package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOccluderDataV1 extends DOccluderData {

    public int area;

    @Override
    public int getSize() {
        return super.getSize() + 4;
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        area = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(area);
    }
}
