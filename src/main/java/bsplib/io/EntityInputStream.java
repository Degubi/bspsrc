package bsplib.io;

import bsplib.entity.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import log.*;
import org.apache.commons.io.input.*;

/**
 * Enity stream reading class. Converts keyvalue text into Entity objects.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityInputStream extends CountingInputStream {

    private static final Logger L = LogUtils.getLogger();
    private boolean allowEsc = false;

    public EntityInputStream(InputStream in) {
        super(in);
    }

    public Entity readEntity() throws IOException {
        boolean section = false;
        boolean string = false;
        boolean esc = false;
        StringBuilder sb = new StringBuilder(512);

        List<KeyValue> keyValues = new ArrayList<>();
        String key = null;

        try {
            for (int b = 0; b != -1; b = read()) {
                switch (b) {
                    case '"':
                        if (!section) {
                            throw new ParseException("String in unopened section", getCount());
                        }

                        // ignore '"' if the previous character was '\'
                        if (esc) {
                            esc = false;
                            break;
                        }

                        // parse strings
                        if (string) {
                            if (key == null) {
                                key = sb.toString();
                            } else {
                                String value = sb.toString();

                                // ignore empty keys
                                if (key.isEmpty()) {
                                    L.log(Level.FINE, "Skipped value \"{0}\" with empty key at {1}",
                                            new Object[] {value, getCount()});
                                } else {
                                    keyValues.add(new KeyValue(key, value));
                                }

                                key = null;
                            }

                            // empty string buffer
                            sb.delete(0, sb.length());
                        }

                        string = !string;
                        continue;

                    case '{':
                        if (section && !string) {
                            throw new ParseException("Opened unclosed section", getCount());
                        }

                        if (!string) {
                            section = true;
                        }
                        break;

                    case '}':
                        if (!section && !string) {
                            throw new ParseException("Closed unopened section", getCount());
                        }

                        if (!string) {
                            return new Entity(keyValues);
                        }
                        break;

                    case '\\':
                        if (allowEsc) {
                            // skip this character and add the next '"' to the string
                            esc = true;
                        }
                        break;
                }

                // append to current string if inside section
                if (section && string) {
                    sb.append((char) b);
                }
            }
        } catch (ParseException ex) {
            L.log(Level.WARNING, "{0} at {1}", new Object[]{ex.getMessage(), ex.getErrorOffset()});

            // skip rest of this section by reading until EOF or '}'
            for (int b = 0; b != -1 && b != '}'; b = read());

            // return what we've got so far
            return new Entity(keyValues);
        }

        return null;
    }

    public boolean isAllowEscSeq() {
        return allowEsc;
    }

    public void setAllowEscSeq(boolean allowEsc) {
        this.allowEsc = allowEsc;
    }
}
