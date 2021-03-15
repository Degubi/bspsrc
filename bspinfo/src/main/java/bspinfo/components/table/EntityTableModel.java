package bspinfo.components.table;

import bsplib.*;
import bsplib.entity.*;
import java.util.*;

public final class EntityTableModel extends ListTableModel {

    public EntityTableModel() {
        super(new Class[] { String.class, Integer.class }, new String[]{ "Class", "Entities" });
    }

    @SuppressWarnings("boxing")
    public EntityTableModel(BspFileReader bspReader) {
        this();

        var entities = bspReader.getData().entities;
        var classes = bspReader.getEntityClassSet();
        var entityStrings = new ArrayList<String>();

        for (Entity ent : entities) {
            entityStrings.add(ent.getClassName());
        }

        for(var cls : classes) {
            addRow(cls, Collections.frequency(entityStrings, cls));
        }
    }
}