package bspinfo.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteSizeCellRenderer extends DefaultTableCellRenderer {

    private boolean si;

    public ByteSizeCellRenderer(boolean si) {
        this.si = si;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Long) {
            value = FileUtils.byteCountToDisplaySize((Long) value);
        } else if (value instanceof Integer) {
            value = FileUtils.byteCountToDisplaySize((Integer) value);
        }

        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        return c;
    }
}
