package de.uzk.hki.da.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.sb.MessageWriter;

/**
 * A specialized message writer responsible for displaying info messages and dialogs in GUI mode.
 * Uses modern styling consistent with GuiTheme.
 *
 * @author Thomas Kleinke
 * @author Polina Gubaidullina
 */
public class GuiMessageWriter extends MessageWriter {

    private Component gui;
    private Image iconImage;

    @Override
    public void showMessage(String message) {
        showMessage(message, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message, int type) {
        String title = SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder";
        ThemeButton okButton = new ThemeButton(ThemeButton.Style.PRIMARY, "OK");
        okButton.setPreferredSize(GuiTheme.buttonSize());
        showThemedDialog(gui, message, title, type, new ThemeButton[]{okButton});
    }

    @Override
    public void showLongErrorMessage(String message) {
        String prettyMsg = message.replace(",", ",\n");
        JTextArea textArea = new JTextArea(5, 40);
        textArea.setText(prettyMsg);
        textArea.setEditable(false);
        textArea.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_SM,
                GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(GuiTheme.BORDER_DEFAULT, 1));
        scrollPane.setPreferredSize(new Dimension(400, 150));
        ThemeButton okButton = new ThemeButton(ThemeButton.Style.PRIMARY, "OK");
        okButton.setPreferredSize(GuiTheme.buttonSize());
        showThemedDialog(gui, scrollPane,
                SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder",
                JOptionPane.ERROR_MESSAGE, new ThemeButton[]{okButton});
    }

    @Override
    public UserInput showOverwriteDialog(String message) {
        ThemeButton yesButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Ja");
        ThemeButton noButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Nein");
        ThemeButton alwaysButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Alle ueberschreiben");
        ThemeButton[] options = new ThemeButton[] { yesButton, noButton, alwaysButton };
        int answer = showThemedDialog(gui, message,
                SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder",
                JOptionPane.INFORMATION_MESSAGE, options);
        switch (answer) {
            case 0: return UserInput.YES;
            case 1: return UserInput.NO;
            case 2: return UserInput.ALWAYS_OVERWRITE;
            default: return UserInput.NO;
        }
    }

    @Override
    public UserInput showCollectionOverwriteDialog(String message) {
        ThemeButton yesButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Ja");
        ThemeButton noButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Nein");
        ThemeButton[] options = new ThemeButton[] { yesButton, noButton };
        int answer = showThemedDialog(gui, message,
                SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder",
                JOptionPane.INFORMATION_MESSAGE, options);
        switch (answer) {
            case 0: return UserInput.YES;
            case 1: return UserInput.NO;
            default: return UserInput.NO;
        }
    }

    public void showZeroByteFileMessage() {
        Collections.sort(zeroByteFiles);

        String sipName = zeroByteFiles.get(0).substring(
                zeroByteFiles.get(0).lastIndexOf('(') + 1,
                zeroByteFiles.get(0).lastIndexOf(')'));

        String message = "";
        if (zeroByteFiles.size() == 1) {
            message = "Bei der Erstellung des SIPs " + sipName + " wurde eine Datei\n"
                    + "der Groesse 0 Byte gefunden:\n\n"
                    + zeroByteFiles.get(0).substring(0, zeroByteFiles.get(0).lastIndexOf('('))
                    + "\n\nBitte ueberpruefen Sie die Datei.";
        } else if (zeroByteFiles.size() < 11) {
            message = "Bei der Erstellung des SIPs " + sipName
                    + " wurden Dateien\nder Groesse 0 Byte gefunden:\n\n";
            for (String s : zeroByteFiles) {
                message += s.substring(0, s.lastIndexOf('('));
                message += "\n";
            }
            message += "\nBitte ueberpruefen Sie die Dateien.";
        } else {
            message = "Bei der Erstellung des SIPs " + sipName
                    + " wurden Dateien\nder Groesse 0 Byte gefunden:\n\n";
            for (int i = 0; i < 10; i++) {
                message += zeroByteFiles.get(i).substring(0, zeroByteFiles.get(i).lastIndexOf('('));
                message += "\n";
            }
            message += "und " + String.valueOf(zeroByteFiles.size() - 10) + " weitere Dateien.\n\n";
            message += "Bitte ueberpruefen Sie Ihre Daten.";
        }

        ThemeButton okButton = new ThemeButton(ThemeButton.Style.PRIMARY, "OK");
        okButton.setPreferredSize(GuiTheme.buttonSize());
        showThemedDialog(gui, message,
                SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder",
                JOptionPane.ERROR_MESSAGE, new ThemeButton[]{okButton});
    }

    public void setGui(Component gui) {
        this.gui = gui;
    }

    public void setIconImage(Image iconImage) {
        this.iconImage = iconImage;
    }

    /**
     * Shows a dialog with themed buttons.
     * For info dialogs (single OK button), the return value can be ignored.
     * For selection dialogs, returns the index of the clicked button or CLOSED_OPTION.
     */
    private int showThemedDialog(Component parent, Object message, String title,
            int messageType, ThemeButton[] options) {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION,
                iconImage != null ? new ImageIcon(iconImage) : null,
                options, options.length > 0 ? options[0] : null);
        JDialog dialog = pane.createDialog(parent, title);
        for (ThemeButton button : options) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pane.setValue(button);
                }
            });
        }
        dialog.setVisible(true);
        Object value = pane.getValue();
        if (value == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i] == value) {
                return i;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

    @Override
    public UserInput showWrongReferencesInMetadataDialog(String message) {
        String prettyMsg = message.replace(",", ",\n");
        JTextArea textArea = new JTextArea(5, 40);
        textArea.setText(prettyMsg);
        textArea.setEditable(false);
        textArea.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_SM,
                GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(GuiTheme.BORDER_DEFAULT, 1));
        scrollPane.setPreferredSize(new Dimension(400, 150));

        ThemeButton yesButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Ja");
        ThemeButton noButton = new ThemeButton(ThemeButton.Style.PRIMARY, "Nein");
        ThemeButton[] options = new ThemeButton[] { yesButton, noButton };
        int answer = showThemedDialog(gui, scrollPane,
                SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder",
                JOptionPane.INFORMATION_MESSAGE, options);
        switch (answer) {
            case 0: return UserInput.YES;
            case 1: return UserInput.NO;
            default: return UserInput.NO;
        }
    }
}
