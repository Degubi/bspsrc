package bsplib.modules.geom;

import bsplib.util.*;

/**
 * Enumeration for brush modes.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum BrushMode {

    BRUSHPLANES("Brushes and planes"),
    ORIGFACE("Original faces"),
    ORIGFACE_PLUS("Original plus split faces"),
    SPLITFACE("Split faces");

    private final String name;

    BrushMode(String name) {
        this.name = name;
    }

    public static BrushMode fromOrdinal(int index) {
        return EnumConverter.fromOrdinal(BrushMode.class, index);
    }

    @Override
    public String toString() {
        return name;
    }
}
