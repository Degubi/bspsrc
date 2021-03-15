package log;

import java.io.*;
import java.util.logging.*;

/**
 * Log configuration utility class.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class LogUtils {

    private LogUtils() {
    }

    public static Logger getLogger() {
        String className = new Throwable().getStackTrace()[1].getClassName();
        return Logger.getLogger(className);
    }

    public static void configure() {
        configure(Level.INFO);
    }

    public static void configure(Level level) {
        configure("info.ata4", level);
    }

    public static void configure(String pkg, Level level) {
        // build log properties file in memory and use
        // LogManager.getLogManager().readConfiguration() to load it.
        // This is the only reliable way I found to override the logging settings
        // at any given time without using JVM parameters.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        ps.printf("handlers = %s\n", ConsoleHandler.class.getName());
        ps.printf(".level = %s\n", Level.INFO);
        ps.printf("%s.level = %s\n", pkg, level.getName());

        try (InputStream is = new ByteArrayInputStream(baos.toByteArray())) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException ex) {
            // don't use the logging system here, maybe it went to hell!
           System.err.println("Can't read logger configuration: " + ex);

            try {
                LogManager.getLogManager().readConfiguration();
            } catch (IOException ex2) {
                // okay, this is just silly...
                System.err.println("Can't restore logger configuration: " + ex2);
            }
        }
    }
}
