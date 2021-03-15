package bspinfo.components;

import java.awt.event.*;
import javax.swing.*;

public final class ReadOnlyCheckBox extends JCheckBox {

    public ReadOnlyCheckBox(String text) {
        super(text);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {}

    @Override
    protected void processMouseEvent(MouseEvent e) {}
}