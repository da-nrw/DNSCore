package de.uzk.hki.da.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uzk.hki.da.gui.Gui;
import de.uzk.hki.da.gui.GuiMessageWriter;
import de.uzk.hki.da.gui.GuiTheme;
import de.uzk.hki.da.gui.ThemeButton;
import de.uzk.hki.da.utils.Utilities;

/**
 * UebertragenPanel: Modernes Panel fuer die Uebertragung an das DNS System.
 * Uses modern layout managers and GuiTheme styling.
 */
public class UebertragenPanel extends JPanel {

    private static final long serialVersionUID = 7528784596782945976L;
    private static Logger logger = LogManager.getLogger(UebertragenPanel.class);

    private static String FILENAME = "SipUebertragen.xml";

    private static final String TEST_SYSTEM_TXT = "Testsystem";
    private static final String PROD_SYSTEM_TXT = "Produktivsystem";
    private static final String BITTE_WAEHLEN = "--- Bitte waehlen ---";
    private static final String TEST_SYSTEM = "da-nrw-q.lvr.de";
    private static final String PROD_SYSTEM = "da-nrw.lvr.de";

    private JLabel userNameLabel;
    private JLabel passwordLabel;
    private JLabel sipPathLabel;
    private JLabel winScpLabel;
    private JLabel responseLabel;
    private JLabel hostNameLabel;
    private JLabel proxyHostNameLabel;
    private JLabel proxyPortLabel;
    private JLabel proxyUserNameLabel;
    private JLabel proxyPasswordLabel;

    private JTextField userNameTextField;
    private JTextField sipPathTextField;
    private JTextField winScpPathTextField;
    private JTextField responsePathTextField;
    private JTextField proxyHostNameTextField;
    private JTextField proxyPortTextField;
    private JTextField proxyUserTextField;
    private JPanel proxyPanel;

    private String hostnameUebertragen = "";
    private JComboBox<String> hostnameCombo;
    private String[] hostnames = { BITTE_WAEHLEN, TEST_SYSTEM_TXT, PROD_SYSTEM_TXT };

    private final SecureRandom rng = new SecureRandom();
    private JPasswordField passwordTextField;
    private JPasswordField proxyPasswordTextField;

    private URL dotsButtonImage;
    private JButton sipPathChooserButton;
    private JFileChooser sipPathChooser;
    private JCheckBox proxyUsedCeckBox;
    private JButton winScpPathChooserButton;
    private JFileChooser winScpPathChooser;
    private JButton responsePathChooserButton;
    private JFileChooser responsePathChooser;

    private JPanel buttonSpeichernLadenPanel;
    private JButton speichernButton;
    private JButton ladenButton;
    private JButton uebertragenButton;

    private Font standardFont;
    private Font boldFont;

    private Gui gui;
    private String dataFolderPath;
    private String scriptFolderPath;

    private GuiMessageWriter messageWriter = new GuiMessageWriter();

    public UebertragenPanel(Gui guiIn, String dataFolderPathIn, String scriptFolderPathIn, boolean enabled,
            ClassLoader classloader, Font standardFontIn, Font boldFontIn) {
        this.setEnabled(enabled);
        this.setVisible(false);
        this.dotsButtonImage = classloader.getResource("images/dotsButton.png");
        this.standardFont = standardFontIn;
        this.boldFont = boldFontIn;
        this.gui = guiIn;
        this.dataFolderPath = dataFolderPathIn;
        this.scriptFolderPath = scriptFolderPathIn;

        setLayout(new BorderLayout());
        setBackground(GuiTheme.BG_MAIN);
        buildContent();
        createListeners();
        proxyPanel.setVisible(false);
    }

    public void defineElementsPosition(int startPanelWidth, int startPanelHeight, JLabel backgroundUebertragenImageLabel) {
        // Modern layout handles positioning automatically
    }

    private void buildContent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(GuiTheme.BG_CARD);
        mainPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER_LIGHT, 1),
                new EmptyBorder(GuiTheme.PADDING_XL, GuiTheme.PADDING_XL, GuiTheme.PADDING_XL, GuiTheme.PADDING_XL)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Contractor
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        mainPanel.add(createLabel("Contractor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        userNameTextField = createTextField();
        mainPanel.add(userNameTextField, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createLabel("Passwort:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordTextField = new JPasswordField(30);
        passwordTextField.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        passwordTextField.setBorder(GuiTheme.createInputBorder());
        passwordTextField.setPreferredSize(GuiTheme.inputFieldSize());
        mainPanel.add(passwordTextField, gbc);
        row++;

        // SIP Path
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createLabel("Verzeichnis der SIPs:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        sipPathTextField = createTextField();
        mainPanel.add(sipPathTextField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        sipPathChooser = new JFileChooser(new File("."));
        sipPathChooserButton = createChooserButton();
        mainPanel.add(sipPathChooserButton, gbc);
        row++;

        // WinSCP Path
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        mainPanel.add(createLabel("Pfad zum WinSCP:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        winScpPathTextField = createTextField();
        mainPanel.add(winScpPathTextField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        winScpPathChooser = new JFileChooser(new File("."));
        winScpPathChooserButton = createChooserButton();
        mainPanel.add(winScpPathChooserButton, gbc);
        row++;

        // Response Path
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        mainPanel.add(createLabel("Pfad zum Response-Ordner:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        responsePathTextField = createTextField();
        mainPanel.add(responsePathTextField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        responsePathChooser = new JFileChooser(new File("."));
        responsePathChooserButton = createChooserButton();
        mainPanel.add(responsePathChooserButton, gbc);
        row++;

        // Zielsystem
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        mainPanel.add(createLabel("Zielsystem:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        hostnameCombo = new JComboBox<String>(hostnames);
        hostnameCombo.setEditable(true);
        hostnameCombo.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        hostnameCombo.setForeground(GuiTheme.TEXT_PRIMARY);
        hostnameCombo.setBackground(GuiTheme.BG_INPUT);
        hostnameCombo.setBorder(GuiTheme.createInputBorder());
        hostnameCombo.setPreferredSize(GuiTheme.inputFieldSize());
        mainPanel.add(hostnameCombo, gbc);
        row++;

        // Proxy checkbox
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        proxyUsedCeckBox = new JCheckBox("Proxy-Einstellungen verwenden");
        proxyUsedCeckBox.setOpaque(false);
        proxyUsedCeckBox.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        proxyUsedCeckBox.setForeground(GuiTheme.TEXT_PRIMARY);
        mainPanel.add(proxyUsedCeckBox, gbc);
        gbc.gridwidth = 1;
        row++;

        // Proxy panel
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        proxyPanel = createProxyPanel();
        mainPanel.add(proxyPanel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Spacer
        gbc.gridy = row; gbc.weighty = 1.0;
        mainPanel.add(Box.createVerticalGlue(), gbc);
        row++;

        // Save/Load buttons
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        buttonSpeichernLadenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonSpeichernLadenPanel.setBackground(GuiTheme.BG_CARD);
        buttonSpeichernLadenPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER_DEFAULT, 1),
                "Einstellungen speichern und laden",
                TitledBorder.LEFT, TitledBorder.TOP,
                GuiTheme.getFont(GuiTheme.FONT_SIZE_MD), GuiTheme.TEXT_SECONDARY));
        ladenButton = createStyledButton("Laden");
        speichernButton = createStyledButton("Speichern");
        buttonSpeichernLadenPanel.add(ladenButton);
        buttonSpeichernLadenPanel.add(speichernButton);
        mainPanel.add(buttonSpeichernLadenPanel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Transfer button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        uebertragenButton = createPrimaryButton("Uebertragen starten");
        mainPanel.add(uebertragenButton, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createProxyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GuiTheme.BG_MAIN);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER_DEFAULT, 1),
                "Proxy-Einstellungen",
                TitledBorder.LEFT, TitledBorder.TOP,
                GuiTheme.getFont(GuiTheme.FONT_SIZE_MD), GuiTheme.TEXT_SECONDARY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        proxyHostNameLabel = createLabel("Proxy Host:");
        proxyHostNameTextField = createTextField();
        proxyPortLabel = createLabel("Proxy Port:");
        proxyPortTextField = createTextField();
        proxyUserNameLabel = createLabel("Proxy User:");
        proxyUserTextField = createTextField();
        proxyPasswordLabel = createLabel("Proxy Passwort:");
        proxyPasswordTextField = new JPasswordField();
        proxyPasswordTextField.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        proxyPasswordTextField.setBorder(GuiTheme.createInputBorder());

        gbc.gridx = 0; gbc.gridy = 0; panel.add(proxyHostNameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(proxyHostNameTextField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(proxyPortLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(proxyPortTextField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(proxyUserNameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(proxyUserTextField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(proxyPasswordLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(proxyPasswordTextField, gbc);

        return panel;
    }

    private void createListeners() {
        sipPathChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourcePath = gui.searchFolder(sipPathChooser);
                if (sourcePath != null) {
                    sipPathTextField.setText(sourcePath);
                    try {
                        Utilities.writeFile(new File(dataFolderPath + File.separator + "srcPath.sav"), sourcePath);
                    } catch (Exception ex) {
                        logger.warn("Failed to save sipPath", ex);
                    }
                }
            }
        });

        winScpPathChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String winScpPath = gui.searchFolder(winScpPathChooser);
                if (winScpPath != null) {
                    winScpPathTextField.setText(winScpPath);
                    try {
                        Utilities.writeFile(new File(dataFolderPath + File.separator + "winScpPath.sav"), winScpPath);
                    } catch (Exception ex) {
                        logger.warn("Failed to save winScpPath", ex);
                    }
                }
            }
        });

        responsePathChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String responsePath = gui.searchFolder(responsePathChooser);
                if (responsePath != null) {
                    responsePathTextField.setText(responsePath);
                    try {
                        Utilities.writeFile(new File(dataFolderPath + File.separator + "responsePath.sav"), responsePath);
                    } catch (Exception ex) {
                        logger.warn("Failed to save responsePath", ex);
                    }
                }
            }
        });

        hostnameCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ausgewaehlt = hostnameCombo.getSelectedItem().toString();
                if (ausgewaehlt.equals(TEST_SYSTEM_TXT)) {
                    hostnameUebertragen = TEST_SYSTEM;
                } else if (ausgewaehlt.equals(PROD_SYSTEM_TXT)) {
                    hostnameUebertragen = PROD_SYSTEM;
                }
            }
        });

        uebertragenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] password = passwordTextField.getPassword();
                char[] proxyPassword = proxyPasswordTextField.getPassword();
                String keyBase64 = "";
                String passwordContractorEnc = "";
                String passwordProxyUserEnc = "";

                if (password.length == 0) {
                    messageWriter.showMessage("Bitte geben Sie ein Passwort fuer den Contractor ein.",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (proxyUsedCeckBox.isSelected() && proxyPassword.length == 0) {
                    messageWriter.showMessage("Bitte geben Sie ein Passwort fuer den Proxy-User ein.",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    SecretKey aesKey = generateAesKey();
                    keyBase64 = Base64.getEncoder().encodeToString(aesKey.getEncoded());
                    passwordContractorEnc = encryptPassword(password, aesKey);
                    passwordProxyUserEnc = encryptPassword(proxyPassword, aesKey);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showThemedMessage("Fehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                } finally {
                    Arrays.fill(password, '\0');
                    Arrays.fill(proxyPassword, '\0');
                }

                if (checkInputFields()) {
                    String os = System.getProperty("os.name").toLowerCase();
                    boolean isWindow = os.contains("win");
                    String shell = isWindow ? "powershell.exe" : "pwsh";

                    if (!scriptFolderPath.endsWith(".ps1")) {
                        scriptFolderPath = scriptFolderPath + "\\createAndTransfer.ps1";
                    }

                    List<String> scriptParams = new ArrayList<String>();
                    scriptParams.add(shell);
                    scriptParams.add("-NoProfile");
                    scriptParams.add("-ExecutionPolicy");
                    scriptParams.add("Bypass");
                    scriptParams.add("-File");
                    scriptParams.add(scriptFolderPath);
                    scriptParams.add(sipPathTextField.getText().trim());
                    scriptParams.add(winScpPathTextField.getText().trim());
                    scriptParams.add(hostnameUebertragen);
                    scriptParams.add(userNameTextField.getText().trim());
                    scriptParams.add(passwordContractorEnc);
                    scriptParams.add(responsePathTextField.getText().trim());
                    scriptParams.add(proxyHostNameTextField.getText().trim());
                    scriptParams.add(proxyPortTextField.getText().trim());
                    scriptParams.add(proxyUserTextField.getText().trim());
                    scriptParams.add(passwordProxyUserEnc);
                    scriptParams.add(keyBase64);

                    ProcessBuilder processBuilder = new ProcessBuilder(scriptParams);
                    processBuilder.redirectErrorStream(true);

                    ScriptOutputDialog dlg = new ScriptOutputDialog(
                            SwingUtilities.getWindowAncestor(uebertragenButton), "PowerShell Output");
                    dlg.setVisible(true);
                    dlg.appendLine("Starte Script: " + scriptFolderPath);
                    dlg.runProcess(processBuilder);
                }
            }
        });

        ladenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = savePropertiesFile(FILENAME, "Laden");
                File file = new File(filename);
                try {
                    if (!file.exists()) {
                        showThemedMessage("Keine Datei gefunden: " + filename, "Fehler", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Properties p = loadPropertiesFromXmlFile(file);
                    userNameTextField.setText(p.getProperty("user", ""));
                    sipPathTextField.setText(p.getProperty("sip", ""));
                    winScpPathTextField.setText(p.getProperty("winScp", ""));
                    responsePathTextField.setText(p.getProperty("reponse", ""));
                    proxyHostNameTextField.setText(p.getProperty("proxyHostName", ""));
                    proxyPortTextField.setText(p.getProperty("proxyPort", ""));
                    proxyUserTextField.setText(p.getProperty("proxyUser", ""));
                } catch (IOException ex) {
                    showThemedMessage("Fehler beim Laden: " + ex.getMessage(),
                            "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        speichernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = savePropertiesFile(FILENAME, "Speichern");
                File file = new File(filename);
                try {
                    savePropertiesToXmlFile(file, userNameTextField.getText(), sipPathTextField.getText(),
                            winScpPathTextField.getText(), responsePathTextField.getText(),
                            proxyHostNameTextField.getText(), proxyPortTextField.getText(),
                            proxyUserTextField.getText());
                    showThemedMessage("Gespeichert: " + filename, "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    showThemedMessage("Fehler beim Speichern: " + ex.getMessage(),
                            "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        proxyUsedCeckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                proxyPanel.setVisible(proxyUsedCeckBox.isSelected());
            }
        });
    }

    private static String encryptPassword(char[] password, SecretKey key) throws Exception {
        byte[] plainBytes = new String(password).getBytes(StandardCharsets.UTF_8);
        try {
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));
            byte[] cipherBytes = cipher.doFinal(plainBytes);
            byte[] combined = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } finally {
            Arrays.fill(plainBytes, (byte) 0);
        }
    }

    private static SecretKey generateAesKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    private Properties loadPropertiesFromXmlFile(File file) throws IOException {
        Properties p = new Properties();
        InputStream is = new FileInputStream(file);
        try {
            p.loadFromXML(is);
        } finally {
            is.close();
        }
        return p;
    }

    private void savePropertiesToXmlFile(File file, String userName, String sipPath, String winScpPath,
            String responsePath, String proxyHost, String proxyPort, String proxyUser) throws IOException {
        Properties p = new Properties();
        p.setProperty("user", safe(userName));
        p.setProperty("sip", safe(sipPath));
        p.setProperty("winScp", safe(winScpPath));
        p.setProperty("reponse", safe(responsePath));
        p.setProperty("proxyHostName", safe(proxyHost));
        p.setProperty("proxyPort", safe(proxyPort));
        p.setProperty("proxyUser", safe(proxyUser));
        OutputStream os = new FileOutputStream(file);
        try {
            p.storeToXML(os, "GUI Eingaben", StandardCharsets.UTF_8.name());
        } finally {
            os.close();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private String savePropertiesFile(String defaultFileName, String approveButtonText) {
        JFileChooser einstellungenSavePathChooser = new JFileChooser(new File("."));
        einstellungenSavePathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        einstellungenSavePathChooser.setAcceptAllFileFilterUsed(false);
        if (einstellungenSavePathChooser.getSelectedFile() == null)
            einstellungenSavePathChooser.setSelectedFile(new File(defaultFileName));
        einstellungenSavePathChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".xml");
            }
            public String getDescription() {
                return "XML-Datei (*.xml)";
            }
        });
        if (einstellungenSavePathChooser.showDialog(this, approveButtonText) == JFileChooser.APPROVE_OPTION) {
            String filePath = einstellungenSavePathChooser.getSelectedFile().getPath();
            if (!filePath.endsWith(".xml")) filePath += ".xml";
            return filePath;
        }
        return null;
    }

    private boolean checkInputFields() {
        if (userNameTextField.getText() == null || userNameTextField.getText().equals("")) {
            messageWriter.showMessage("Bitte geben Sie Ihren Contractor-Namen an.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (sipPathTextField.getText() == null || sipPathTextField.getText().equals("")) {
            messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem SIP-Ordner an.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (winScpPathTextField.getText() == null || winScpPathTextField.getText().equals("")) {
            messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem WinSCP an.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (responsePathTextField.getText() == null || responsePathTextField.getText().equals("")) {
            messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem Response-Ordner an.",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (hostnameCombo.getSelectedItem().toString().equals(BITTE_WAEHLEN)) {
            messageWriter.showMessage("Bitte waehlen Sie das Zielsystem aus.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (proxyUsedCeckBox.isSelected()) {
            try {
                int port = Integer.parseInt(proxyPortTextField.getText());
                if (port < 1 || port > 65535) {
                    messageWriter.showMessage("Ungueltiger Port!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException nfe) {
                messageWriter.showMessage("Ungueltiger Port!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public boolean leaveSection() {
        this.setVisible(false);
        return true;
    }

    // -- Helper methods for modern UI --

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_MD));
        label.setForeground(GuiTheme.TEXT_PRIMARY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setEditable(true);
        field.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
        field.setBorder(GuiTheme.createInputBorder());
        field.setPreferredSize(GuiTheme.inputFieldSize());
        return field;
    }

    private JButton createChooserButton() {
        ThemeButton button = new ThemeButton(ThemeButton.Style.SECONDARY, "...");
        button.setPreferredSize(new Dimension(40, GuiTheme.INPUT_HEIGHT));
        return button;
    }

    private JButton createStyledButton(String text) {
        ThemeButton button = new ThemeButton(ThemeButton.Style.SECONDARY, text);
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private JButton createPrimaryButton(String text) {
        ThemeButton button = new ThemeButton(ThemeButton.Style.PRIMARY, text);
        button.setPreferredSize(new Dimension(200, GuiTheme.BUTTON_HEIGHT));
        return button;
    }

    private void showThemedMessage(String message, String title, int messageType) {
        ThemeButton okButton = new ThemeButton(ThemeButton.Style.PRIMARY, "OK");
        okButton.setPreferredSize(GuiTheme.buttonSize());
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION,
                null, new Object[]{okButton}, okButton);
        JDialog dialog = pane.createDialog(this, title);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pane.setValue(okButton);
            }
        });
        dialog.setVisible(true);
    }
}
