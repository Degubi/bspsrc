package bspinfo.gui.models;

import bsplib.*;
import bsplib.lump.*;
import java.util.*;
import util.gui.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class GameLumpTableModel extends ListTableModel {

    public GameLumpTableModel() {
        super(4);
        columnNames = Arrays.asList(new String[]{ "Name", "Size", "Size usage", "Version"});
        columnClasses = new Class[] {String.class, Integer.class, Integer.class, Integer.class};
    }

    public GameLumpTableModel(BspFile bspFile) {
        this();

        List<GameLump> lumps = bspFile.getGameLumps();

        float lumpSize = 0;

        for (GameLump l : lumps) {
            lumpSize += l.getLength();
        }

        for (GameLump l : lumps) {
            List<Object> row = new ArrayList<>();
            row.add(l.getName());
            row.add(l.getLength());
            row.add(Math.round(l.getLength() / lumpSize * 100f));
            row.add(l.getVersion());
            addRow(row);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
