package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DStaticProp V6 variant for Bloody Good Time.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV6BGT extends DStaticPropV6 {

    public String targetname;

    @Override
    public int getSize() {
        return super.getSize() + DStaticPropV5Ship.TARGETNAME_LEN; // 192
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        targetname = in.readStringFixed(DStaticPropV5Ship.TARGETNAME_LEN);
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeStringFixed(targetname, DStaticPropV5Ship.TARGETNAME_LEN);
    }
}
