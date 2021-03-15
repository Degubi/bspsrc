package bspinfo.components.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {

    public ProgressCellRenderer() {
        super(JProgressBar.HORIZONTAL);

        setBorderPainted(false);
        setStringPainted(true);
    }

    @SuppressWarnings("boxing")
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value instanceof Integer) {
            setBackground(table.getBackground());
            setValue((Integer) value);
        }

        return this;
    }
}