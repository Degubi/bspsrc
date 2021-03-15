package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DStaticProp V6 variant for Dark Messiah.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV6DM extends DStaticPropV6 {

    protected byte[] unknown = new byte[72];

    @Override
    public int getSize() {
        return super.getSize() + 72; // 136
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        in.readBytes(unknown);
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeBytes(unknown);
    }
}
