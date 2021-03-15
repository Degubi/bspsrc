package bspsrc;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.*;

/**
 * Simple file filter for BSP files (.bsp)
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspFileFilter extends SuffixFileFilter {

    public BspFileFilter() {
        super(".bsp", IOCase.INSENSITIVE);
    }
}
