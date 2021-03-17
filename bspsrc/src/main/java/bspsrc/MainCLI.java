package bspsrc;

import bsplib.app.*;
import bsplib.decompile.*;
import bsplib.log.*;
import bsplib.modules.geom.*;
import bsplib.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.cli.*;

public final class MainCLI {
    private static final Logger LOGGER = Logger.getLogger("BSPSource CLI");

    public static void main(String[] args) {
        LogUtils.configure();

        if(args.length == 0) {
            printHelp();
            return;
        }

        var options = new Options();
        addOptions(options, options, options, options, options, options);

        var commandLine = parseCommandLine(options, args);
        if(commandLine != null) {
            if(commandLine.hasOption(helpOpt.getOpt())) {
                printHelp();
                return;
            }

            if(commandLine.hasOption(versionOpt.getOpt())) {
                System.out.println("BSPSource " + Main.VERSION + '\n' +
                                   "Based on VMEX v0.98g by Rof <rof@mellish.org.uk>\n" +
                                   "Extended and modified by Nico Bergemann <barracuda415@yahoo.de>");
                return;
            }

            if(commandLine.hasOption(listappidsOpt.getOpt())) {
                System.out.printf("%6s  %s\n", "ID", "Name");

                for(var app : SourceAppDB.getInstance().getAppList()) {
                    System.out.printf("%6d  %s\n", app.appID, app.name);
                }
                return;
            }

            var config = getConfig(commandLine);
            if(config.getFileSet().isEmpty()) {
                LOGGER.severe("No BSP file(s) specified");
            }else{
                Main.run(config);
            }
        }
    }

    private static CommandLine parseCommandLine(Options options, String[] args) {
        try {
            return new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            LOGGER.severe("Unable to parse commandline options! Error details:\n" + e.getMessage());
            return null;
        }
    }

    private static void printHelp() {
        System.out.println("BSPSource " + Main.VERSION + '\n' +
                           "usage: bspsrc [options] <path> [path...]\n");

        var helpFormatter = new HelpFormatter();
        var writer = new PrintWriter(System.out);

        var mainOptions = new Options();
        var entityOptions = new Options();
        var entityMappingOptions = new Options();
        var worldOptions = new Options();
        var textureOptions = new Options();
        var otherOptions = new Options();
        addOptions(mainOptions, entityOptions, entityMappingOptions, worldOptions, textureOptions, otherOptions);

        printOptions("Main options:", mainOptions, helpFormatter, writer);
        printOptions("Entity options:", entityOptions, helpFormatter, writer);
        printOptions("Entity mapping options:", entityMappingOptions, helpFormatter, writer);
        printOptions("World brush options:", worldOptions, helpFormatter, writer);
        printOptions("Texture options:", textureOptions, helpFormatter, writer);
        printOptions("Other options:", otherOptions, helpFormatter, writer);

        writer.flush();
    }

    private static void printOptions(String header, Options options, HelpFormatter formatter, PrintWriter writer) {
        formatter.printWrapped(writer, HelpFormatter.DEFAULT_WIDTH, header);
        formatter.printOptions(writer, HelpFormatter.DEFAULT_WIDTH, options, formatter.getLeftPadding(), formatter.getDescPadding());
        formatter.printWrapped(writer, HelpFormatter.DEFAULT_WIDTH, "");
    }

    private static void addOptions(Options mainOpts, Options entityOpts, Options entityMappingOpts, Options worldOpts, Options textureOpts, Options otherOpts) {
        mainOpts.addOption(helpOpt);
        mainOpts.addOption(versionOpt);
        mainOpts.addOption(debugOpt);
        mainOpts.addOption(recursiveOpt);
        mainOpts.addOption(outputOpt);
        mainOpts.addOption(fileListOpt);
        entityOpts.addOption(npentsOpt);
        entityOpts.addOption(nbentsOpt);
        entityOpts.addOption(npropsOpt);
        entityOpts.addOption(noverlOpt);
        entityOpts.addOption(ncubemOpt);
        entityOpts.addOption(ndetailsOpt);
        entityOpts.addOption(nareapOpt);
        entityOpts.addOption(nocclOpt);
        entityOpts.addOption(nladderOpt);
        entityOpts.addOption(nrotfixOpt);
        entityMappingOpts.addOption(fAreapManualOpt);
        entityMappingOpts.addOption(fOcclManualOpt);
        worldOpts.addOption(nbrushOpt);
        worldOpts.addOption(ndispOpt);
        worldOpts.addOption(bmodeOpt);
        worldOpts.addOption(thicknOpt);
        textureOpts.addOption(ntexfixOpt);
        textureOpts.addOption(ntooltexfixOpt);
        textureOpts.addOption(ftexOpt);
        textureOpts.addOption(bftexOpt);
        otherOpts.addOption(nvmfOpt);
        otherOpts.addOption(nlumpfilesOpt);
        otherOpts.addOption(nprotOpt);
        otherOpts.addOption(listappidsOpt);
        otherOpts.addOption(nvisgrpOpt);
        otherOpts.addOption(ncamsOpt);
        otherOpts.addOption(appidOpt);
        otherOpts.addOption(formatOpt);
        otherOpts.addOption(unpackOpt);
        otherOpts.addOption(nsmartUnpackOpt);
    }


    private static DecompileConfig getConfig(CommandLine cl) {
        var config = new DecompileConfig();
        var files = new HashSet<BspFileEntry>();
        var outputFile = cl.hasOption(outputOpt.getOpt()) ? new File(cl.getOptionValue(outputOpt.getOpt())) : null;
        var optionalFileList = cl.getOptionValue(fileListOpt.getOpt());

        if(optionalFileList != null) {
            try(var lines = Files.lines(Path.of(optionalFileList))) {
                lines.map(File::new)
                     .map(filePath -> new BspFileEntry(filePath, outputFile))
                     .forEach(files::add);
            } catch (IOException e) {}
        }

        config.setDebug(cl.hasOption(debugOpt.getOpt()));
        config.writePointEntities = !cl.hasOption(npentsOpt.getOpt());
        config.writeBrushEntities = !cl.hasOption(nbentsOpt.getOpt());
        config.writeStaticProps = !cl.hasOption(npropsOpt.getOpt());
        config.writeOverlays = !cl.hasOption(noverlOpt.getOpt());
        config.writeDisp = !cl.hasOption(ndispOpt.getOpt());
        config.writeAreaportals = !cl.hasOption(nareapOpt.getOpt());
        config.writeOccluders = !cl.hasOption(nocclOpt.getOpt());
        config.writeCubemaps = !cl.hasOption(ncubemOpt.getOpt());
        config.writeDetails = !cl.hasOption(ndetailsOpt.getOpt());
        config.writeLadders = !cl.hasOption(nladderOpt.getOpt());
        config.apForceManualMapping = cl.hasOption(fAreapManualOpt.getOpt());
        config.occForceManualMapping = cl.hasOption(fOcclManualOpt.getOpt());
        config.writeWorldBrushes = !cl.hasOption(nbrushOpt.getOpt());

        if(cl.hasOption(bmodeOpt.getOpt())) {
            var modeStr = cl.getOptionValue(bmodeOpt.getOpt());

            config.brushMode = parseEnum(BrushMode.class, modeStr)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid brush mode: " + modeStr));
        }

        if(cl.hasOption(formatOpt.getOpt())) {
            var formatStr = cl.getOptionValue(formatOpt.getOpt());

            config.sourceFormat = parseEnum(SourceFormat.class, formatStr)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid source format: " + formatStr));
        }

        if(cl.hasOption(thicknOpt.getOpt())) {
            var thicknessStr = cl.getOptionValue(thicknOpt.getOpt());

            try {
                config.backfaceDepth = Float.parseFloat(thicknessStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid thickness: " + thicknessStr);
            }
        }

        config.fixCubemapTextures = !cl.hasOption(ntexfixOpt.getOpt());
        config.fixToolTextures = !cl.hasOption(ntooltexfixOpt.getOpt());

        if(cl.hasOption(ftexOpt.getOpt())) {
            config.faceTexture = cl.getOptionValue(ftexOpt.getOpt());
        }

        if(cl.hasOption(bftexOpt.getOpt())) {
            config.backfaceTexture = cl.getOptionValue(bftexOpt.getOpt());
        }

        config.loadLumpFiles = !cl.hasOption(nlumpfilesOpt.getOpt());
        config.skipProt = cl.hasOption(nprotOpt.getOpt());
        config.fixEntityRot = !cl.hasOption(nrotfixOpt.getOpt());
        config.nullOutput = cl.hasOption(nvmfOpt.getOpt());
        config.writeVisgroups = !cl.hasOption(nvisgrpOpt.getOpt());
        config.writeCameras = !cl.hasOption(ncamsOpt.getOpt());
        config.unpackEmbedded = cl.hasOption(unpackOpt.getOpt());
        config.smartUnpack = !cl.hasOption(nsmartUnpackOpt.getOpt());

        if(cl.hasOption(appidOpt.getOpt())) {
            var appidStr = cl.getOptionValue(appidOpt.getOpt()).toUpperCase();

            try {
                config.defaultApp = SourceAppDB.getInstance().fromID(Integer.parseInt(appidStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid App-ID: " + appidStr);
            }
        }

        var argsLeft = cl.getArgs();
        var recursive = cl.hasOption(recursiveOpt.getOpt());

        for(var arg : argsLeft) {
            var path = Path.of(arg);

            if(Files.isDirectory(path)) {
                var bspPathMatcher = path.getFileSystem().getPathMatcher("blob:*.bsp");

                try(var pathStream = Files.walk(path, recursive ? Integer.MAX_VALUE : 0)) {
                    pathStream
                            .filter(filePath -> Files.isRegularFile(filePath))
                            .filter(bspPathMatcher::matches)
                            .map(filePath -> new BspFileEntry(filePath.toFile(), outputFile))
                            .forEach(files::add);
                } catch (IOException e) {}
            }else{
                files.add(new BspFileEntry(path.toFile(), outputFile));
            }
        }

        config.addFiles(files);

        return config;
    }

    private static <E extends Enum<E>> Optional<E> parseEnum(Class<E> eClass, String value) {
        try {
            return Optional.of(Enum.valueOf(eClass, value));
        } catch (IllegalArgumentException e) {
            try {
                return Optional.of(eClass.getEnumConstants()[Integer.parseInt(value)]);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e1) {}
        }

        return Optional.empty();
    }


    private static final Option helpOpt = new Option("help", "Print this help.");
    private static final Option versionOpt = new Option("version", "Print version info.");
    private static final Option listappidsOpt = new Option("appids", "List all available application IDs");
    private static final Option debugOpt = new Option("debug", "Enable debug mode. Increases verbosity and adds additional data to the VMF file.");
    private static final Option outputOpt = Option.builder("output").hasArg().argName("file")
                                                  .desc("Override output path for VMF file(s). Treated as directory if multiple BSP files are provided. \ndefault: <mappath>/<mapname>_d.vmf")
                                                  .build();

    private static final Option recursiveOpt = new Option("recursive", "Decompile all files found in the given directory.");
    private static final Option fileListOpt = Option.builder("list").hasArg().argName("file")
                                                    .desc("Use a text files with paths as input BSP file list.")
                                                    .build();

    private static final Option nbentsOpt = new Option("no_brush_ents", "Don't write any brush entities.");
    private static final Option npentsOpt = new Option("no_point_ents", "Don't write any point entities.");
    private static final Option npropsOpt = new Option("no_sprp", "Don't write prop_static entities.");
    private static final Option noverlOpt = new Option("no_overlays", "Don't write info_overlay entities.");
    private static final Option ncubemOpt = new Option("no_cubemaps", "Don't write env_cubemap entities.");
    private static final Option ndetailsOpt = new Option("no_details", "Don't write func_detail entities.");
    private static final Option nareapOpt = new Option("no_areaportals", "Don't write func_areaportal(_window) entities.");
    private static final Option nocclOpt = new Option("no_occluders", "Don't write func_occluder entities.");
    private static final Option nladderOpt = new Option("no_ladders", "Don't write func_ladder entities.");
    private static final Option nrotfixOpt = new Option("no_rotfix", "Don't fix instance entity brush rotations for Hammer.");

    private static final Option fAreapManualOpt = new Option("force_manual_areaportal", "Force manual entity mapping for areaportal entities");
    private static final Option fOcclManualOpt = new Option("force_manual_occluder", "Force manual entitiy mapping for occluder entities");

    private static final Option nbrushOpt = new Option("no_brushes", "Don't write any world brushes.");
    private static final Option ndispOpt = new Option("no_disps", "Don't write displacement surfaces.");
    private static final Option bmodeOpt = Option.builder("brushmode").hasArg().argName("enum")
                                                 .desc("Brush decompiling mode:\n" +
                                                       BrushMode.BRUSHPLANES.name() + "   - brushes and planes\n" +
                                                       BrushMode.ORIGFACE.name() + "      - original faces only\n" +
                                                       BrushMode.ORIGFACE_PLUS.name() + " - original + split faces\n" +
                                                       BrushMode.SPLITFACE.name() + "     - split faces only\n" +
                                                       "default: " + BrushMode.BRUSHPLANES.name())
                                                 .build();

    private static final Option thicknOpt = Option.builder("thickness").hasArg().argName("float")
                                                  .desc("Thickness of brushes created from flat faces in units.\ndefault: 1")
                                                  .build();

    private static final Option ntexfixOpt = new Option("no_texfix", "Don't fix texture names.");
    private static final Option ntooltexfixOpt = new Option("no_tooltexfix", "Don't fix tool textures.");
    private static final Option ftexOpt = Option.builder("facetex").hasArg().argName("string")
                                                .desc("Replace all face textures with this one.")
                                                .build();
    private static final Option bftexOpt = Option.builder("bfacetex").hasArg().argName("string")
                                                 .desc("Replace all back-face textures with this one. Used in face-based decompiling modes only.")
                                                 .build();

    private static final Option nvmfOpt = new Option("no_vmf", "Don't write any VMF files, read BSP only.");
    private static final Option nlumpfilesOpt = new Option("no_lumpfiles", "Don't load lump files (.lmp) associated with the BSP file.");
    private static final Option nprotOpt = new Option("no_prot", "Skip decompiling protection checking. Can increase speed when mass-decompiling unprotected maps.");
    private static final Option appidOpt = Option.builder("appid").hasArg().argName("string/int")
                                                 .desc("Overrides game detection by using " +
                                                       "this Steam Application ID instead.\n" +
                                                       "Use -appids to list all known app-IDs.")
                                                 .build();

    private static final Option nvisgrpOpt = new Option("no_visgroups", "Don't group entities from instances into visgroups.");
    private static final Option ncamsOpt = new Option("no_cams", "Don't create Hammer cameras above each player spawn.");
    private static final Option formatOpt = Option.builder("format").hasArg().argName("enum")
                                                  .desc("Sets the VMF format used for the decompiled maps:\n" +
                                                        SourceFormat.AUTO.name() + " - " + SourceFormat.AUTO + "\n" +
                                                        SourceFormat.OLD.name() + "  - " + SourceFormat.OLD + "\n" +
                                                        SourceFormat.NEW.name() + "  - " + SourceFormat.NEW + "\n" +
                                                        "default: " + SourceFormat.AUTO.name())
                                                  .build();

    private static final Option unpackOpt = new Option("unpack_embedded", "Unpack embedded files in the bsp.");
    private static final Option nsmartUnpackOpt = new Option("no_smart_unpack", "Disable 'smart' extracting of embedded files.\n Smart extracting automatically skips all files generated by vbsp, that are only relevant to running the map in the engine.");
}