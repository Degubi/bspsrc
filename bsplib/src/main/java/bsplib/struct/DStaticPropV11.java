package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * V11 structure found in Black Mesa, xengine(cu5) branch and later releases
 * (introduced with the December 2017 Update.)
 *
 * Possibly found in recent Source 2013 games as well.
 */
public class DStaticPropV11 extends DStaticPropV11lite {

    // m_FlagsEx
    // Additional flags? Purpose and use unknown. Usually 0.
    public int flagsEx;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 80
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        flagsEx = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeInt(flagsEx);
   }
}
