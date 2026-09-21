package de.uzk.hki.da.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import de.uzk.hki.da.gui.GuiTheme;
import de.uzk.hki.da.gui.ThemeButton;

/**
 * Modernes Dialogfenster fuer die Anzeige von Script-Ausgaben.
 * Zeigt PowerShell/Skript-Ausgaben in einem formatierten Bereich an.
 */
public class ScriptOutputDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextArea textArea;
    private final JButton closeButton;

    public ScriptOutputDialog(Window owner, String title) {
        super(owner, title, ModalityType.MODELESS);

        textArea = new JTextArea(20, 80);
        textArea.setEditable(false);
        textArea.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(new Color(200, 210, 200));
        textArea.setCaretColor(new Color(200, 210, 200));
        textArea.setBorder(new EmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_SM,
                GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(GuiTheme.BORDER_DEFAULT, 1));

        closeButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Schliessen");
        //closeButton.setRenderActiveWhenDisabled(true);
        closeButton.setPreferredSize(GuiTheme.buttonSize());
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, GuiTheme.PADDING_MD, GuiTheme.PADDING_SM));
        bottom.setBackground(GuiTheme.BG_FOOTER);
        bottom.add(closeButton);

        setLayout(new BorderLayout(GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setBackground(GuiTheme.BG_CARD);
        pack();
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(owner);
    }

    public void appendLine(String line) {
        textArea.append(line + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void runProcess(ProcessBuilder processBuilder) {
        closeButton.setEnabled(false);

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                processBuilder.redirectErrorStream(true);
                Process p = processBuilder.start();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        publish(line);
                    }
                } finally {
                    br.close();
                }
                return p.waitFor();
            }

            @Override
            protected void process(List<String> chunks) {
                for (String s : chunks) {
                    appendLine(s);
                }
            }

            @Override
            protected void done() {
                try {
                    int exit = get();
                    appendLine("---- Fertig. ExitCode=" + exit + " ----");
                } catch (Exception ex) {
                    appendLine("---- Fehler: " + ex.getMessage() + " ----");
                } finally {
                    closeButton.setEnabled(true);
                }
            }
        }.execute();
    }
}
