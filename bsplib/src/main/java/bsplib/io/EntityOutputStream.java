package bsplib.io;

import bsplib.entity.*;
import java.io.*;
import java.util.*;
import java.util.Map.*;

/**
 * Enity stream writing class. Converts Entity objects text into keyvalue text.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityOutputStream extends PrintStream {

    public EntityOutputStream(OutputStream out) {
        super(out);
    }

    public void writeEntity(Entity ent) {
        print("{\n");

        for (Entry<String, String> kv : ent.getEntrySet()) {
            printf("\"%s\" \"%s\"\n", kv.getKey(), kv.getValue());
        }

        printf("\"classname\" \"%s\"\n", ent.getClassName());

        List<KeyValue> ios = ent.getIO();

        for (KeyValue io : ios) {
            printf("\"%s\" \"%s\"\n", io.getKey(), io.getValue());
        }

        print("}\n");
    }
}