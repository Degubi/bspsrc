package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 * Old V10 structure found in CS:GO.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV10CSGO extends DStaticPropV9 {

    protected int unknown;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 76
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

    /**
     * @return Always {@code false}, because DStaticPropV10CSGO doesn't use ScreenSpaceFade anymore and its flag is now used for 'RenderInFastReflection'
     */
    @Override
    public boolean hasScreenSpaceFadeInPixels() {
        return false;
    }

    public boolean hasRenderInFastReflection() {
        return super.hasScreenSpaceFadeInPixels();
    }
}
