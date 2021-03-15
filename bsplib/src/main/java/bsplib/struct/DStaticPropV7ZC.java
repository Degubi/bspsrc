package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DStaticProp V7 variant for Zeno Clash.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV7ZC extends DStaticPropV6 {

    protected int unknown;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 68
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        unknown = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeInt(unknown);
    }
}
