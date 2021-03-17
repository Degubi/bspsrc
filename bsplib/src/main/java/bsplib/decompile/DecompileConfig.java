package bsplib.decompile;

import bsplib.app.*;
import bsplib.log.*;
import bsplib.modules.geom.*;
import bsplib.util.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

public final class DecompileConfig {

    // logger
    private static final Logger L = LogUtils.getLogger();

    public SourceApp defaultApp = SourceApp.UNKNOWN;
    public BrushMode brushMode = BrushMode.BRUSHPLANES;
    public SourceFormat sourceFormat = SourceFormat.AUTO;
    public String backfaceTexture = "";
    public String faceTexture = "";
    public boolean fixCubemapTextures = true;
    public boolean fixEntityRot = true;
    public boolean fixToolTextures = true;
    public boolean loadLumpFiles = true;
    public boolean nullOutput = false;
    public boolean skipProt = false;
    public boolean unpackEmbedded = false;
    public boolean smartUnpack = true;
    public float backfaceDepth = 1;
    public int maxCubemapSides = 8;
    public int maxOverlaySides = 64;
    public boolean detailMerge = true;
    public float detailMergeThresh = 1;
    public boolean apForceManualMapping = false;
    public boolean occForceManualMapping = false;
    public boolean writeAreaportals = true;
    public boolean writeBrushEntities = true;
    public boolean writeCameras = true;
    public boolean writeCubemaps = true;
    public boolean writeDetails = true;
    public boolean writeDisp = true;
    public boolean writeOccluders = true;
    public boolean writeOverlays = true;
    public boolean writePointEntities = true;
    public boolean writeStaticProps = true;
    public boolean writeVisgroups = true;
    public boolean writeWorldBrushes = true;
    public boolean writeLadders = true;

    private boolean debug = false;
    private Set<BspFileEntry> files = new HashSet<>();

    public DecompileConfig() {

    }

    private void updateLogger(boolean debug) {
        LogUtils.configure(debug ? Level.ALL : Level.INFO);
        if (debug) {
            L.fine("Debug mode on, verbosity set to maximum");
        }
    }

    public void dumpToLog() {
        dumpToLog(L);
    }

    public void dumpToLog(Logger logger) {
        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            // ignore static fields
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                logger.log(Level.CONFIG, "{0} = {1}", new Object[]{field.getName(), field.get(this)});
            } catch (Exception ex) {
                continue;
            }
        }
    }

    public void setFileSet(Set<BspFileEntry> entries) {
        this.files = new HashSet<>(entries);
    }

    /**
     * @return an unmodifiable set of all BspFileEntry objects
     */
    public Set<BspFileEntry> getFileSet() {
        return Collections.unmodifiableSet(files);
    }

    public void addFiles(Set<BspFileEntry> files) {
        this.files.addAll(files);
    }

    public boolean isWriteEntities() {
        return writeBrushEntities || writePointEntities;
    }

    public void setWriteEntities(boolean writeEntities) {
        writeBrushEntities = writePointEntities = writeEntities;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        updateLogger(debug);
        this.debug = debug;
    }
}