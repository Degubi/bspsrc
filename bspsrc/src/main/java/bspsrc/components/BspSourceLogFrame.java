package bspsrc.components;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import javax.swing.*;

public final class BspSourceLogFrame extends JFrame {

    private final TextAreaHandler handler;

    public BspSourceLogFrame() {
        initComponents();
        handler = new TextAreaHandler(messageTextArea, errorTextArea);
        handler.setFormatter(new ConsoleFormatter());
    }

    public void setLogging(boolean logging) {
        Logger log = Logger.getLogger("");
        if (logging) {
            log.addHandler(handler);
        } else {
            log.removeHandler(handler);
        }
    }

    public void clear() {
        messageTextArea.setText("");
        errorTextArea.setText("");
    }

    private void initComponents() {
        messageScrollPane = new JScrollPane();
        messageTextArea = new JTextArea();
        errorScrollPane = new JScrollPane();
        errorTextArea = new JTextArea();

        setTitle("BSPSource output");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(160, 256));

        messageScrollPane.setBorder(BorderFactory.createTitledBorder("Progress"));

        messageTextArea.setEditable(false);
        messageTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        messageTextArea.setRows(1);
        messageTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        messageScrollPane.setViewportView(messageTextArea);

        errorScrollPane.setBorder(BorderFactory.createTitledBorder("Errors & Warnings"));

        errorTextArea.setEditable(false);
        errorTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        errorTextArea.setForeground(new java.awt.Color(153, 0, 0));
        errorTextArea.setLineWrap(true);
        errorTextArea.setRows(1);
        errorTextArea.setWrapStyleWord(true);
        errorTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        errorScrollPane.setViewportView(errorTextArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(errorScrollPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                    .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorScrollPane, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private JScrollPane errorScrollPane;
    private JTextArea errorTextArea;
    private JScrollPane messageScrollPane;
    private JTextArea messageTextArea;

    static final class ConsoleFormatter extends Formatter {

        private static final Map<Level, String> LEVEL_PREFIX = Map.of(Level.CONFIG,  "[config]",
                Level.FINE,    "[debug]",
                Level.FINER,   "[debug]",
                Level.FINEST,  "[trace]",
                Level.INFO,    "[info]",
                Level.SEVERE,  "[error]",
                Level.WARNING, "[warning]");

        @Override
        public String format(LogRecord record) {
            var sb = new StringBuilder();
            sb.append(LEVEL_PREFIX.get(record.getLevel()));
            sb.append(' ');

            var classNameParts = record.getLoggerName().split("\\.");
            if (record.getLevel() != Level.INFO && classNameParts.length != 0) {
                sb.append(classNameParts[classNameParts.length - 1]);
                sb.append(": ");
            }

            sb.append(formatMessage(record));

            var thrown = record.getThrown();
            if (thrown != null) {
                sb.append(", caused by ");

            var sw = new StringWriter();
            try(var pw = new PrintWriter(sw)) {
                thrown.printStackTrace(pw);
            }
                sb.append(sw.toString());
            }

            sb.append('\n');

            return sb.toString();
        }
    }
}