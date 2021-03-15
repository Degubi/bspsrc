package util.gui;

import java.io.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FileExtensionFilter extends FileFilter {

    private final FileNameExtensionFilter parent;

    public FileExtensionFilter(String description, String... extensions) {
        parent = new FileNameExtensionFilter(description, extensions);
    }

    @Override
    public boolean accept(File f) {
        return parent.accept(f);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(parent.getDescription());

        String[] exts = parent.getExtensions();

        if (exts.length == 0) {
            return sb.toString();
        }

        sb.append(" (");

        for (String ext : exts) {
            sb.append("*.");
            sb.append(ext);
            sb.append(';');
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');

        return sb.toString();
    }
}
