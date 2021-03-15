package bsplib.struct;

import io.*;

/**
 * Generic interface for classes that emulate C/C++ structures.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface DStruct extends Struct {

    public int getSize();
}
