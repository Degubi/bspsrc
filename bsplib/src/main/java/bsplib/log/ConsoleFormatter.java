package bsplib.log;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * Log formatter for console output.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ConsoleFormatter extends Formatter {

    private static final Map<Level, String> LEVEL_PREFIX;

    static {
        Map<Level, String> levelPrefix = new HashMap<>();
        levelPrefix.put(Level.CONFIG,  "[config]");
        levelPrefix.put(Level.FINE,    "[debug]");
        levelPrefix.put(Level.FINER,   "[debug]");
        levelPrefix.put(Level.FINEST,  "[trace]");
        levelPrefix.put(Level.INFO,    "[info]");
        levelPrefix.put(Level.SEVERE,  "[error]");
        levelPrefix.put(Level.WARNING, "[warning]");

        LEVEL_PREFIX = Collections.unmodifiableMap(levelPrefix);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(LEVEL_PREFIX.get(record.getLevel()));
        sb.append(' ');

        String[] classNameParts = record.getLoggerName().split("\\.");

        // add class name for non-info records
        if (record.getLevel() != Level.INFO && classNameParts.length != 0) {
            sb.append(classNameParts[classNameParts.length - 1]);
            sb.append(": ");
        }

        sb.append(formatMessage(record));

        // print stack trace if given
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            sb.append(", caused by ");
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                thrown.printStackTrace(pw);
            }
            sb.append(sw.toString());
        }

        sb.append("\n");

        return sb.toString();
    }
}
