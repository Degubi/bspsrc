package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * DOverlay variant for Dota 2.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOverlayDota2 extends DOverlay {

    private int unknown;

    @Override
    public int getSize() {
        return super.getSize() + 4;
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
