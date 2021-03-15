package bspinfo.components.table;

import bsplib.*;

public final class LumpTableModel extends ListTableModel {

    public LumpTableModel() {
        super(new Class[] { Integer.class, String.class, Integer.class, Integer.class, Integer.class },
              new String[]{ "ID", "Name", "Size", "Size usage", "Version" });
    }

    @SuppressWarnings("boxing")
    public LumpTableModel(BspFile bspFile) {
        this();

        var lumps = bspFile.getLumps();
        var lumpSize = 0F;

        for(var l : lumps) {
            lumpSize += l.getLength();
        }

        for(var l : lumps) {
            addRow(l.getIndex(), l.getName(), l.getLength(), Math.round(l.getLength() / lumpSize * 100f), l.getVersion());
        }
    }
}