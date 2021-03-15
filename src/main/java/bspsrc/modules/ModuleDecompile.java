package bspsrc.modules;

import bsplib.*;
import bspsrc.*;

/**
 * An extension of ReadingModule for modules that also output VMF data
 * via the VmfWriter.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class ModuleDecompile extends ModuleRead {

    protected final VmfWriter writer;

    public ModuleDecompile(BspFileReader reader, VmfWriter writer) {
        super(reader);
        this.writer = writer;
    }
}
