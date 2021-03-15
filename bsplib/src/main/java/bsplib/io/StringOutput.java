package bsplib.io;

import java.io.*;
import java.nio.charset.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface StringOutput {

    void writeStringFixed(String str, int length, Charset charset) throws IOException;

    void writeStringFixed(String str, int length) throws IOException;

    void writeStringFixed(String str, Charset charset) throws IOException;

    void writeStringFixed(String str) throws IOException;

    void writeStringNull(String str, Charset charset) throws IOException;

    void writeStringNull(String str) throws IOException;

    void writeStringPrefixed(String str, Class<? extends Number> prefixType, Charset charset) throws IOException;

    void writeStringPrefixed(String str, Class<? extends Number> prefixType) throws IOException;

}
