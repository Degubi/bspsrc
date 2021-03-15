package bspinfo.log;

import java.awt.*;
import java.util.logging.*;
import javax.swing.*;

public final class DialogHandler extends Handler {

    private final Component parentComponent;

    public DialogHandler(Component parentComponent) {
        this.parentComponent = parentComponent;
        setFormatter(new DialogFormatter());
        setLevel(Level.WARNING);
    }

    @Override
    public void publish(LogRecord record) {
        if(record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        int dialogType;
        String title;
        String message;

         try {
            message = getFormatter().format(record);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

        if (record.getLevel() == Level.WARNING) {
            dialogType = JOptionPane.WARNING_MESSAGE;
            title = "Warning";
        } else if (record.getLevel() == Level.SEVERE) {
            dialogType = JOptionPane.ERROR_MESSAGE;
            title = "Error";
        } else {
            dialogType = JOptionPane.INFORMATION_MESSAGE;
            title = "Information";
        }

        JOptionPane.showMessageDialog(parentComponent, message, title, dialogType);
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}