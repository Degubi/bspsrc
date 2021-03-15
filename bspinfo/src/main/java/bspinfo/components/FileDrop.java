package bspinfo.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import javax.swing.*;
import javax.swing.border.*;

public final class FileDrop {

    private transient Border normalBorder;
    private transient DropTargetListener dropListener;
    private static Boolean supportsDnD;

    @SuppressWarnings("unused")
    public static void attach(Component component, Consumer<File[]> listener) {
        new FileDrop(component, listener);
    }

    private FileDrop(Component c, Consumer<File[]> listener) {
        PrintStream out = null;
        var dragBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0f, 0f, 1f, 0.25f));

        if (supportsDnD()) {
            dropListener = new DropTargetListener() {

                @Override
                public void dragEnter(DropTargetDragEvent evt) {
                    log(out, "FileDrop: dragEnter event.");

                    if (isDragOk(out, evt)) {
                        if (c instanceof JComponent) {
                            JComponent jc = (JComponent) c;
                            normalBorder = jc.getBorder();
                            log(out, "FileDrop: normal border saved.");
                            jc.setBorder(dragBorder);
                            log(out, "FileDrop: drag border set.");
                        }

                        evt.acceptDrag(DnDConstants.ACTION_COPY);
                        log(out, "FileDrop: event accepted.");
                    }
                    else {
                        evt.rejectDrag();
                        log(out, "FileDrop: event rejected.");
                    }
                }

                @Override
                public void dragOver(DropTargetDragEvent evt) {}

                @Override
                public void drop(DropTargetDropEvent evt) {
                    log(out, "FileDrop: drop event.");
                    try {
                        Transferable tr = evt.getTransferable();

                        if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            evt.acceptDrop(DnDConstants.ACTION_COPY);
                            log(out, "FileDrop: file list accepted.");

                            var fileList = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);

                            File[] filesTemp = new File[fileList.size()];
                            fileList.toArray(filesTemp);
                            final File[] files = filesTemp;

                            if (listener != null) {
                                listener.accept(files);
                            }

                            evt.getDropTargetContext().dropComplete(true);
                            log(out, "FileDrop: drop complete.");
                        }
                        else
                        {
                            DataFlavor[] flavors = tr.getTransferDataFlavors();
                            boolean handled = false;
                            for (int zz = 0; zz < flavors.length; zz++) {
                                if (flavors[zz].isRepresentationClassReader()) {
                                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                                    log(out, "FileDrop: reader accepted.");

                                    Reader reader = flavors[zz].getReaderForText(tr);

                                    BufferedReader br = new BufferedReader(reader);

                                    if (listener != null) {
                                        listener.accept(createFileArray(br, out));
                                    }

                                    evt.getDropTargetContext().dropComplete(true);
                                    log(out, "FileDrop: drop complete.");
                                    handled = true;
                                    break;
                                }
                            }
                            if (!handled) {
                                log(out, "FileDrop: not a file list or reader - abort.");
                                evt.rejectDrop();
                            }
                        }
                    }
                    catch (IOException io) {
                        log(out, "FileDrop: IOException - abort:");
                        io.printStackTrace(out);
                        evt.rejectDrop();
                    }
                    catch (UnsupportedFlavorException ufe) {
                        log(out, "FileDrop: UnsupportedFlavorException - abort:");
                        ufe.printStackTrace(out);
                        evt.rejectDrop();
                    }
                    finally {
                        if (c instanceof JComponent) {
                            JComponent jc = (JComponent) c;
                            jc.setBorder(normalBorder);
                            log(out, "FileDrop: normal border restored.");
                        }
                    }
                }

                @Override
                public void dragExit(DropTargetEvent evt) {
                    log(out, "FileDrop: dragExit event.");

                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent) c;
                        jc.setBorder(normalBorder);
                        log(out, "FileDrop: normal border restored.");
                    }
                }

                @Override
                public void dropActionChanged(DropTargetDragEvent evt) {
                    log(out, "FileDrop: dropActionChanged event.");

                    if (isDragOk(out, evt)) {
                        evt.acceptDrag(DnDConstants.ACTION_COPY);
                        log(out, "FileDrop: event accepted.");
                    }
                    else {
                        evt.rejectDrag();
                        log(out, "FileDrop: event rejected.");
                    }
                }
            };

            makeDropTarget(out, c, true);
        }
        else {
            log(out, "FileDrop: Drag and drop is not supported with this JVM");
        }
    }

    private static boolean supportsDnD() {
        if (supportsDnD == null) {
            try {
                Class.forName("java.awt.dnd.DnDConstants");
                supportsDnD = true;
            }
            catch (Exception e) {
                supportsDnD = false;
            }
        }
        return supportsDnD.booleanValue();
    }

    private static String ZERO_CHAR_STRING = "" + (char) 0;

    private static File[] createFileArray(BufferedReader bReader, PrintStream out) {
        try {
            List<File> list = new ArrayList<>();
            String line;
            while ((line = bReader.readLine()) != null) {
                try {
                    if (ZERO_CHAR_STRING.equals(line)) {
                        continue;
                    }

                    File file = new File(new URI(line));
                    list.add(file);
                } catch (Exception ex) {
                    log(out, "Error with " + line + ": " + ex.getMessage());
                }
            }

            return list.toArray(new File[list.size()]);
        } catch (IOException ex) {
            log(out, "FileDrop: IOException");
        }
        return new File[0];
    }

    private void makeDropTarget(final PrintStream out, final Component c, boolean recursive) {
        final DropTarget dt = new DropTarget();
        try {
            dt.addDropTargetListener(dropListener);
        }
        catch (TooManyListenersException e) {
            e.printStackTrace();
            log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?");
        }

        c.addHierarchyListener(new HierarchyListener() {

            @Override
            public void hierarchyChanged(HierarchyEvent evt) {
                log(out, "FileDrop: Hierarchy changed.");
                Component parent = c.getParent();
                if (parent == null) {
                    c.setDropTarget(null);
                    log(out, "FileDrop: Drop target cleared from component.");
                }
                else {
                    new DropTarget(c, dropListener);
                    log(out, "FileDrop: Drop target added to component.");
                }
            }
        });
        if (c.getParent() != null) {
            new DropTarget(c, dropListener);
        }

        if (recursive && (c instanceof Container)) {
            Container cont = (Container) c;
            Component[] comps = cont.getComponents();

            for (int i = 0; i < comps.length; i++) {
                makeDropTarget(out, comps[i], recursive);
            }
        }
    }

    private boolean isDragOk(final PrintStream out, final DropTargetDragEvent evt) {
        boolean ok = false;

        DataFlavor[] flavors = evt.getCurrentDataFlavors();

        int i = 0;
        while (!ok && i < flavors.length) {
            final DataFlavor curFlavor = flavors[i];
            if (curFlavor.equals(DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader()) {
                ok = true;
            }
            i++;
        }

        if (out != null) {
            if (flavors.length == 0) {
                log(out, "FileDrop: no data flavors.");
            }
            for (i = 0; i < flavors.length; i++) {
                log(out, flavors[i].toString());
            }
        }

        return ok;
    }

    private static void log(PrintStream out, String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public static boolean remove(Component c) {
        return remove(null, c, true);
    }

    public static boolean remove(PrintStream out, Component c, boolean recursive) {
        if (supportsDnD()) {
            log(out, "FileDrop: Removing drag-and-drop hooks.");
            c.setDropTarget(null);
            if (recursive && (c instanceof Container)) {
                Component[] comps = ((Container) c).getComponents();
                for (int i = 0; i < comps.length; i++) {
                    remove(out, comps[i], recursive);
                }
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}