package io.file;

import static java.nio.file.StandardOpenOption.*;

import io.*;
import java.io.*;
import java.nio.file.*;

/**
 * Basic handler that can read and write files and also read from byte buffers.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class FileHandler {

    protected Path sourceFile;

    public void load(Path file) throws IOException {
        sourceFile = file;
        load(DataReaders.forFile(file, READ));
    }

    public abstract void load(DataReader in) throws IOException;

    public void save(Path file) throws IOException {
        sourceFile = file;
        save(DataWriters.forFile(file, WRITE, CREATE, TRUNCATE_EXISTING));
    }

    public abstract void save(DataWriter out) throws IOException;

    public Path getSourceFile() {
        return sourceFile;
    }
}
