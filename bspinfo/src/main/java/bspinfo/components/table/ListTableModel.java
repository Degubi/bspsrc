package bspinfo.components.table;

import java.util.*;
import javax.swing.table.*;

abstract class ListTableModel extends AbstractTableModel {

    private final List<Object[]> modelData = new ArrayList<>();
    private final Class<?>[] columnClasses;
    private final String[] columnNames;

    public ListTableModel(Class<?>[] columnClasses, String[] columnNames) {
        this.columnClasses = columnClasses;
        this.columnNames = columnNames;

        fireTableStructureChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return modelData.get(row)[column];
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        modelData.get(row)[column] = value;
        fireTableCellUpdated(row, column);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column < columnClasses.length ? columnClasses[column] : super.getColumnClass(column);
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return modelData.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        Object columnName = null;

        if (column < columnNames.length) {
            columnName = columnNames[column];
        }

        return (columnName == null) ? super.getColumnName(column) : columnName.toString();
    }

    public void addRow(Object... rowData) {
        var row = getRowCount();

        modelData.add(row, rowData);
        fireTableRowsInserted(row, row);
    }
}