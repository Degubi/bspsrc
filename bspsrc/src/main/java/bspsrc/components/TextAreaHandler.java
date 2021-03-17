package bspsrc.components;

import java.util.logging.*;
import javax.swing.*;

public final class TextAreaHandler extends Handler {
    private final JTextArea out;
    private final JTextArea err;
    private boolean headerPrinted;

    public TextAreaHandler(JTextArea out, JTextArea err) {
        this.out = out;
        this.err = err;
    }

    private void doHeaders() {
        if(!headerPrinted) {
            var head = getFormatter().getHead(this);
            out.append(head);
            err.append(head);
            headerPrinted = true;
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
            out.setCaretPosition(out.getDocument().getLength());
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {
        doHeaders();
    }
}