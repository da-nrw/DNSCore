/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.gui.panels;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ScriptOutputDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JTextArea textArea = new JTextArea(20, 80);
    private final JButton closeButton = new JButton("Schließen");

    /**
     * 
     * @param owner
     * @param title
     */
    public ScriptOutputDialog(Window owner, String title) {
        super(owner, title, ModalityType.MODELESS);

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);

        closeButton.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(closeButton);

        setLayout(new BorderLayout(8, 8));
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * 
     * @param line
     */
    public void appendLine(String line) {
        textArea.append(line + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // autoscroll
    }

    /**
     * 
     * @param processBuilder
     */
    public void runProcess(ProcessBuilder processBuilder) {
        closeButton.setEnabled(false);

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
            	processBuilder.redirectErrorStream(true); // stderr+stdout zusammen
                Process p = processBuilder.start();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        publish(line); // geht ins process() im EDT
                    }
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
