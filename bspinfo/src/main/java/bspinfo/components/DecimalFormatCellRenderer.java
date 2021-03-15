package bspinfo.components;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DecimalFormatCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter;

    public DecimalFormatCellRenderer(DecimalFormat formatter) {
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col) {
        if (value instanceof Number) {
            value = formatter.format((Number) value);
        }

        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, col);

        c.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        return c;
    }
}
