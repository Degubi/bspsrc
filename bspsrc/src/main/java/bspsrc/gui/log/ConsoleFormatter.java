package bspsrc.gui.log;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public final class ConsoleFormatter extends Formatter {
    private static final Map<Level, String> LEVEL_PREFIX = Map.of(Level.CONFIG,  "[config]",
                                                                  Level.FINE,    "[debug]",
                                                                  Level.FINER,   "[debug]",
                                                                  Level.FINEST,  "[trace]",
                                                                  Level.INFO,    "[info]",
                                                                  Level.SEVERE,  "[error]",
                                                                  Level.WARNING, "[warning]");
    @Override
    public String format(LogRecord record) {
        var sb = new StringBuilder();
        sb.append(LEVEL_PREFIX.get(record.getLevel()));
        sb.append(' ');

        var classNameParts = record.getLoggerName().split("\\.");
        if (record.getLevel() != Level.INFO && classNameParts.length != 0) {
            sb.append(classNameParts[classNameParts.length - 1]);
            sb.append(": ");
        }

        sb.append(formatMessage(record));

        var thrown = record.getThrown();
        if (thrown != null) {
            sb.append(", caused by ");

            var sw = new StringWriter();
            try(var pw = new PrintWriter(sw)) {
                thrown.printStackTrace(pw);
            }
            sb.append(sw.toString());
        }

        sb.append('\n');

        return sb.toString();
    }
}