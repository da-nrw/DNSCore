package de.uzk.hki.da.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.uzk.hki.da.gui.GuiTheme;

/**
 * HistoriePanel: Container fuer die Historie der eingelieferten SIPs.
 * Der Inhalt wird von der Gui-Klasse aufgebaut (modernes Card-Layout).
 */
public class HistoriePanel extends JPanel {

    private static final long serialVersionUID = 2201673485393042516L;

    public HistoriePanel(boolean enabled, ClassLoader classloader) {
        this.setEnabled(enabled);
        setBackground(GuiTheme.BG_MAIN);
    }

    public void defineElementsPosition(int startPanelWidth, int startPanelHeight, int jhOffsetY,
            JLabel backgroundHistorieImageLabel) {
        // Modern layout handles positioning automatically
    }
}
