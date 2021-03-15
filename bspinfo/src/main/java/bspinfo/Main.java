package bspinfo;

import bspinfo.components.*;
import bspinfo.components.renderer.*;
import bspinfo.components.table.*;
import bspinfo.log.*;
import bsplib.*;
import bsplib.entity.*;
import bsplib.log.*;
import bsplib.lump.*;
import bsplib.modules.*;
import bsplib.modules.texture.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.*;

public final class Main {
    public static final Logger LOGGER = Logger.getLogger("BSPInfo");
    private static final String TITLE = "BSPInfo 2.0.0";

    private static File currentFile;
    private static BspFile bspFile;
    private static final JFrame frame = new JFrame(TITLE);
    private static final ReadOnlyCheckBox checkBoxBSPProtect = new ReadOnlyCheckBox("BSPProtect");
    private static final ReadOnlyCheckBox checkBoxIIDObfs = new ReadOnlyCheckBox("Entity obfuscation");
    private static final ReadOnlyCheckBox checkBoxIIDTexHack = new ReadOnlyCheckBox("Nodraw texture hack");
    private static final ReadOnlyCheckBox checkBoxVmexBrush = new ReadOnlyCheckBox("Protector brush");
    private static final ReadOnlyCheckBox checkBoxVmexEntity = new ReadOnlyCheckBox("Entity flag");
    private static final ReadOnlyCheckBox checkBoxVmexTexture = new ReadOnlyCheckBox("Texture flag");
    private static final JButton extractAllEmbeddedButton = new JButton("Extract all");
    private static final JButton extractAllGameLumpsButton = new JButton("Extract all");
    private static final JButton extractAllLumpsButton = new JButton("Extract all");
    private static final JButton extractEmbeddedButton = new JButton("Extract");
    private static final JButton extractEmbeddedZipButton = new JButton("Extract Zip file");
    private static final JButton extractGameLumpButton = new JButton("Extract");
    private static final JButton extractLumpButton = new JButton("Extract");
    private static final URILabel linkLabelAppURL = new URILabel();
    private static final JTable tableEmbedded = new JTable();
    private static final JTable tableEntities = new JTable();
    private static final JTable tableGameLumps = new JTable();
    private static final JTable tableLumps = new JTable();
    private static final JTextArea textAreaMaterials = new JTextArea();
    private static final JTextArea textAreaModels = new JTextArea();
    private static final JTextArea textAreaParticles = new JTextArea();
    private static final JTextArea textAreaSoundScripts = new JTextArea();
    private static final JTextArea textAreaSounds = new JTextArea();
    private static final JTextArea textAreaSoundscapes = new JTextArea();
    private static final JTextField textFieldAppID = new JTextField();
    private static final JTextField textFieldBrushEnts = new JTextField();
    private static final JTextField textFieldComment = new JTextField();
    private static final JTextField textFieldCompressed = new JTextField();
    private static final JTextField textFieldEndian = new JTextField();
    private static final JTextField textFieldFileCRC = new JTextField();
    private static final JTextField textFieldGame = new JTextField();
    private static final JTextField textFieldMapCRC = new JTextField();
    private static final JTextField textFieldName = new JTextField();
    private static final JTextField textFieldPointEnts = new JTextField();
    private static final JTextField textFieldRevision = new JTextField();
    private static final JTextField textFieldTotalEnts = new JTextField();
    private static final JTextField textFieldVbspParams = new JTextField();
    private static final JTextField textFieldVersion = new JTextField();
    private static final JTextField textFieldVradParams = new JTextField();
    private static final JTextField textFieldVvisParams = new JTextField();

    public static void main(String[] args) throws Exception {
        LogUtils.configure();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var menuFile = new JMenu("File");
        var openFileMenuItem = new JMenuItem("Open");
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openFileMenuItem.addActionListener(Main::openFileMenuItemActionPerformed);
        menuFile.add(openFileMenuItem);

        var menuBar = new JMenuBar();
        menuBar.add(menuFile);

        var tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", createGeneralsPanel());
        tabbedPane.addTab("Lumps", createLumpsPanel());
        tabbedPane.addTab("Game lumps", createGameLumpsPanel());
        tabbedPane.addTab("Entities", createEntitiesPanel());
        tabbedPane.addTab("Dependencies", createDependenciesPanel());
        tabbedPane.addTab("Embedded files", createEmbeddedPanel());
        tabbedPane.addTab("Protection", createProtectionPanel());

        SwingUtilities.updateComponentTreeUI(tabbedPane);

        if(args.length > 0) {
            loadFile(new File(args[0]));
        }

        frame.setContentPane(tabbedPane);
        frame.setJMenuBar(menuBar);
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Main.class.getResource("icon.png")));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setMinimumSize(new Dimension(600, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        LOGGER.addHandler(new DialogHandler(frame));
        FileDrop.attach(frame, files -> {
            if(files[0].getName().endsWith(".bsp")) {
                loadFile(files[0]);
            }});
    }

    private static void reset() {
        textFieldName.setText(null);
        textFieldVersion.setText(null);
        textFieldRevision.setText(null);
        textFieldCompressed.setText(null);
        textFieldEndian.setText(null);
        textFieldAppID.setText(null);
        textFieldGame.setText(null);
        linkLabelAppURL.setText(null);
        textFieldFileCRC.setText(null);
        textFieldMapCRC.setText(null);

        textFieldVbspParams.setText(null);
        textFieldVvisParams.setText(null);
        textFieldVradParams.setText(null);

        checkBoxVmexEntity.setSelected(false);
        checkBoxVmexTexture.setSelected(false);
        checkBoxVmexBrush.setSelected(false);

        checkBoxIIDObfs.setSelected(false);
        checkBoxIIDTexHack.setSelected(false);

        checkBoxBSPProtect.setSelected(false);

        tableLumps.setModel(new LumpTableModel());

        textFieldTotalEnts.setText(null);
        textFieldBrushEnts.setText(null);
        textFieldPointEnts.setText(null);

        tableEntities.setModel(new EntityTableModel());

        textAreaMaterials.setText(null);
        textAreaSounds.setText(null);
        textAreaSoundScripts.setText(null);
        textAreaSoundscapes.setText(null);
        textAreaModels.setText(null);
        textAreaParticles.setText(null);

        tableEmbedded.setModel(new EmbeddedTableModel());

        extractLumpButton.setEnabled(false);
        extractAllLumpsButton.setEnabled(false);

        extractGameLumpButton.setEnabled(false);
        extractAllGameLumpsButton.setEnabled(false);

        extractEmbeddedButton.setEnabled(false);
        extractAllEmbeddedButton.setEnabled(false);
        extractEmbeddedZipButton.setEnabled(false);
    }

    @SuppressWarnings("boxing")
    private static void loadFile(File file) {
        currentFile = file;

        frame.setTitle(TITLE + " - " + file.getName());
        reset();

        new Thread(() -> {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            try {
                bspFile = new BspFile();
                bspFile.load(currentFile.toPath());

                textFieldName.setText(bspFile.getName());
                textFieldVersion.setText(String.valueOf(bspFile.getVersion()));
                textFieldRevision.setText(String.valueOf(bspFile.getRevision()));
                textFieldCompressed.setText(bspFile.isCompressed() ? "Yes" : "No");
                textFieldEndian.setText(bspFile.getByteOrder() == ByteOrder.LITTLE_ENDIAN ? "Little endian" : "Big endian");

                var bspReader = new BspFileReader(bspFile);
                bspReader.loadEntities();

                var data = bspReader.getData();
                if (data.entities != null && !data.entities.isEmpty()) {
                    Entity worldspawn = data.entities.get(0);
                    textFieldComment.setText(worldspawn.getValue("comment"));
                }

                var app = bspFile.getSourceApp();

                textFieldAppID.setText(app.getAppID() > 0 ? String.valueOf(app.getAppID()) : "n/a");
                textFieldGame.setText(app.getName());

                var steamStoreURI = app.getSteamStoreURI();
                if (steamStoreURI != null) {
                    linkLabelAppURL.setURI("Steam store link", steamStoreURI);
                }

                var cparams = new BspCompileParams(bspReader);

                textFieldVbspParams.setText(StringUtils.join(cparams.getVbspParams(), ' '));

                if (cparams.isVvisRun()) {
                    textFieldVvisParams.setText(StringUtils.join(cparams.getVvisParams(), ' '));
                } else {
                    textFieldVvisParams.setText("(not run)");
                }

                if (cparams.isVradRun()) {
                    textFieldVradParams.setText(StringUtils.join(cparams.getVradParams(), ' '));
                } else {
                    textFieldVradParams.setText("(not run)");
                }

                var texsrc = new TextureSource(bspReader);
                var prot = new BspProtection(bspReader, texsrc);
                prot.check();

                checkBoxVmexEntity.setSelected(prot.hasEntityFlag());
                checkBoxVmexTexture.setSelected(prot.hasTextureFlag());
                checkBoxVmexBrush.setSelected(prot.hasBrushFlag());

                checkBoxIIDObfs.setSelected(prot.hasObfuscatedEntities());
                checkBoxIIDTexHack.setSelected(prot.hasModifiedTexinfo());

                checkBoxBSPProtect.setSelected(prot.hasEncryptedEntities());

                tableLumps.setModel(new LumpTableModel(bspFile));
                tableGameLumps.setModel(new GameLumpTableModel(bspFile));

                var brushEnts = 0;
                var pointEnts = 0;
                var entities = bspReader.getData().entities;

                for(var ent : entities) {
                    if (ent.getModelNum() > 0) {
                        brushEnts++;
                    } else {
                        pointEnts++;
                    }
                }

                var totalEnts = pointEnts + brushEnts;
                var df = new DecimalFormat("#,##0");

                textFieldTotalEnts.setText(df.format(totalEnts));
                textFieldBrushEnts.setText(df.format(brushEnts));
                textFieldPointEnts.setText(df.format(pointEnts));
                tableEntities.setModel(new EntityTableModel(bspReader));

                var bspres = new BspDependencies(bspReader);

                textAreaMaterials.append(String.join("\n", bspres.getMaterials()));
                textAreaSounds.append(String.join("\n", bspres.getSoundFiles()));
                textAreaSoundScripts.append(String.join("\n", bspres.getSoundScripts()));
                textAreaSoundscapes.append(String.join("\n", bspres.getSoundscapes()));
                textAreaModels.append(String.join("\n", bspres.getModels()));
                textAreaParticles.append(String.join("\n", bspres.getParticles()));

                tableEmbedded.setModel(new EmbeddedTableModel(bspFile));

                var checksum = new BspChecksum(bspReader);
                textFieldFileCRC.setText(String.format("%x", checksum.getFileCRC()));
                textFieldMapCRC.setText(String.format("%x", checksum.getMapCRC()));

                extractLumpButton.setEnabled(true);
                extractAllLumpsButton.setEnabled(true);

                extractGameLumpButton.setEnabled(true);
                extractAllGameLumpsButton.setEnabled(true);

                extractEmbeddedButton.setEnabled(true);
                extractAllEmbeddedButton.setEnabled(true);
                extractEmbeddedZipButton.setEnabled(true);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Couldn't read BSP file", ex);
            } finally {
                frame.setCursor(Cursor.getDefaultCursor());
            }
        }).start();
    }

    private static JPanel createEntitiesPanel() {
        textFieldPointEnts.setEditable(false);
        textFieldBrushEnts.setEditable(false);
        textFieldTotalEnts.setEditable(false);
        tableEntities.setAutoCreateRowSorter(true);
        tableEntities.setModel(new EntityTableModel());

        var pointLabel = new JLabel("Point");
        var brushLabel = new JLabel("Brush");
        var totalLabel = new JLabel("Total");
        var jScrollPane1 = new JScrollPane(tableEntities);

        var panelEntities = new JPanel();
        var panelEntitiesLayout = new GroupLayout(panelEntities);
        panelEntities.setLayout(panelEntitiesLayout);
        panelEntitiesLayout.setHorizontalGroup(
            panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelEntitiesLayout.createSequentialGroup()
                        .addComponent(pointLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textFieldPointEnts, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(brushLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textFieldBrushEnts, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(totalLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textFieldTotalEnts, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelEntitiesLayout.setVerticalGroup(
            panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pointLabel)
                    .addComponent(textFieldPointEnts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(brushLabel)
                    .addComponent(textFieldBrushEnts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalLabel)
                    .addComponent(textFieldTotalEnts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );

        var entitiesModel = tableEntities.getColumnModel();
        entitiesModel.getColumn(0).setPreferredWidth(250);
        entitiesModel.getColumn(1).setPreferredWidth(50);
        entitiesModel.getColumn(1).setCellRenderer(new DecimalFormatCellRenderer(new DecimalFormat("#,##0")));
        tableEntities.setAutoCreateColumnsFromModel(false);

        return panelEntities;
    }

    private static JPanel createEmbeddedPanel() {
        tableEmbedded.setAutoCreateRowSorter(true);
        tableEmbedded.setModel(new EmbeddedTableModel());
        tableEmbedded.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        extractEmbeddedButton.setEnabled(false);
        extractEmbeddedButton.addActionListener(Main::extractEmbeddedButtonActionPerformed);
        extractAllEmbeddedButton.setEnabled(false);
        extractAllEmbeddedButton.addActionListener(Main::extractAllEmbeddedButtonActionPerformed);
        extractEmbeddedZipButton.setEnabled(false);
        extractEmbeddedZipButton.addActionListener(Main::extractEmbeddedZipButtonActionPerformed);

        var scrollPaneEmbedded = new JScrollPane(tableEmbedded);

        var panelEmbedded = new JPanel();
        var panelEmbeddedLayout = new GroupLayout(panelEmbedded);
        panelEmbedded.setLayout(panelEmbeddedLayout);
        panelEmbeddedLayout.setHorizontalGroup(
            panelEmbeddedLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEmbeddedLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEmbeddedLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneEmbedded, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                    .addGroup(panelEmbeddedLayout.createSequentialGroup()
                        .addComponent(extractEmbeddedButton)
                        .addGap(18, 18, 18)
                        .addComponent(extractAllEmbeddedButton)
                        .addGap(18, 18, 18)
                        .addComponent(extractEmbeddedZipButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelEmbeddedLayout.setVerticalGroup(
            panelEmbeddedLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEmbeddedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneEmbedded, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEmbeddedLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(extractEmbeddedButton)
                    .addComponent(extractAllEmbeddedButton)
                    .addComponent(extractEmbeddedZipButton))
                .addContainerGap())
        );

        var embeddedModel = tableEmbedded.getColumnModel();
        embeddedModel.getColumn(0).setPreferredWidth(250);
        embeddedModel.getColumn(1).setPreferredWidth(50);
        embeddedModel.getColumn(1).setCellRenderer(new ByteSizeCellRenderer());
        tableEmbedded.setAutoCreateColumnsFromModel(false);

        return panelEmbedded;
    }

    private static JPanel createLumpsPanel() {
        tableLumps.setAutoCreateRowSorter(true);
        tableLumps.setModel(new LumpTableModel());
        tableLumps.getTableHeader().setReorderingAllowed(false);

        extractLumpButton.setEnabled(false);
        extractLumpButton.addActionListener(Main::extractLumpButtonActionPerformed);

        extractAllLumpsButton.setEnabled(false);
        extractAllLumpsButton.addActionListener(Main::extractAllLumpsButtonActionPerformed);

        var panelLumps = new JPanel();
        var scrollPaneLumps = new JScrollPane(tableLumps);
        var panelLumpsLayout = new GroupLayout(panelLumps);

        panelLumps.setLayout(panelLumpsLayout);
        panelLumpsLayout.setHorizontalGroup(
            panelLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLumpsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneLumps, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                    .addGroup(panelLumpsLayout.createSequentialGroup()
                        .addComponent(extractLumpButton)
                        .addGap(18, 18, 18)
                        .addComponent(extractAllLumpsButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelLumpsLayout.setVerticalGroup(
            panelLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLumpsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneLumps, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLumpsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(extractLumpButton)
                    .addComponent(extractAllLumpsButton))
                .addContainerGap())
        );

        var lumpsModel = tableLumps.getColumnModel();
        lumpsModel.getColumn(0).setPreferredWidth(30);
        lumpsModel.getColumn(1).setPreferredWidth(150);
        lumpsModel.getColumn(4).setPreferredWidth(40);
        lumpsModel.getColumn(2).setCellRenderer(new ByteSizeCellRenderer());
        lumpsModel.getColumn(3).setCellRenderer(new ProgressCellRenderer());
        tableLumps.setAutoCreateColumnsFromModel(false);

        return panelLumps;
    }

    private static JPanel createGameLumpsPanel() {
        tableGameLumps.setAutoCreateRowSorter(true);
        tableGameLumps.setModel(new GameLumpTableModel());
        tableGameLumps.getTableHeader().setReorderingAllowed(false);

        extractGameLumpButton.setEnabled(false);
        extractGameLumpButton.addActionListener(Main::extractGameLumpButtonActionPerformed);

        extractAllGameLumpsButton.setEnabled(false);
        extractAllGameLumpsButton.addActionListener(Main::extractAllGameLumpsButtonActionPerformed);

        var scrollPaneGameLumps = new JScrollPane(tableGameLumps);

        var panelGameLumps = new JPanel();
        var panelGameLumpsLayout = new GroupLayout(panelGameLumps);

        panelGameLumps.setLayout(panelGameLumpsLayout);
        panelGameLumpsLayout.setHorizontalGroup(
            panelGameLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGameLumpsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGameLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneGameLumps, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelGameLumpsLayout.createSequentialGroup()
                        .addComponent(extractGameLumpButton)
                        .addGap(18, 18, 18)
                        .addComponent(extractAllGameLumpsButton)
                        .addGap(0, 139, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGameLumpsLayout.setVerticalGroup(
            panelGameLumpsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGameLumpsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneGameLumps, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGameLumpsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(extractGameLumpButton)
                    .addComponent(extractAllGameLumpsButton))
                .addContainerGap())
        );

        var gameLumpsModel = tableGameLumps.getColumnModel();
        gameLumpsModel.getColumn(0).setPreferredWidth(30);
        gameLumpsModel.getColumn(3).setPreferredWidth(40);
        gameLumpsModel.getColumn(1).setCellRenderer(new ByteSizeCellRenderer());
        gameLumpsModel.getColumn(2).setCellRenderer(new ProgressCellRenderer());
        tableGameLumps.setAutoCreateColumnsFromModel(false);

        return panelGameLumps;
    }

    private static JPanel createGeneralsPanel() {
        linkLabelAppURL.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldAppID.setEditable(false);
        textFieldGame.setEditable(false);
        textFieldEndian.setEditable(false);
        textFieldCompressed.setEditable(false);
        textFieldVersion.setEditable(false);
        textFieldName.setEditable(false);
        textFieldRevision.setEditable(false);
        textFieldComment.setEditable(false);
        textFieldFileCRC.setEditable(false);
        textFieldMapCRC.setEditable(false);
        textFieldVbspParams.setEditable(false);
        textFieldVvisParams.setEditable(false);
        textFieldVradParams.setEditable(false);

        var commentLabel = new JLabel("Comment");

        var compressedLabel = new JLabel("Compressed");
        compressedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var endiannessLabel = new JLabel("Endianness");
        endiannessLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var versionLabel = new JLabel("Version");
        versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var labelRevision = new JLabel("Revision");
        labelRevision.setHorizontalAlignment(SwingConstants.RIGHT);

        var panelHeaders = new JPanel();
        var panelHeadersLayout = new GroupLayout(panelHeaders);

        panelHeaders.setBorder(BorderFactory.createTitledBorder("Headers"));
        panelHeaders.setLayout(panelHeadersLayout);
        panelHeadersLayout.setHorizontalGroup(
            panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelHeadersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeadersLayout.createSequentialGroup()
                        .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(commentLabel)
                            .addComponent(compressedLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(panelHeadersLayout.createSequentialGroup()
                                .addComponent(textFieldCompressed, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(endiannessLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldEndian))
                            .addComponent(textFieldComment)))
                    .addGroup(panelHeadersLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(nameLabel)
                            .addComponent(versionLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(panelHeadersLayout.createSequentialGroup()
                                .addComponent(textFieldVersion, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(labelRevision)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldRevision))
                            .addComponent(textFieldName))))
                .addContainerGap())
        );
        panelHeadersLayout.setVerticalGroup(
            panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelHeadersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(textFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(textFieldVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRevision)
                    .addComponent(textFieldRevision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(compressedLabel)
                    .addComponent(textFieldCompressed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(endiannessLabel)
                    .addComponent(textFieldEndian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeadersLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(commentLabel)
                    .addComponent(textFieldComment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        var generalTab = new JPanel();
        var panelGeneralLayout = new GroupLayout(generalTab);

        var panelGame = new JPanel();
        var panelGameLayout = new GroupLayout(panelGame);

        var appIDLabel = new JLabel("App-ID");
        appIDLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var labelGame = new JLabel("Name");
        labelGame.setHorizontalAlignment(SwingConstants.RIGHT);

        panelGame.setBorder(BorderFactory.createTitledBorder("Game"));
        panelGame.setLayout(panelGameLayout);
        panelGameLayout.setHorizontalGroup(
            panelGameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGameLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelGame)
                    .addComponent(appIDLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelGameLayout.createSequentialGroup()
                        .addComponent(textFieldAppID, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(linkLabelAppURL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(textFieldGame))
                .addContainerGap())
        );
        panelGameLayout.setVerticalGroup(
            panelGameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGameLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelGame)
                    .addComponent(textFieldGame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGameLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(appIDLabel)
                    .addComponent(textFieldAppID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(linkLabelAppURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        var fileCRCLabel = new JLabel("File CRC");
        fileCRCLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fileCRCLabel.setHorizontalTextPosition(SwingConstants.RIGHT);

        var labelMapCRC = new JLabel("Map CRC");

        var panelChecksums = new JPanel();
        var panelChecksumsLayout = new GroupLayout(panelChecksums);
        panelChecksums.setBorder(BorderFactory.createTitledBorder("Checksums"));
        panelChecksums.setLayout(panelChecksumsLayout);
        panelChecksumsLayout.setHorizontalGroup(
            panelChecksumsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelChecksumsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileCRCLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textFieldFileCRC, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelMapCRC)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textFieldMapCRC, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        panelChecksumsLayout.setVerticalGroup(
            panelChecksumsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelChecksumsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelChecksumsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelChecksumsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelMapCRC)
                        .addComponent(textFieldMapCRC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelChecksumsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fileCRCLabel)
                        .addComponent(textFieldFileCRC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        var vbspLabel = new JLabel("vbsp");
        var vvisLabel = new JLabel("vvis");
        var vradLabel = new JLabel("vrad");

        var panelCompileParams = new JPanel();
        var panelCompileParamsLayout = new GroupLayout(panelCompileParams);
        panelCompileParams.setBorder(BorderFactory.createTitledBorder("Detected compile parameters"));
        panelCompileParams.setLayout(panelCompileParamsLayout);
        panelCompileParamsLayout.setHorizontalGroup(
            panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelCompileParamsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(vradLabel)
                    .addComponent(vbspLabel)
                    .addComponent(vvisLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldVradParams)
                    .addComponent(textFieldVvisParams)
                    .addComponent(textFieldVbspParams))
                .addContainerGap())
        );
        panelCompileParamsLayout.setVerticalGroup(
            panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelCompileParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(vbspLabel)
                    .addComponent(textFieldVbspParams, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldVvisParams, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(vvisLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCompileParamsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(vradLabel)
                    .addComponent(textFieldVradParams, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        generalTab.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeaders, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelGame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelChecksums, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCompileParams, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeaders, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelChecksums, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCompileParams, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        return generalTab;
    }

    private static JTabbedPane createDependenciesPanel() {
        var font = new Font("Monospaced", 0, 12);

        textAreaMaterials.setColumns(20);
        textAreaMaterials.setEditable(false);
        textAreaMaterials.setFont(font);
        textAreaMaterials.setRows(5);

        textAreaSounds.setColumns(20);
        textAreaSounds.setFont(font);
        textAreaSounds.setRows(5);

        textAreaSoundScripts.setColumns(20);
        textAreaSoundScripts.setFont(font);
        textAreaSoundScripts.setRows(5);

        textAreaSoundscapes.setColumns(20);
        textAreaSoundscapes.setFont(font);
        textAreaSoundscapes.setRows(5);

        textAreaModels.setColumns(20);
        textAreaModels.setFont(font);
        textAreaModels.setRows(5);

        textAreaParticles.setColumns(20);
        textAreaParticles.setFont(font);
        textAreaParticles.setRows(5);

        var tabbedPaneDependencies = new JTabbedPane();
        tabbedPaneDependencies.addTab("Materials", new JScrollPane(textAreaMaterials));
        tabbedPaneDependencies.addTab("Sounds", new JScrollPane(textAreaSounds));
        tabbedPaneDependencies.addTab("Sound scripts", new JScrollPane(textAreaSoundScripts));
        tabbedPaneDependencies.addTab("Soundscapes", new JScrollPane(textAreaSoundscapes));
        tabbedPaneDependencies.addTab("Models", new JScrollPane(textAreaModels));
        tabbedPaneDependencies.addTab("Particles", new JScrollPane(textAreaParticles));
        return tabbedPaneDependencies;
    }

    private static JPanel createProtectionPanel() {
        checkBoxVmexEntity.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxVmexEntity.setIconTextGap(6);
        checkBoxVmexTexture.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxVmexTexture.setIconTextGap(6);
        checkBoxVmexBrush.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxVmexBrush.setIconTextGap(6);

        checkBoxIIDObfs.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxIIDObfs.setIconTextGap(6);
        checkBoxIIDTexHack.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxIIDTexHack.setIconTextGap(6);

        checkBoxBSPProtect.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxBSPProtect.setIconTextGap(6);

        var panelVmex = new JPanel();
        panelVmex.setBorder(BorderFactory.createTitledBorder("VMEX"));

        var panelVmexLayout = new GroupLayout(panelVmex);
        panelVmex.setLayout(panelVmexLayout);
        panelVmexLayout.setHorizontalGroup(
            panelVmexLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelVmexLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelVmexLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxVmexBrush, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxVmexTexture, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxVmexEntity, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelVmexLayout.setVerticalGroup(
            panelVmexLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelVmexLayout.createSequentialGroup()
                .addComponent(checkBoxVmexEntity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxVmexTexture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxVmexBrush, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        var IIDPanel = new JPanel();
        IIDPanel.setBorder(BorderFactory.createTitledBorder("IID"));

        var panelIIDLayout = new GroupLayout(IIDPanel);
        IIDPanel.setLayout(panelIIDLayout);
        panelIIDLayout.setHorizontalGroup(
            panelIIDLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelIIDLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(panelIIDLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIIDTexHack, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxIIDObfs, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelIIDLayout.setVerticalGroup(
            panelIIDLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelIIDLayout.createSequentialGroup()
                .addComponent(checkBoxIIDObfs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIIDTexHack, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        var panelOther = new JPanel();
        panelOther.setBorder(BorderFactory.createTitledBorder("Other"));

        var panelOtherLayout = new GroupLayout(panelOther);
        panelOther.setLayout(panelOtherLayout);
        panelOtherLayout.setHorizontalGroup(
            panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelOtherLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkBoxBSPProtect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(checkBoxBSPProtect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        var panelProt = new JPanel();

        var panelProtLayout = new GroupLayout(panelProt);
        panelProt.setLayout(panelProtLayout);
        panelProtLayout.setHorizontalGroup(
            panelProtLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelProtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProtLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelVmex, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(IIDPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelOther, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(151, Short.MAX_VALUE))
        );
        panelProtLayout.setVerticalGroup(
            panelProtLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelProtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelVmex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IIDPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOther, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(233, Short.MAX_VALUE))
        );

        return panelProt;
    }


    private static void openFileMenuItemActionPerformed(@SuppressWarnings("unused") ActionEvent e) {
        var openFileChooser = new JFileChooser();
        openFileChooser.setFileFilter(new FileExtensionFilter("Source engine map file", "bsp"));

        if(openFileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        loadFile(openFileChooser.getSelectedFile());
    }

    @SuppressWarnings("boxing")
    private static void extractLumpButtonActionPerformed(@SuppressWarnings("unused") ActionEvent e) {
        var selected = tableLumps.getSelectedRows();
        if(selected.length == 0) {
            return;
        }

        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            var files = 0;
            var model = tableLumps.getModel();
            var sorter = tableLumps.getRowSorter();
            var dest = saveDirectoryChooser.getSelectedFile();

            for(var index : selected) {
                index = sorter.convertRowIndexToModel(index);
                var lumpIndex = (Integer) model.getValueAt(index, 0);
                var lumpType = LumpType.get(lumpIndex, bspFile.getVersion());

                try {
                    extractLump(bspFile, dest, lumpType);
                    files++;
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Couldn't extract lump " + lumpType, ex);
                }
            }

            JOptionPane.showMessageDialog(frame, "Successfully extracted " + files + " lumps.");
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractAllLumpsButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            extractLump(bspFile, saveDirectoryChooser.getSelectedFile(), null);
            JOptionPane.showMessageDialog(frame, "Successfully extracted all lumps.");
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't extract lumps", ex);
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractGameLumpButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var selected = tableGameLumps.getSelectedRows();
        if (selected.length == 0) {
            return;
        }

        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            var files = 0;
            var model = tableGameLumps.getModel();
            var sorter = tableGameLumps.getRowSorter();
            var dest = saveDirectoryChooser.getSelectedFile();

            for(var index : selected) {
                index = sorter.convertRowIndexToModel(index);
                var id = (String) model.getValueAt(index, 0);

                try {
                    extractGameLump(bspFile, dest, id);
                    files++;
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Couldn't extract game lump " + id, ex);
                }
            }

            JOptionPane.showMessageDialog(frame, "Successfully extracted " + files + " game lumps.");
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractAllGameLumpsButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            extractGameLump(bspFile, saveDirectoryChooser.getSelectedFile(), null);
            JOptionPane.showMessageDialog(frame, "Successfully extracted all game lumps.");
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't extract lumps", ex);
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractEmbeddedButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var selected = tableEmbedded.getSelectedRows();
        if(selected.length == 0) {
            return;
        }

        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        var names = new ArrayList<String>();
        var model = tableEmbedded.getModel();
        var sorter = tableEmbedded.getRowSorter();

        for(var index : selected) {
            index = sorter.convertRowIndexToModel(index);
            names.add((String) model.getValueAt(index, 0));
        }

        try {
            bspFile.getPakFile().unpack(saveDirectoryChooser.getSelectedFile().toPath(), names::contains);

            JOptionPane.showMessageDialog(frame, "Successfully extracted " + names.size() + " embedded files.");
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't extract embedded files", ex);
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractAllEmbeddedButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var saveDirectoryChooser = new JFileChooser(currentFile);
        saveDirectoryChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (saveDirectoryChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            bspFile.getPakFile().unpack(saveDirectoryChooser.getSelectedFile().toPath(), false);
            JOptionPane.showMessageDialog(frame, "Successfully extracted all embedded files.");
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't extract embedded files", ex);
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void extractEmbeddedZipButtonActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        var saveZipFileChooser = new JFileChooser(new File(currentFile.getParent(), bspFile.getName() + ".zip"));
        saveZipFileChooser.setAcceptAllFileFilterUsed(false);
        saveZipFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveZipFileChooser.setFileFilter(new FileExtensionFilter("Zip file", "zip"));

        if (saveZipFileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            bspFile.getPakFile().unpack(saveZipFileChooser.getSelectedFile().toPath(), true);
            JOptionPane.showMessageDialog(frame, "Successfully extracted embedded Zip file.");
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't extract embedded Zip file", ex);
        } finally {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }


    @SuppressWarnings("boxing")
    private static void extractLump(BspFile bspFile, File destDir, LumpType type) throws IOException {
        FileUtils.forceMkdir(destDir);

        var lumps = bspFile.getLumps();

        for(var lump : lumps) {
            if (type != null && lump.getType() != type) {
                continue;
            }

            var fileName = String.format("%02d_%s.bin", lump.getIndex(), lump.getName());
            var lumpFile = new File(destDir, fileName);

            Main.LOGGER.log(Level.INFO, "Extracting {0}", lump);

            try {
                var is = lump.getInputStream();
                FileUtils.copyInputStreamToFile(is, lumpFile);
            } catch (IOException ex) {
                throw new IOException("Can't extract lump", ex);
            }
        }
    }

    @SuppressWarnings("boxing")
    private static void extractGameLump(BspFile bspFile, File destDir, String type) throws IOException {
        FileUtils.forceMkdir(destDir);

        var gameLumps = bspFile.getGameLumps();

        for(var gameLump : gameLumps) {
            if(type != null && !gameLump.getName().equalsIgnoreCase(type)) {
                continue;
            }

            var fileName = String.format("%s_v%d.bin", gameLump.getName(), gameLump.getVersion());
            var lumpFile = new File(destDir, fileName);

            Main.LOGGER.log(Level.INFO, "Extracting {0}", gameLump);

            try {
                var is = gameLump.getInputStream();
                FileUtils.copyInputStreamToFile(is, lumpFile);
            } catch (IOException ex) {
                throw new IOException("Can't extract lump", ex);
            }
        }
    }
}