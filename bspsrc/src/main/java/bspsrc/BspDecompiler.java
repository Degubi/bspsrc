package bspsrc;

import bsplib.*;
import bsplib.app.*;
import bsplib.decompile.*;
import bsplib.log.*;
import bsplib.modules.*;
import bsplib.modules.entity.*;
import bsplib.modules.geom.*;
import bsplib.modules.texture.*;
import bsplib.nmo.*;
import bsplib.util.*;
import java.util.logging.*;

public class BspDecompiler extends ModuleDecompile {

    // logger
    private static final Logger L = LogUtils.getLogger();

    // sub-modules
    private final DecompileConfig config;
    private final TextureSource texsrc;
    private final BrushSource brushsrc;
    private final FaceSource facesrc;
    private final EntitySource entsrc;
    private final BspProtection bspprot;
    private final VmfMeta vmfmeta;

    public BspDecompiler(BspFileReader reader, VmfWriter writer, DecompileConfig config) {
        super(reader, writer);

        WindingFactory.clearCache();

        this.config = config;

        texsrc = new TextureSource(reader);
        bspprot = new BspProtection(reader, texsrc);
        vmfmeta = new VmfMeta(reader, writer);
        brushsrc = new BrushSource(reader, writer, config, texsrc, bspprot, vmfmeta);
        facesrc = new FaceSource(reader, writer, config, texsrc, vmfmeta);
        entsrc = new EntitySource(reader, writer, config, brushsrc, facesrc, texsrc, bspprot, vmfmeta);
    }

    /**
     * Starts the decompiling process
     */
    public void start() {
        // fix texture names
        texsrc.setFixTextureNames(config.fixCubemapTextures);

        // VTBM has too many crucial game-specific tool textures that would break,
        // so override the user selection
        if (bspFile.getSourceApp().appID == SourceAppID.VAMPIRE_BLOODLINES) {
            texsrc.setFixToolTextures(false);
        } else {
            texsrc.setFixToolTextures(config.fixToolTextures);
        }

        // check for protection and warn if the map has been protected
        if (!config.skipProt && bspprot.check()) {
            L.log(Level.WARNING, "{0} contains anti-decompiling flags or is obfuscated!", reader.getBspFile().getName());
            L.log(Level.WARNING, "Detected methods:");

            for(var method : bspprot.getProtectionMethods()) {
                L.warning(method);
            }
        }

        // set comment
        vmfmeta.appendComment("Decompiled by BSPSource v" + Main.VERSION + " from " + bspFile.getName());

        // start worldspawn
        vmfmeta.writeWorldHeader();

        // write brushes and displacements
        if (config.writeWorldBrushes) {
            writeBrushes();
        }

        // end worldspawn
        vmfmeta.writeWorldFooter();

        // write entities
        if (config.isWriteEntities()) {
            writeEntities();
        }

        // write visgroups
        if (config.writeVisgroups) {
            vmfmeta.writeVisgroups();
        }

        // write cameras
        if (config.writeCameras) {
            vmfmeta.writeCameras();
        }
    }

    private void writeBrushes() {
        switch (config.brushMode) {
            case BRUSHPLANES:
                brushsrc.writeBrushes();
                break;

            case ORIGFACE:
                facesrc.writeOrigFaces();
                break;

            case ORIGFACE_PLUS:
                facesrc.writeOrigFacesPlus();
                break;

            case SPLITFACE:
                facesrc.writeFaces();
                break;

            default:
                break;
        }

        // add faces with displacements
        // face modes don't need to do this separately
        if (config.brushMode == BrushMode.BRUSHPLANES) {
            facesrc.writeDispFaces();
        }
    }

    private void writeEntities() {
        if (config.isWriteEntities()) {
            entsrc.writeEntities();
        }

        if (config.writeBrushEntities && config.writeDetails && config.brushMode == BrushMode.BRUSHPLANES) {
            entsrc.writeDetails();
        }

        if (config.writePointEntities) {
            if (config.writeOverlays) {
                entsrc.writeOverlays();
            }

            if (config.writeStaticProps) {
                entsrc.writeStaticProps();
            }

            if (config.writeCubemaps) {
                entsrc.writeCubemaps();
            }

            // Only write func_ladder if game is not csgo. Csgo doesn't use the func_ladder entity
            if (config.writeLadders && bspFile.getSourceApp().appID != SourceAppID.COUNTER_STRIKE_GO) {
                entsrc.writeLadders();
            }
        }
    }

    /**
     * @see EntitySource#setNmo(NmoFile)
     */
    public void setNmoData(NmoFile nmo) {
        entsrc.setNmo(nmo);
    }
}