package bsplib.modules;

import bsplib.*;
import bsplib.struct.*;

/**
 * Basic abstract class for all modules that are reading BSP files with the
 * BspFileReader.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class ModuleRead {

    protected final BspFileReader reader;
    protected final BspData bsp;
    protected final BspFile bspFile;

    public ModuleRead(BspFileReader reader) {
        this.reader = reader;
        this.bsp = reader.getData();
        this.bspFile = reader.getBspFile();
    }
}
