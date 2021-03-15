package io;

import java.io.*;

/**
 * Interface for classes that can be serialized to DataWriter and
 * deserialized from DataReader.
 *
 * Typically used to emulate simple C/C++ structs.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Struct {

    /**
     * Deserializes this object from a DataReader.
     *
     * @param in data source
     * @throws IOException if an I/O error occurs
     */
    public void read(DataReader in) throws IOException;

    /**
     * Serializes this object to a DataWriter.
     *
     * @param out data destination
     * @throws IOException if an I/O error occurs
     */
    public void write(DataWriter out) throws IOException;

}
