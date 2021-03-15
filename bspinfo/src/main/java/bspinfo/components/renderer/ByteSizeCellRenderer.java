package bspinfo.components.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.io.*;

public final class ByteSizeCellRenderer extends DefaultTableCellRenderer {

    @SuppressWarnings("boxing")
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Long) {
            value = FileUtils.byteCountToDisplaySize((Long) value);
        } else if (value instanceof Integer) {
            value = FileUtils.byteCountToDisplaySize((Integer) value);
        }

        var c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        return c;
    }
}