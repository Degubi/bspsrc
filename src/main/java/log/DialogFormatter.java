package log;

import java.util.logging.*;

/**
 * Log formatter for dialog messages.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DialogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatMessage(record));
        sb.append(": ");
        sb.append(record.getThrown().getMessage());
        return sb.toString();
    }

}
