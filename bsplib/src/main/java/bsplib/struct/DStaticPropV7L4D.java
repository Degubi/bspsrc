package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DStaticProp V7 variant for some old Left 4 Dead maps.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV7L4D extends DStaticPropV6 {

    public Color32 diffuseModulation;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 68
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        diffuseModulation = new Color32(in.readInt());
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeInt(diffuseModulation.rgba);
    }
}
