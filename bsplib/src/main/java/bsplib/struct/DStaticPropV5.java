package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV5 extends DStaticPropV4 {

    public float forcedFadeScale;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 60
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        forcedFadeScale = in.readFloat();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeFloat(forcedFadeScale);
    }
}
