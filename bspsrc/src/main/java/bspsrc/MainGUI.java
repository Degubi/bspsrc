package bspsrc;

import bsplib.app.*;
import bsplib.log.*;
import bsplib.modules.geom.*;
import bsplib.util.*;
import bspsrc.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

public class MainGUI extends JFrame {

    private static final Logger L = LogUtils.getLogger();

    private BspSourceConfig config;
    private BspSourceLogFrame logFrame;
    private DefaultListModel<BspFileEntry> listFilesModel = new DefaultListModel<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        LogUtils.configure();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new MainGUI().setVisible(true);
    }

    /** Creates new form BspSourceFrame */
    public MainGUI() {
        initComponents();
        initComponentsCustom();
        reset();

        FileDrop.attach(listFiles, files -> {
		    java.io.FileFilter filter = new BspFileFilter();

		    for (File file : files) {
		        if (file.isDirectory()) {
		            File[] subFiles = file.listFiles(filter);
		            for (File subFile : subFiles) {
		                listFilesModel.addElement(new BspFileEntry(subFile));
		            }
		        } else if (filter.accept(file)) {
		            listFilesModel.addElement(new BspFileEntry(file));
		        }
		    }

		    buttonDecompile.setEnabled(!listFilesModel.isEmpty());
		});
    }

    public ComboBoxModel getFaceTextureModel() {
        return new DefaultComboBoxModel<>(EnumToolTexture.values());
    }

    public ComboBoxModel getAppIDModel() {
        DefaultComboBoxModel<SourceApp> cbmodel = new DefaultComboBoxModel<>();
        List<SourceApp> apps = SourceAppDB.getInstance().getAppList();

        apps.stream()
            .sorted((SourceApp a1, SourceApp a2) -> a1.getName().compareTo(a2.getName()))
            .forEach(app -> cbmodel.addElement(app));

        cbmodel.insertElementAt(new SourceApp("Automatic", 0), 0);

        return cbmodel;
    }

    public ComboBoxModel getBrushModeModel() {
        return new DefaultComboBoxModel<>(BrushMode.values());
    }

    public ComboBoxModel getSourceFormatModel() {
        return new DefaultComboBoxModel<>(SourceFormat.values());
    }

    public ListModel getFilesModel() {
        return listFilesModel;
    }

    /**
     * Resets BSPSource and all form elements to their default values
     */
    public final void reset() {
        config = new BspSourceConfig();

        // check boxes
        checkBoxAreaportal.setSelected(config.writeAreaportals);
        checkBoxForceApMode.setSelected(config.apForceManualMapping);
        checkBoxForceOccMode.setSelected(config.occForceManualMapping);
        checkBoxCubemap.setSelected(config.writeCubemaps);
        checkBoxDebugMode.setSelected(config.isDebug());
        checkBoxDetail.setSelected(config.writeDetails);
        checkBoxDisp.setSelected(config.writeDisp);
        checkBoxFixToolTex.setSelected(config.fixToolTextures);
        checkBoxFixCubemapTex.setSelected(config.fixCubemapTextures);
        checkBoxFixRotation.setSelected(config.fixEntityRot);
        checkBoxLoadLumpFile.setSelected(config.loadLumpFiles);
        checkBoxOccluder.setSelected(config.writeOccluders);
        checkBoxLadder.setSelected(config.writeLadders);
        checkBoxOverlay.setSelected(config.writeOverlays);
        checkBoxPropStatic.setSelected(config.writeStaticProps);
        checkBoxVisgroups.setSelected(config.writeVisgroups);
        checkBoxCameras.setSelected(config.writeCameras);
        checkBoxExtractEmbedded.setSelected(config.unpackEmbedded);
        checkBoxSmartExtract.setSelected(config.smartUnpack);
        checkBoxSmartExtract.setEnabled(checkBoxExtractEmbedded.isSelected());

        // linked check boxes
        checkBoxEnableEntities.setSelected(config.isWriteEntities());
        setPanelEnabled(panelEntities, checkBoxEnableEntities);
        checkBoxEnableWorldBrushes.setSelected(config.writeWorldBrushes);
        setPanelEnabled(panelWorldBrushes, checkBoxEnableWorldBrushes);

        // combo boxes
        comboBoxBackfaceTex.setSelectedIndex(0);
        comboBoxFaceTex.setSelectedIndex(0);
        comboBoxMapFormat.setSelectedIndex(0);
        comboBoxSourceFormat.setSelectedIndex(0);

        // misc
        listFilesModel.removeAllElements();

        switch(config.brushMode) {
            case BRUSHPLANES:
                radioButtonBrushesPlanes.setSelected(true);
                break;

            case ORIGFACE:
                radioButtonOrigFaces.setSelected(true);
                break;

            case ORIGFACE_PLUS:
                radioButtonOrigSplitFaces.setSelected(true);
                break;

            case SPLITFACE:
                radioButtonSplitFaces.setSelected(true);
                break;
        }

        buttonDecompile.setEnabled(false);
    }

    public void setButtonsEnabled(boolean value) {
        buttonDecompile.setEnabled(value);
        buttonDefaults.setEnabled(value);
    }

    private void setPanelEnabled(JPanel panel, JCheckBox checkbox) {
        Component[] comps = panel.getComponents();

        for (Component comp : comps) {
            // don't touch the checkbox
            if (comp == checkbox) {
                continue;
            }

            // enable/disable everything in child panels
            if (comp instanceof JPanel) {
                setPanelEnabled((JPanel) comp, checkbox);
            }

            comp.setEnabled(checkbox.isSelected());
        }
    }

    private File[] openFileDialog(File defaultFile, FileFilter filter) {
        JFileChooser fc = new JFileChooser() {

            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (file != null && !file.exists()) {
                    showFileNotFoundDialog();
                    return;
                }
                super.approveSelection();
            }

            private void showFileNotFoundDialog() {
                JOptionPane.showMessageDialog(this, "The selected file doesn't exist.");
            }
        };
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(filter);

        if (defaultFile != null) {
            fc.setSelectedFile(defaultFile);
        } else {
            // use user.dir as default directory
            try {
                fc.setSelectedFile(new File(System.getProperty("user.dir")));
            } catch (Exception ex) {
            }
        }

        // show open file dialog
        int option = fc.showOpenDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return fc.getSelectedFiles();
    }

    private File saveFileDialog(File defaultFile, FileFilter filter) {
        JFileChooser fc = new JFileChooser() {

            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (file != null && file.exists() && !askOverwrite(file)) {
                    return;
                }
                super.approveSelection();
            }

            private boolean askOverwrite(File file) {
                String title = "Overwriting " + file.getPath();
                String message = "File " + file.getName() + " already exists.\n"
                        + "Do you like to replace it?";

                int choice = JOptionPane.showConfirmDialog(this, message, title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                return choice == JOptionPane.OK_OPTION;
            }
        };
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(defaultFile);
        fc.setFileFilter(filter);

        // show save file dialog
        int option = fc.showSaveDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return fc.getSelectedFile();
    }

    private File selectDirectoryDialog(File defaultFile) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (defaultFile != null) {
            fc.setSelectedFile(defaultFile);
        } else {
            // use user.dir as default directory
            try {
                fc.setSelectedFile(new File(System.getProperty("user.dir")));
            } catch (Exception ex) {
            }
        }

        // show dir selection dialog
        int option = fc.showOpenDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return fc.getSelectedFile();
    }

    private File[] openBspFileDialog(File bspFile) {
        return openFileDialog(bspFile,
                new FileExtensionFilter("Source engine map file", "bsp"));
    }

    private File saveVmfFileDialog(File vmfFile) {
        return saveFileDialog(vmfFile,
                new FileExtensionFilter("Hammer map file", "vmf"));
    }

    /**
     * Opens the log window and starts BspSource in a new thread.
     */
    private void startBspSource() {
        new Thread() {

            @Override
            public void run() {
                // overwrite files in config
                config.setFileSet(new HashSet<>(Collections.list(listFilesModel.elements())));

                // clear old output
                logFrame.clear();

                // show logging frame
                if (!logFrame.isVisible()) {
                    logFrame.setVisible(true);
                }

                logFrame.requestFocus();

                // enable logging on the output window
                logFrame.setLogging(true);

                // deactivate buttons
                setButtonsEnabled(false);

                try {
                    // start BspSource
                    BspSource bspsource = new BspSource(config);
                    bspsource.run();
                } catch (Throwable t) {
                    // "Oh this is bad!"
                    L.log(Level.SEVERE, "Fatal BSPSource error", t);
                } finally {
                    // activate buttons
                    setButtonsEnabled(true);

                    // use default logging again
                    logFrame.setLogging(false);
                }
            }
        }.start();
    }

    private void initComponentsCustom() {
        // add version to title
        setTitle("BSPSource " + BspSource.VERSION);

        // logging frame
        logFrame = new BspSourceLogFrame();

        // instant awesome, just add icons!
        try {
            URL iconUrl = getClass().getResource("icon.png");
            Image icon = Toolkit.getDefaultToolkit().createImage(iconUrl);
            setIconImage(icon);
            logFrame.setIconImage(icon);
        } catch (Exception ex) {
            // meh, don't care
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupBrushMode = new ButtonGroup();
        tabbedPaneOptions = new JTabbedPane();
        panelFiles = new JPanel();
        scrollFiles = new JScrollPane();
        listFiles = new JList();
        buttonAdd = new JButton();
        buttonRemove = new JButton();
        buttonRemoveAll = new JButton();
        labelDnDTip = new JLabel();
        panelWorldBrushes = new JPanel();
        checkBoxDisp = new JCheckBox();
        checkBoxEnableWorldBrushes = new JCheckBox();
        panelBrushMode = new JPanel();
        radioButtonBrushesPlanes = new JRadioButton();
        radioButtonOrigFaces = new JRadioButton();
        radioButtonSplitFaces = new JRadioButton();
        radioButtonOrigSplitFaces = new JRadioButton();
        panelEntities = new JPanel();
        panelPointEnts = new JPanel();
        checkBoxPropStatic = new JCheckBox();
        checkBoxCubemap = new JCheckBox();
        checkBoxOverlay = new JCheckBox();
        panelBrushEnts = new JPanel();
        checkBoxDetail = new JCheckBox();
        checkBoxAreaportal = new JCheckBox();
        checkBoxOccluder = new JCheckBox();
        checkBoxFixRotation = new JCheckBox();
        checkBoxLadder = new JCheckBox();
        checkBoxEnableEntities = new JCheckBox();
        jpEntityMapping = new JPanel();
        jpAreaportalMapping = new JPanel();
        checkBoxForceApMode = new JCheckBox();
        checkBoxForceOccMode = new JCheckBox();
        panelTextures = new JPanel();
        labelFaceTex = new JLabel();
        labelBackfaceTex = new JLabel();
        comboBoxFaceTex = new JComboBox();
        comboBoxBackfaceTex = new JComboBox();
        checkBoxFixCubemapTex = new JCheckBox();
        checkBoxFixToolTex = new JCheckBox();
        panelOther = new JPanel();
        checkBoxDebugMode = new JCheckBox();
        checkBoxLoadLumpFile = new JCheckBox();
        comboBoxMapFormat = new JComboBox();
        labelMapFormat = new JLabel();
        checkBoxVisgroups = new JCheckBox();
        checkBoxCameras = new JCheckBox();
        checkBoxExtractEmbedded = new JCheckBox();
        labelSourceFormat = new JLabel();
        comboBoxSourceFormat = new JComboBox();
        checkBoxSmartExtract = new JCheckBox();
        buttonDecompile = new JButton();
        buttonDefaults = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setResizable(false);

        listFiles.setModel(getFilesModel());
        scrollFiles.setViewportView(listFiles);

        buttonAdd.setText("Add");
        buttonAdd.addActionListener(evt -> buttonAddActionPerformed(evt));

        buttonRemove.setText("Remove");
        buttonRemove.addActionListener(evt -> buttonRemoveActionPerformed(evt));

        buttonRemoveAll.setText("Remove all");
        buttonRemoveAll.addActionListener(evt -> buttonRemoveAllActionPerformed(evt));

        labelDnDTip.setText("Tip: drag and drop files/folders on the box above");

        GroupLayout panelFilesLayout = new GroupLayout(panelFiles);
        panelFiles.setLayout(panelFilesLayout);
        panelFilesLayout.setHorizontalGroup(
            panelFilesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, panelFilesLayout.createSequentialGroup()
                        .addComponent(scrollFiles, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilesLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(buttonRemove, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonRemoveAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(labelDnDTip))
                .addContainerGap())
        );
        panelFilesLayout.setVerticalGroup(
            panelFilesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrollFiles, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addGroup(panelFilesLayout.createSequentialGroup()
                        .addComponent(buttonAdd)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRemove)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRemoveAll)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDnDTip)
                .addContainerGap())
        );

        tabbedPaneOptions.addTab("Files", panelFiles);

        checkBoxDisp.setText("Write displacements");
        checkBoxDisp.addActionListener(evt -> checkBoxDispActionPerformed(evt));

        checkBoxEnableWorldBrushes.setText("Enable");
        checkBoxEnableWorldBrushes.addActionListener(evt -> checkBoxEnableWorldBrushesActionPerformed(evt));

        panelBrushMode.setBorder(BorderFactory.createTitledBorder("Mode"));

        buttonGroupBrushMode.add(radioButtonBrushesPlanes);
        radioButtonBrushesPlanes.setText("Brushes and planes");
        radioButtonBrushesPlanes.setToolTipText("<html>Create brushes that closely resemble those<br>\nbrushes from which the map was originally created from.</html>");
        radioButtonBrushesPlanes.addActionListener(evt -> radioButtonBrushesPlanesActionPerformed(evt));

        buttonGroupBrushMode.add(radioButtonOrigFaces);
        radioButtonOrigFaces.setText("Original faces only");
        radioButtonOrigFaces.setToolTipText("<html>Create flat brushes from the culled<br>\nbrush sides of the original brushes.<br>\n<b>Note:</b> some sides may be missing.</html>");
        radioButtonOrigFaces.addActionListener(evt -> radioButtonOrigFacesActionPerformed(evt));

        buttonGroupBrushMode.add(radioButtonSplitFaces);
        radioButtonSplitFaces.setText("Split faces only");
        radioButtonSplitFaces.setToolTipText("<html>Create flat brushes from the split faces<br>\nthe engine is using for rendering.\n</html>");
        radioButtonSplitFaces.addActionListener(evt -> radioButtonSplitFacesActionPerformed(evt));

        buttonGroupBrushMode.add(radioButtonOrigSplitFaces);
        radioButtonOrigSplitFaces.setText("Original/split faces");
        radioButtonOrigSplitFaces.setToolTipText("<html>Create flat brushes from the culled<br>\nbrush sides of the original brushes.<br>\nWhen a side doesn't exist, the split face<br>\nis created instead.\n</html>");
        radioButtonOrigSplitFaces.addActionListener(evt -> radioButtonOrigSplitFacesActionPerformed(evt));

        GroupLayout panelBrushModeLayout = new GroupLayout(panelBrushMode);
        panelBrushMode.setLayout(panelBrushModeLayout);
        panelBrushModeLayout.setHorizontalGroup(
            panelBrushModeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelBrushModeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBrushModeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonBrushesPlanes)
                    .addComponent(radioButtonOrigFaces)
                    .addComponent(radioButtonOrigSplitFaces)
                    .addComponent(radioButtonSplitFaces))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBrushModeLayout.setVerticalGroup(
            panelBrushModeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelBrushModeLayout.createSequentialGroup()
                .addComponent(radioButtonBrushesPlanes)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonOrigFaces)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonOrigSplitFaces)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonSplitFaces)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout panelWorldBrushesLayout = new GroupLayout(panelWorldBrushes);
        panelWorldBrushes.setLayout(panelWorldBrushesLayout);
        panelWorldBrushesLayout.setHorizontalGroup(
            panelWorldBrushesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelWorldBrushesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelWorldBrushesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelWorldBrushesLayout.createSequentialGroup()
                        .addComponent(panelBrushMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkBoxDisp))
                    .addComponent(checkBoxEnableWorldBrushes))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelWorldBrushesLayout.setVerticalGroup(
            panelWorldBrushesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelWorldBrushesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxEnableWorldBrushes)
                .addGap(7, 7, 7)
                .addGroup(panelWorldBrushesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxDisp)
                    .addComponent(panelBrushMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        tabbedPaneOptions.addTab("World", panelWorldBrushes);

        panelPointEnts.setBorder(BorderFactory.createTitledBorder("Point entities"));

        checkBoxPropStatic.setText("prop_static");
        checkBoxPropStatic.addActionListener(evt -> checkBoxPropStaticActionPerformed(evt));

        checkBoxCubemap.setText("info_cubemap");
        checkBoxCubemap.addActionListener(evt -> checkBoxCubemapActionPerformed(evt));

        checkBoxOverlay.setText("info_overlay");
        checkBoxOverlay.addActionListener(evt -> checkBoxOverlayActionPerformed(evt));

        GroupLayout panelPointEntsLayout = new GroupLayout(panelPointEnts);
        panelPointEnts.setLayout(panelPointEntsLayout);
        panelPointEntsLayout.setHorizontalGroup(
            panelPointEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelPointEntsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPointEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxPropStatic)
                    .addComponent(checkBoxCubemap)
                    .addComponent(checkBoxOverlay))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPointEntsLayout.setVerticalGroup(
            panelPointEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelPointEntsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxPropStatic)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxCubemap)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxOverlay)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBrushEnts.setBorder(BorderFactory.createTitledBorder("Brush entities"));

        checkBoxDetail.setText("func_detail");
        checkBoxDetail.addActionListener(evt -> checkBoxDetailActionPerformed(evt));

        checkBoxAreaportal.setText("func_areaportal/_window");
        checkBoxAreaportal.addChangeListener(evt -> checkBoxAreaportalStateChanged(evt));

        checkBoxOccluder.setText("func_occluder");
        checkBoxOccluder.addChangeListener(evt -> checkBoxOccluderStateChanged(evt));

        checkBoxFixRotation.setText("Fix rotation of instances");
        checkBoxFixRotation.setToolTipText("<html>\nFixes rotation of brush entities that were compiled from rotated instances.<br>\nThe wrong rotation of these brushes is visible in Hammer only and <br>\nwon't affect re-compilation.\n</html>");
        checkBoxFixRotation.addActionListener(evt -> checkBoxFixRotationActionPerformed(evt));

        checkBoxLadder.setText("func_ladder");
        checkBoxLadder.addActionListener(evt -> checkBoxLadderActionPerformed(evt));

        GroupLayout panelBrushEntsLayout = new GroupLayout(panelBrushEnts);
        panelBrushEnts.setLayout(panelBrushEntsLayout);
        panelBrushEntsLayout.setHorizontalGroup(
            panelBrushEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelBrushEntsLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBrushEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxDetail)
                    .addComponent(checkBoxAreaportal)
                    .addComponent(checkBoxOccluder)
                    .addComponent(checkBoxFixRotation)
                    .addComponent(checkBoxLadder))
                .addGap(5, 5, 5))
        );
        panelBrushEntsLayout.setVerticalGroup(
            panelBrushEntsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelBrushEntsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxDetail)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAreaportal)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxOccluder)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxFixRotation)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxLadder)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxEnableEntities.setText("Enable");
        checkBoxEnableEntities.addActionListener(evt -> checkBoxEnableEntitiesActionPerformed(evt));

        GroupLayout panelEntitiesLayout = new GroupLayout(panelEntities);
        panelEntities.setLayout(panelEntitiesLayout);
        panelEntitiesLayout.setHorizontalGroup(
            panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxEnableEntities)
                    .addGroup(panelEntitiesLayout.createSequentialGroup()
                        .addComponent(panelPointEnts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelBrushEnts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelEntitiesLayout.setVerticalGroup(
            panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxEnableEntities)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelEntitiesLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelBrushEnts, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelPointEnts, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPaneOptions.addTab("Entities", panelEntities);

        jpEntityMapping.setName("Mapping"); // NOI18N

        jpAreaportalMapping.setBorder(BorderFactory.createTitledBorder("Force Manual Method"));
        jpAreaportalMapping.setName(""); // NOI18N
        jpAreaportalMapping.setPreferredSize(new java.awt.Dimension(135, 48));

        checkBoxForceApMode.setText("Areaportals");
        checkBoxForceApMode.setToolTipText("This forces the mapping of areaportals to brushes, to be made manually. Only check this if you're getting errors with the default mapping method!");
        checkBoxForceApMode.setActionCommand("forceManualMapping");
        checkBoxForceApMode.addItemListener(evt -> checkBoxForceApModeItemStateChanged(evt));

        checkBoxForceOccMode.setText("Occluders");
        checkBoxForceOccMode.setToolTipText("This forces the mapping of occluders to brushes, to be made manually. Only check this if you're getting errors with the default mapping method!");
        checkBoxForceOccMode.addItemListener(evt -> checkBoxForceOccModeItemStateChanged(evt));

        GroupLayout jpAreaportalMappingLayout = new GroupLayout(jpAreaportalMapping);
        jpAreaportalMapping.setLayout(jpAreaportalMappingLayout);
        jpAreaportalMappingLayout.setHorizontalGroup(
            jpAreaportalMappingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(checkBoxForceOccMode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(checkBoxForceApMode, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
        );
        jpAreaportalMappingLayout.setVerticalGroup(
            jpAreaportalMappingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jpAreaportalMappingLayout.createSequentialGroup()
                .addComponent(checkBoxForceApMode)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxForceOccMode))
        );

        GroupLayout jpEntityMappingLayout = new GroupLayout(jpEntityMapping);
        jpEntityMapping.setLayout(jpEntityMappingLayout);
        jpEntityMappingLayout.setHorizontalGroup(
            jpEntityMappingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jpEntityMappingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpAreaportalMapping, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );
        jpEntityMappingLayout.setVerticalGroup(
            jpEntityMappingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jpEntityMappingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpAreaportalMapping, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(120, Short.MAX_VALUE))
        );

        tabbedPaneOptions.addTab("Entity mapping", jpEntityMapping);

        labelFaceTex.setText("Face texture");

        labelBackfaceTex.setText("Back-face texture");

        comboBoxFaceTex.setModel(getFaceTextureModel());
        comboBoxFaceTex.addActionListener(evt -> comboBoxFaceTexActionPerformed(evt));

        comboBoxBackfaceTex.setModel(getFaceTextureModel());
        comboBoxBackfaceTex.addActionListener(evt -> comboBoxBackfaceTexActionPerformed(evt));

        checkBoxFixCubemapTex.setText("Fix cubemap textures");
        checkBoxFixCubemapTex.setToolTipText("Fix textures for environment-mapped materials.");
        checkBoxFixCubemapTex.addActionListener(evt -> checkBoxFixCubemapTexActionPerformed(evt));

        checkBoxFixToolTex.setText("Fix tool textures");
        checkBoxFixToolTex.setToolTipText("Fix tool textures such as toolsnodraw or toolsblocklight.");
        checkBoxFixToolTex.addActionListener(evt -> checkBoxFixToolTexActionPerformed(evt));

        GroupLayout panelTexturesLayout = new GroupLayout(panelTextures);
        panelTextures.setLayout(panelTexturesLayout);
        panelTexturesLayout.setHorizontalGroup(
            panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelTexturesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxFixToolTex)
                    .addComponent(checkBoxFixCubemapTex)
                    .addGroup(panelTexturesLayout.createSequentialGroup()
                        .addGroup(panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(labelBackfaceTex)
                            .addComponent(labelFaceTex))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(comboBoxFaceTex, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxBackfaceTex, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(171, Short.MAX_VALUE))
        );
        panelTexturesLayout.setVerticalGroup(
            panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelTexturesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxFaceTex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFaceTex))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTexturesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBackfaceTex)
                    .addComponent(comboBoxBackfaceTex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxFixCubemapTex)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxFixToolTex)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        tabbedPaneOptions.addTab("Textures", panelTextures);

        checkBoxDebugMode.setText("Debug mode");
        checkBoxDebugMode.setToolTipText("<html>\nThe debug mode produces <i>very</i> verbose output<br>\ntext and writes additional data into the VMF file.\n</html>");
        checkBoxDebugMode.addActionListener(evt -> checkBoxDebugModeActionPerformed(evt));

        checkBoxLoadLumpFile.setText("Load lump files");
        checkBoxLoadLumpFile.setToolTipText("<html>\nWhen enabled, external lump files  <i>(.lmp)</i> with the same<br>\nname as the BSP file will be processed during decompilation.\n</html>");
        checkBoxLoadLumpFile.addActionListener(evt -> checkBoxLoadLumpFileActionPerformed(evt));

        comboBoxMapFormat.setModel(getAppIDModel());
        comboBoxMapFormat.setToolTipText("<html>\n<p>Overrides the internal game detection for maps.</p>\n<p>Select <i>\"Automatic\"</i> for automatic detection.</p>\n<br>\n<b>Warning:</b> Change only if the game isn't detected<br>\ncorrectly, wrong values can cause program errors!\n</html>");
        comboBoxMapFormat.addActionListener(evt -> comboBoxMapFormatActionPerformed(evt));

        labelMapFormat.setText("BSP format");

        checkBoxVisgroups.setText("Create Hammer visgroups");
        checkBoxVisgroups.setToolTipText("<html>Automatically group instanced entities to visgroups.\n<p><b>Note:</b> World brushes created from instances can't<br>\nbe grouped because of missing information.</p>\n</html>");
        checkBoxVisgroups.addActionListener(evt -> checkBoxVisgroupsActionPerformed(evt));

        checkBoxCameras.setText("Create Hammer cameras");
        checkBoxCameras.setToolTipText("<html>Create Hammer viewport cameras above <br>\neach player spawn to ease navigation.</html>");
        checkBoxCameras.addActionListener(evt -> checkBoxCamerasActionPerformed(evt));

        checkBoxExtractEmbedded.setText("Extract embedded files");
        checkBoxExtractEmbedded.setToolTipText("<html>\nExtract all resource files that are embedded into the BSP file.\n</html>");
        checkBoxExtractEmbedded.addItemListener(evt -> checkBoxExtractEmbeddedItemStateChanged(evt));

        labelSourceFormat.setText("VMF format");

        comboBoxSourceFormat.setModel(getSourceFormatModel());
        comboBoxSourceFormat.setToolTipText("<html>\n<p>Sets the VMF source format.</p>\n<p>On default, newer maps are decompiled to a format<br/>\nthat is incompatible with older Hammer versions. <br/>\nSelect <i>\"Source 2004-2009\"</i> if you want to make sure that<br/>\nthe decompiled map is loadable in old Hammer versions.\n</html>");
        comboBoxSourceFormat.addActionListener(evt -> comboBoxSourceFormatActionPerformed(evt));

        checkBoxSmartExtract.setText("Smart extracting");
        checkBoxSmartExtract.setToolTipText("Ignore files generated by vBsp only used by the engine for running the map.");
        checkBoxSmartExtract.addActionListener(evt -> checkBoxSmartExtractActionPerformed(evt));

        GroupLayout panelOtherLayout = new GroupLayout(panelOther);
        panelOther.setLayout(panelOtherLayout);
        panelOtherLayout.setHorizontalGroup(
            panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(checkBoxSmartExtract))
                    .addComponent(checkBoxExtractEmbedded)
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxDebugMode)
                            .addComponent(checkBoxLoadLumpFile))
                        .addGap(18, 18, 18)
                        .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxCameras)
                            .addComponent(checkBoxVisgroups)))
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(labelSourceFormat)
                            .addComponent(labelMapFormat))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboBoxMapFormat, 0, 190, Short.MAX_VALUE)
                            .addComponent(comboBoxSourceFormat, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addComponent(checkBoxDebugMode)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxLoadLumpFile))
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addComponent(checkBoxVisgroups)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxCameras)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxExtractEmbedded)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxSmartExtract)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMapFormat)
                    .addComponent(comboBoxMapFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOtherLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSourceFormat)
                    .addComponent(comboBoxSourceFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        tabbedPaneOptions.addTab("Other", panelOther);

        buttonDecompile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        buttonDecompile.setText("Decompile");
        buttonDecompile.addActionListener(evt -> buttonDecompileActionPerformed(evt));

        buttonDefaults.setText("Defaults");
        buttonDefaults.setToolTipText("Resets all configurations to their defaults.");
        buttonDefaults.addActionListener(evt -> buttonDefaultsActionPerformed(evt));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPaneOptions, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonDefaults)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                        .addComponent(buttonDecompile)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPaneOptions)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDefaults)
                    .addComponent(buttonDecompile))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxLadderActionPerformed(ActionEvent evt) {//GEN-FIRST:event_checkBoxLadderActionPerformed
        config.writeLadders = checkBoxLadder.isSelected();
    }//GEN-LAST:event_checkBoxLadderActionPerformed

    private void checkBoxAreaportalStateChanged(ChangeEvent evt) {//GEN-FIRST:event_checkBoxAreaportalStateChanged
        config.writeAreaportals = checkBoxAreaportal.isSelected();
        checkBoxForceApMode.setEnabled(checkBoxAreaportal.isSelected());
    }//GEN-LAST:event_checkBoxAreaportalStateChanged

    private void checkBoxOccluderStateChanged(ChangeEvent evt) {//GEN-FIRST:event_checkBoxOccluderStateChanged
        config.writeOccluders = checkBoxOccluder.isSelected();
        checkBoxForceOccMode.setEnabled(checkBoxOccluder.isSelected());
    }//GEN-LAST:event_checkBoxOccluderStateChanged

    private void checkBoxForceApModeItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_checkBoxForceApModeItemStateChanged
        config.apForceManualMapping = checkBoxForceApMode.isSelected();
    }//GEN-LAST:event_checkBoxForceApModeItemStateChanged

    private void checkBoxForceOccModeItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_checkBoxForceOccModeItemStateChanged
        config.occForceManualMapping = checkBoxForceOccMode.isSelected();
    }//GEN-LAST:event_checkBoxForceOccModeItemStateChanged

    private void checkBoxSmartExtractActionPerformed(ActionEvent evt) {//GEN-FIRST:event_checkBoxSmartExtractActionPerformed
        config.smartUnpack = checkBoxSmartExtract.isSelected();
    }//GEN-LAST:event_checkBoxSmartExtractActionPerformed

    private void checkBoxExtractEmbeddedItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_checkBoxExtractEmbeddedItemStateChanged
        config.unpackEmbedded = checkBoxExtractEmbedded.isSelected();
        checkBoxSmartExtract.setEnabled(checkBoxExtractEmbedded.isSelected());
    }//GEN-LAST:event_checkBoxExtractEmbeddedItemStateChanged

    private void checkBoxEnableEntitiesActionPerformed(ActionEvent evt) {
        config.setWriteEntities(checkBoxEnableEntities.isSelected());
        setPanelEnabled(panelEntities, checkBoxEnableEntities);
    }

    private void checkBoxPropStaticActionPerformed(ActionEvent evt) {
        config.writeStaticProps = checkBoxPropStatic.isSelected();
    }

    private void checkBoxDetailActionPerformed(ActionEvent evt) {
        config.writeDetails = checkBoxDetail.isSelected();
    }

    private void checkBoxOverlayActionPerformed(ActionEvent evt) {
        config.writeOverlays = checkBoxOverlay.isSelected();
    }

    private void checkBoxCubemapActionPerformed(ActionEvent evt) {
        config.writeCubemaps = checkBoxCubemap.isSelected();
    }

    private void checkBoxFixRotationActionPerformed(ActionEvent evt) {
        config.fixEntityRot = checkBoxFixRotation.isSelected();
    }

    private void buttonDecompileActionPerformed(ActionEvent evt) {
        if (listFilesModel.isEmpty()) {
            // how the hell has the user bypassed the buttons?
            return;
        }

        // don't show file dialog for multiple bsp files
        if (listFilesModel.size() == 1) {
            BspFileEntry entry = listFilesModel.firstElement();
            File vmfFile = saveVmfFileDialog(entry.getVmfFile());

            if (vmfFile == null) {
                // the user obviously doesn't want to decompile...
                return;
            }

            entry.setVmfFile(vmfFile);
            entry.setPakDir(new File(vmfFile.getAbsoluteFile().getParentFile(),
                    entry.getPakDir().getName()));
            entry.setNmosFile(new File(vmfFile.getAbsoluteFile().getParentFile(), entry.getNmosFile().getName()));
        } else {
            File dstDir = selectDirectoryDialog(null);

            if (dstDir == null) {
                // the user obviously doesn't want to decompile...
                return;
            }

            // update paths with new destination dir
            Enumeration<BspFileEntry> entries = listFilesModel.elements();
            while(entries.hasMoreElements()) {
                BspFileEntry entry = entries.nextElement();
                entry.setVmfFile(new File(dstDir, entry.getVmfFile().getName()));
                entry.setPakDir(new File(dstDir, entry.getPakDir().getName()));
                entry.setNmosFile(new File(dstDir, entry.getNmosFile().getName()));
            }
        }

        startBspSource();
    }

    private void buttonDefaultsActionPerformed(ActionEvent evt) {
        reset();
    }

    private void checkBoxDispActionPerformed(ActionEvent evt) {
        config.writeDisp = checkBoxDisp.isSelected();
    }

    private void comboBoxFaceTexActionPerformed(ActionEvent evt) {
        EnumToolTexture tex = (EnumToolTexture)comboBoxFaceTex.getSelectedItem();
        config.faceTexture = tex.texPath;
    }

    private void comboBoxBackfaceTexActionPerformed(ActionEvent evt) {
        EnumToolTexture tex = (EnumToolTexture)comboBoxBackfaceTex.getSelectedItem();
        config.backfaceTexture = tex.texPath;
    }

    private void checkBoxFixCubemapTexActionPerformed(ActionEvent evt) {
        config.fixCubemapTextures = checkBoxFixCubemapTex.isSelected();
    }

    private void checkBoxDebugModeActionPerformed(ActionEvent evt) {
        config.setDebug(checkBoxDebugMode.isSelected());
    }

    private void checkBoxLoadLumpFileActionPerformed(ActionEvent evt) {
        config.loadLumpFiles = checkBoxLoadLumpFile.isSelected();
    }

    private void buttonRemoveAllActionPerformed(ActionEvent evt) {
        listFilesModel.clear();
        buttonDecompile.setEnabled(false);
    }

    private void buttonRemoveActionPerformed(ActionEvent evt) {
        int[] selected = listFiles.getSelectedIndices();
        listFiles.clearSelection();

        for (int index : selected) {
            listFilesModel.remove(index);
        }

        buttonDecompile.setEnabled(!listFilesModel.isEmpty());
    }

    private void buttonAddActionPerformed(ActionEvent evt) {
        File bspFile = null;

        if (listFilesModel.size() == 1) {
            bspFile = listFilesModel.firstElement().getBspFile();
        }

        File[] bspFiles = openBspFileDialog(bspFile);

        if (bspFiles == null) {
            // selection canceled
            return;
        }

        for (File file : bspFiles) {
            listFilesModel.addElement(new BspFileEntry(file));
        }

        buttonDecompile.setEnabled(!listFilesModel.isEmpty());
    }

    private void comboBoxMapFormatActionPerformed(ActionEvent evt) {
        config.defaultApp = (SourceApp) comboBoxMapFormat.getSelectedItem();
    }

    private void checkBoxEnableWorldBrushesActionPerformed(ActionEvent evt) {
        config.writeWorldBrushes = checkBoxEnableWorldBrushes.isSelected();
        setPanelEnabled(panelWorldBrushes, checkBoxEnableWorldBrushes);
    }

    private void checkBoxVisgroupsActionPerformed(ActionEvent evt) {
        config.writeVisgroups = checkBoxVisgroups.isSelected();
    }

    private void checkBoxCamerasActionPerformed(ActionEvent evt) {
        config.writeCameras = checkBoxCameras.isSelected();
    }

    private void radioButtonBrushesPlanesActionPerformed(ActionEvent evt) {
        config.brushMode = BrushMode.BRUSHPLANES;
    }

    private void radioButtonOrigFacesActionPerformed(ActionEvent evt) {
        config.brushMode = BrushMode.ORIGFACE;
    }

    private void radioButtonOrigSplitFacesActionPerformed(ActionEvent evt) {
        config.brushMode = BrushMode.ORIGFACE_PLUS;
    }

    private void radioButtonSplitFacesActionPerformed(ActionEvent evt) {
        config.brushMode = BrushMode.SPLITFACE;
    }

    private void checkBoxFixToolTexActionPerformed(ActionEvent evt) {
        config.fixToolTextures = checkBoxFixToolTex.isSelected();
    }

    private void checkBoxExtractEmbeddedActionPerformed(ActionEvent evt) {
        config.unpackEmbedded = checkBoxExtractEmbedded.isSelected();
        checkBoxSmartExtract.setEnabled(checkBoxExtractEmbedded.isSelected());
    }

    private void comboBoxSourceFormatActionPerformed(ActionEvent evt) {
        config.sourceFormat = (SourceFormat)comboBoxSourceFormat.getSelectedItem();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonAdd;
    private JButton buttonDecompile;
    private JButton buttonDefaults;
    private ButtonGroup buttonGroupBrushMode;
    private JButton buttonRemove;
    private JButton buttonRemoveAll;
    private JCheckBox checkBoxAreaportal;
    private JCheckBox checkBoxCameras;
    private JCheckBox checkBoxCubemap;
    private JCheckBox checkBoxDebugMode;
    private JCheckBox checkBoxDetail;
    private JCheckBox checkBoxDisp;
    private JCheckBox checkBoxEnableEntities;
    private JCheckBox checkBoxEnableWorldBrushes;
    private JCheckBox checkBoxExtractEmbedded;
    private JCheckBox checkBoxFixCubemapTex;
    private JCheckBox checkBoxFixRotation;
    private JCheckBox checkBoxFixToolTex;
    private JCheckBox checkBoxForceApMode;
    private JCheckBox checkBoxForceOccMode;
    private JCheckBox checkBoxLadder;
    private JCheckBox checkBoxLoadLumpFile;
    private JCheckBox checkBoxOccluder;
    private JCheckBox checkBoxOverlay;
    private JCheckBox checkBoxPropStatic;
    private JCheckBox checkBoxSmartExtract;
    private JCheckBox checkBoxVisgroups;
    private JComboBox comboBoxBackfaceTex;
    private JComboBox comboBoxFaceTex;
    private JComboBox comboBoxMapFormat;
    private JComboBox comboBoxSourceFormat;
    private JPanel jpAreaportalMapping;
    private JPanel jpEntityMapping;
    private JLabel labelBackfaceTex;
    private JLabel labelDnDTip;
    private JLabel labelFaceTex;
    private JLabel labelMapFormat;
    private JLabel labelSourceFormat;
    private JList listFiles;
    private JPanel panelBrushEnts;
    private JPanel panelBrushMode;
    private JPanel panelEntities;
    private JPanel panelFiles;
    private JPanel panelOther;
    private JPanel panelPointEnts;
    private JPanel panelTextures;
    private JPanel panelWorldBrushes;
    private JRadioButton radioButtonBrushesPlanes;
    private JRadioButton radioButtonOrigFaces;
    private JRadioButton radioButtonOrigSplitFaces;
    private JRadioButton radioButtonSplitFaces;
    private JScrollPane scrollFiles;
    private JTabbedPane tabbedPaneOptions;
}