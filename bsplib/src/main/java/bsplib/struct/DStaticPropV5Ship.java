package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DStaticProp V5 variant for The Ship.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV5Ship extends DStaticPropV5 {

    public String targetname;

    public static final int TARGETNAME_LEN = 128;

    @Override
    public int getSize() {
        return super.getSize() + TARGETNAME_LEN; // 188
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        targetname = in.readStringFixed(TARGETNAME_LEN);
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeStringFixed(targetname, TARGETNAME_LEN);
    }
}
