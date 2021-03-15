package bspinfo.log;

import java.util.logging.*;

public final class DialogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return record + ": " + record.getThrown().getMessage();
    }
}