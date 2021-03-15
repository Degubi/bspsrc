package bspinfo.gui;

import bsplib.*;
import bsplib.lump.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import log.*;
import org.apache.commons.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspFileUtils {

    private static final Logger L = LogUtils.getLogger();

    private BspFileUtils() {
    }

    static void extractLump(BspFile bspFile, File destDir, LumpType type) throws IOException {
        FileUtils.forceMkdir(destDir);

        List<Lump> lumps = bspFile.getLumps();

        for (Lump lump : lumps) {
            if (type != null && lump.getType() != type) {
                continue;
            }

            String fileName = String.format("%02d_%s.bin", lump.getIndex(),
                    lump.getName());
            File lumpFile = new File(destDir, fileName);

            L.log(Level.INFO, "Extracting {0}", lump);

            try {
                InputStream is = lump.getInputStream();
                FileUtils.copyInputStreamToFile(is, lumpFile);
            } catch (IOException ex) {
                throw new BspFileException("Can't extract lump", ex);
            }
        }
    }

    public static void extractLumps(BspFile bspFile, File destDir) throws IOException {
        extractLump(bspFile, destDir, null);
    }

    static void extractGameLump(BspFile bspFile, File destDir, String type) throws IOException {
        FileUtils.forceMkdir(destDir);

        List<GameLump> gameLumps = bspFile.getGameLumps();

        for (GameLump gameLump : gameLumps) {
            if (type != null && !gameLump.getName().equalsIgnoreCase(type)) {
                continue;
            }

            String fileName = String.format("%s_v%d.bin", gameLump.getName(), gameLump.getVersion());
            File lumpFile = new File(destDir, fileName);

            L.log(Level.INFO, "Extracting {0}", gameLump);

            try {
                InputStream is = gameLump.getInputStream();
                FileUtils.copyInputStreamToFile(is, lumpFile);
            } catch (IOException ex) {
                throw new BspFileException("Can't extract lump", ex);
            }
        }
    }

    public static void extractGameLumps(BspFile bspFile, File destDir) throws IOException {
        extractGameLump(bspFile, destDir, null);
    }
}
