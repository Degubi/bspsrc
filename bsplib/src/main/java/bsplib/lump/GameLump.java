package bsplib.lump;

import bsplib.util.*;
import org.apache.commons.io.*;

/**
 * Lump extension for game lumps that are stored inside LUMP_GAME_LUMP.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class GameLump extends AbstractLump {

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public String getName() {
        return StringMacroUtils.unmakeID(EndianUtils.swapInteger(getFourCC()));
    }

    @Override
    public void setCompressed(boolean compressed) {
        super.setCompressed(compressed);
        setFlags(compressed ? 1 : 0);
    }
}
