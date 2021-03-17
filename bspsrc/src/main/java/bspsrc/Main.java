package bspsrc;

import bsplib.*;
import bsplib.app.*;
import bsplib.decompile.*;
import bsplib.log.*;
import bsplib.modules.*;
import bsplib.nmo.*;
import bsplib.util.*;
import java.io.*;
import java.util.logging.*;

public final class Main {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String VERSION = "2.0.0";

    public static void main(String[] args) throws Exception {
        if(System.console() == null) {
            MainGUI.main(args);
        }else{
            MainCLI.main(args);
        }
    }

    @SuppressWarnings("boxing")
    public static void run(DecompileConfig config) {
        var startTime = System.currentTimeMillis();

        if(config.isDebug()) {
            config.dumpToLog();
        }

        var entries = config.getFileSet();
        if (entries.isEmpty()) {
            LOGGER.severe("No BSP files found");
        } else {
            for (var entry : entries) {
                try {
                    decompile(entry, config);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Decompiling error", ex);
                }
            }

            var duration = (System.currentTimeMillis() - startTime) / 1000.0;
            LOGGER.log(Level.INFO, "Processed {0} file(s) in {1} seconds", new Object[]{ entries.size(), String.format("%.4f", duration) });
        }
    }

    @SuppressWarnings("boxing")
    private static void decompile(BspFileEntry entry, DecompileConfig config) {
        File bspFile = entry.getBspFile();
        File vmfFile = entry.getVmfFile();

        // load BSP
        BspFileReader reader;

        LOGGER.log(Level.INFO, "Loading {0}", bspFile);

        try {
            BspFile bsp = new BspFile();
            bsp.setSourceApp(config.defaultApp);
            bsp.load(bspFile.toPath());

            if (config.loadLumpFiles) {
                bsp.loadLumpFiles();
            }

            // extract embedded files
            if (config.unpackEmbedded) {
                try {
                    bsp.getPakFile().unpack(
                            entry.getPakDir().toPath(),
                            fileName -> !config.smartUnpack || !PakFile.isVBSPGeneratedFile(bsp.getName(), fileName)
                    );
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Can't extract embedded files", ex);
                }
            }

            reader = new BspFileReader(bsp);
            reader.loadAll();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Can't load " + bspFile, ex);
            return;
        }

        // load NMO if game is 'No More Room in Hell'
        NmoFile nmo = null;
        if (reader.getBspFile().getSourceApp().appID == SourceAppID.NO_MORE_ROOM_IN_HELL) {
            var nmoFile = entry.getNmoFile();
            var nmosFile = entry.getNmosFile();

            if (nmoFile.exists()) {
                try {
                    nmo = new NmoFile();
                    nmo.load(nmoFile.toPath(), true);

                    // write nmos
                    try {
                        nmo.writeAsNmos(nmosFile.toPath());
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Error while writing nmos", ex);
                    }
                } catch (IOException | NmoException ex) {
                    LOGGER.log(Level.SEVERE, "Can't load " + nmoFile, ex);
                    nmo = null;
                }
            } else {
                LOGGER.warning("Missing .nmo file! If the bsp is for the objective game mode, its objectives will be missing");
            }
        }

        if (!config.isDebug()) {
            LOGGER.log(Level.INFO, "BSP version: {0}", reader.getBspFile().getVersion());
            LOGGER.log(Level.INFO, "Game: {0}", reader.getBspFile().getSourceApp());
        }

        // create and configure decompiler and start decompiling
        try (VmfWriter writer = getVmfWriter(vmfFile, config)) {
            BspDecompiler decompiler = new BspDecompiler(reader, writer, config);

            if (nmo != null) {
                decompiler.setNmoData(nmo);
            }

            decompiler.start();
            LOGGER.log(Level.INFO, "Finished decompiling {0}", bspFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Can't decompile " + bspFile + " to " + vmfFile, ex);
        }
    }

    @SuppressWarnings("resource")
    private static VmfWriter getVmfWriter(File vmfFile, DecompileConfig config) throws IOException {
        return config.nullOutput ? new VmfWriter(OutputStream.nullOutputStream()) : new VmfWriter(vmfFile);
    }
}