package bspsrc.gui;

import java.util.logging.*;
import javax.swing.*;

/**
 * Logging handler for two JTextAreas, one for normal messages and one for errors.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TextAreaHandler extends Handler {

    private JTextArea out;
    private JTextArea err;
    private boolean doneHeader;

    public TextAreaHandler(JTextArea out, JTextArea err) {
        this.out = out;
        this.err = err;
    }

    private void doHeaders() {
        if (!doneHeader) {
            String head = getFormatter().getHead(this);
            out.append(head);
            err.append(head);
            doneHeader = true;
        }
    }

    @Override
    public void publish(LogRecord record) {
        String msg;
        try {
            msg = getFormatter().format(record);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

        try {
            doHeaders();
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                err.append(msg);
            }

            out.append(msg);
            // make sure the last line is always visible
            out.setCaretPosition(out.getDocument().getLength());
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void flush() {
        // not required
    }

    @Override
    public void close() throws SecurityException {
        doHeaders();
    }
}
