package bspsrc.components;

import bsplib.modules.texture.*;

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
