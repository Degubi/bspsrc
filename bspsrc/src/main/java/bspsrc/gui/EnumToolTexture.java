package bspsrc.gui;

import bsplib.modules.texture.*;

/**
 * Enumeration for some tool textures.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum EnumToolTexture {

    DEFAULT("Default", ""),
    WHITE("White", ToolTexture.WHITE),
    BLACK("Black", ToolTexture.BLACK),
    NODRAW("Nodraw", ToolTexture.NODRAW),
    ORANGE("Orange", ToolTexture.ORANGE),
    SKIP("Skip", ToolTexture.SKIP);

    public final String texName;
    public final String texPath;

    private EnumToolTexture(String texName, String texPath) {
        this.texName = texName;
        this.texPath = texPath;
    }

    @Override
    public String toString() {
        return texName;
    }
}
