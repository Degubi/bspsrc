package bspsrc;

import bspsrc.cli.*;
import bspsrc.gui.*;

/**
 * Simple launcher that starts the CLI if there's a console available or the GUI
 * otherwise.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspSourceLauncher {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        if (System.console() == null) {
            BspSourceFrame.main(args);
        } else {
            BspSourceCli.main(args);
        }
    }
}
