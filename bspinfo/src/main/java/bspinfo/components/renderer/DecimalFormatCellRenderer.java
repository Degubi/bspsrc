package bspinfo.components.renderer;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

public final class DecimalFormatCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter;

    public DecimalFormatCellRenderer(DecimalFormat formatter) {
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if(value instanceof Number) {
            value = formatter.format(value);
        }

        var c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        c.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        return c;
    }
}