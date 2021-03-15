package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOverlayFade implements DStruct {

    public float fadeDistMinSq;
    public float fadeDistMaxSq;

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void read(DataReader in) throws IOException {
        fadeDistMinSq = in.readFloat();
        fadeDistMaxSq = in.readFloat();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeFloat(fadeDistMinSq);
        out.writeFloat(fadeDistMaxSq);
    }
}
