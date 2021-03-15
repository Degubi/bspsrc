package bspinfo.components.table;

import bspinfo.*;
import bsplib.*;
import java.io.*;
import java.util.logging.*;

public class EmbeddedTableModel extends ListTableModel {

    public EmbeddedTableModel() {
        super(new Class[] { String.class, Long.class }, new String[]{ "Name", "Size" });
    }

    @SuppressWarnings("boxing")
    public EmbeddedTableModel(BspFile bspFile) {
        this();

        try(var zip = bspFile.getPakFile().getZipFile()) {
            var enumeration = zip.getEntries();

            while (enumeration.hasMoreElements()) {
                var ze = enumeration.nextElement();
                addRow(ze.getName(), ze.getSize());
            }

        } catch (IOException ex) {
            Main.LOGGER.log(Level.WARNING, "Can't read pak");
        }
    }
}