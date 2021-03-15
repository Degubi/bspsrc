package bsplib.modules.geom;

import bsplib.struct.*;
import bsplib.util.*;

/**
 * Brush utility class.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BrushUtils {

    private BrushUtils() {
    }

    /**
     * Returns the bounding box of a brush by combining the bounding boxes of all
     * its brush sides.
     *
     * @param bsp bsp data
     * @param brush a brush
     * @return the bounding box of the brush
     */
    public static AABB getBounds(BspData bsp, DBrush brush) {
        // add bounds of all brush sides
        AABB bounds = new AABB();
        for (int i = 0; i < brush.numside; i++) {
            bounds = bounds.include(WindingFactory.fromSide(bsp, brush, i).getBounds());
        }
        return bounds;
    }
}
