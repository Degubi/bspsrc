package bspinfo.gui.models;

import bsplib.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import log.*;
import org.apache.commons.compress.archivers.zip.*;
import util.gui.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EmbeddedTableModel extends ListTableModel {

    private static final Logger L = LogUtils.getLogger();

    public EmbeddedTableModel() {
        super(3);
        columnNames = Arrays.asList(new String[]{"Name", "Size"});
        columnClasses = new Class[] {String.class, Long.class};
    }

    public EmbeddedTableModel(BspFile bspFile) {
        this();

        try (ZipFile zip = bspFile.getPakFile().getZipFile()) {
            Enumeration<ZipArchiveEntry> enumeration = zip.getEntries();
            while (enumeration.hasMoreElements()) {
                ZipArchiveEntry ze = enumeration.nextElement();
                addRow(Arrays.asList(ze.getName(), ze.getSize()));
            }
        } catch (IOException ex) {
            L.log(Level.WARNING, "Can't read pak");
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
