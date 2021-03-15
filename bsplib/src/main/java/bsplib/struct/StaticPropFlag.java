package bsplib.struct;

/**
 * Enumeration for static prop flags.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum StaticPropFlag {

    STATIC_PROP_FLAG_FADES,             // 0x1
    STATIC_PROP_USE_LIGHTING_ORIGIN,    // 0x2
    STATIC_PROP_NO_DRAW,                // 0x4
    STATIC_PROP_IGNORE_NORMALS,         // 0x8
    STATIC_PROP_NO_SHADOW,              // 0x10
    STATIC_PROP_SCREEN_SPACE_FADE,      // 0x20
    STATIC_PROP_NO_PER_VERTEX_LIGHTING, // 0x40
    STATIC_PROP_NO_SELF_SHADOWING,      // 0x80
    STATIC_PROP_NO_PER_TEXEL_LIGHTING;  // 0x100
}
