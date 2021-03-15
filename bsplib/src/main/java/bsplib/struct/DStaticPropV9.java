package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV9 extends DStaticPropV8 {

    public boolean disableX360;
    public byte[] unknown = new byte[3];

    @Override
    public int getSize() {
        return super.getSize() + 4; // 72
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        disableX360 = in.readBoolean();
        in.readBytes(unknown); // non-zero garbage?
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeInt(disableX360 ? 1 : 0);
        out.writeBytes(unknown);
    }
}
