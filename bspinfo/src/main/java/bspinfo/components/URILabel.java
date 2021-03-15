package bspinfo.components;

import bsplib.log.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;

public final class URILabel extends JLabel {

    private static final Logger L = LogUtils.getLogger();
    private URI uri;

    public URILabel() {
        super(" ");
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new URLOpenAdapter());
    }

    public void setURI(String text, URI uri) {
        this.uri = uri;
        super.setText("<html><body><a href=\"" + uri + "\">" + text + "</a></body></html>");
    }

    public void setURI(String text, String uriString) {
        try {
            setURI(text, new URI(uriString));
        } catch (URISyntaxException ex) {
            L.log(Level.WARNING, "Invalid URI format", ex);
            setText(text);
        }
    }

    private final class URLOpenAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ex) {
                    L.log(Level.WARNING, "Can't browse URI", ex);
                }
            }
        }
    }
}