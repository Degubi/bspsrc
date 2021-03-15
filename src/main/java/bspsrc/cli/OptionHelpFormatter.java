package bspsrc.cli;

import java.io.*;
import org.apache.commons.cli.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class OptionHelpFormatter extends HelpFormatter {

    public void printHelp(PrintWriter pw, int width,
            String header, Options options, int leftPad,
            int descPad, String footer) {
        if ((header != null) && (header.trim().length() > 0)) {
            printWrapped(pw, width, header);
        }

        printOptions(pw, width, options, leftPad, descPad);

        if ((footer != null) && (footer.trim().length() > 0)) {
            printWrapped(pw, width, footer);
        }
        printWrapped(pw, width, "");
    }

    public void printHelp(String header, Options options, String footer) {
        PrintWriter pw = new PrintWriter(System.out);

        printHelp(pw, getWidth(), header, options, getLeftPadding(), getDescPadding(), footer);
        pw.flush();
    }

    @Override
    public void printHelp(String header, Options options) {
        printHelp(header, options, null);
    }
}
