package bspsrc.cli;

import org.apache.commons.cli.*;

/**
 * Quick hack to group multiple Options.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MultiOptions extends Options {

    public MultiOptions addOptions(Options options) {
        if (options != null) {
            options.getOptions().forEach(this::addOption);
        }

        return this;
    }
}
