package bspsrc.modules.texture;

import bsplib.struct.*;

/**
 * A simple texture data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class Texture {

    private DTexData data;
    private String texture;
    private String textureOverride;
    private TextureAxis u = new TextureAxis(1, 0, 0);
    private TextureAxis v = new TextureAxis(0, 1, 0);
    private int lmscale = 16;

    public TextureAxis getUAxis() {
        return u;
    }

    public void setUAxis(TextureAxis u) {
        this.u = u;
    }

    public TextureAxis getVAxis() {
        return v;
    }

    public void setVAxis(TextureAxis v) {
        this.v = v;
    }

    public String getTexture() {
        return textureOverride != null ? textureOverride : texture;
    }

    public String getOriginalTexture() {
        return texture;
    }

    public void setOriginalTexture(String texture) {
        this.texture = texture;
    }

    public String getOverrideTexture() {
        return textureOverride;
    }

    public void setOverrideTexture(String texture) {
        this.textureOverride = texture;
    }

    public int getLightmapScale() {
        return lmscale;
    }

    public void setLightmapScale(int lmscale) {
        this.lmscale = lmscale;
    }

    public DTexData getData() {
        return data;
    }

    public void setData(DTexData texdata) {
        this.data = texdata;
    }
}
