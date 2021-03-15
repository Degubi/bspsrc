package bspinfo.components.table;

import bsplib.*;

public final class GameLumpTableModel extends ListTableModel {

    public GameLumpTableModel() {
        super(new Class[] { String.class, Integer.class, Integer.class, Integer.class },
              new String[]{ "Name", "Size", "Size usage", "Version" });
    }

    @SuppressWarnings("boxing")
    public GameLumpTableModel(BspFile bspFile) {
        this();

        var lumps = bspFile.getGameLumps();
        var lumpSize = 0F;

        for(var l : lumps) {
            lumpSize += l.getLength();
        }

        for(var l : lumps) {
            addRow(l.getName(), l.getLength(), Math.round(l.getLength() / lumpSize * 100f), l.getVersion());
        }
    }
}