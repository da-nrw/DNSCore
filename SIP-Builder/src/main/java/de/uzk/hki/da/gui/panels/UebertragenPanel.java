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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uzk.hki.da.gui.Gui;
import de.uzk.hki.da.gui.GuiMessageWriter;
import de.uzk.hki.da.utils.Utilities;

/**
 * UebertragenPanel: Innerhalb dieses Panels wird die Übertragung an das DNS
 * System vorbereitet und durchgeführt
 */
public class UebertragenPanel extends JPanel {

	private static final long serialVersionUID = 7528784596782945976L;
	private static Logger logger = LogManager.getLogger(UebertragenPanel.class);

	private static String FILENAME = "SipUebertragen.xml";

//	private int startPanelWidth = 760;
//	private int startPanelHeight = 566;
//	private int jhOffsetY = 40;

	// Labels
	private JLabel backgroundImageLabel;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JLabel sipPathLabel;
	private JLabel winScpLabel;
	private JLabel responseLabel;
	private JLabel hostNameLabel;

	// Eingabefelder
	private JTextField userNameTextField;
	private JTextField sipPathTextField;
	private JTextField winScpPathTextField;
	private JTextField responsePathTextField;

	// Combobox
	private static final String TEST_SYSTEM_TXT = "Testsystem";
	private static final String PROD_SYSTEM_TXT = "Produktivsystem";
	private static final String BITTE_WAEHLEN = "--- Bitte wählen ---";
	private static final String TEST_SYSTEM = "da-nrw-q.lvr.de";
	private static final String PROD_SYSTEM = "da-nrw.lvr.de";

	private String hostnameUebertragen = "";
	private JComboBox<String> hostnameCombo;
	private String[] hostnames = { BITTE_WAEHLEN, TEST_SYSTEM_TXT, PROD_SYSTEM_TXT };

	// Passwort-Feld
	private final SecureRandom rng = new SecureRandom();
	private JPasswordField passwordTextField;

	// Buttons
	// 3 Punkte
	private URL dotsButtonImage;

	// Sip-auswählen
	private JButton sipPathChooserButton;
	private JFileChooser sipPathChooser;

	// Pfad zu Winscp
	private JButton winScpPathChooserButton;
	private JFileChooser winScpPathChooser;

	// Response Path
	private JButton responsePathChooserButton;
	private JFileChooser responsePathChooser;

	// Einstellungen speichern und laden
	private JPanel buttonSpeichernLadenPanel;
	private JButton speichernButton;
	private JButton ladenButton;

	// Uebertragen
	private JButton uebertragenButton;
	private String pwdEncrypted = "";

	private Font standardFont;
	private Font boldFont;

	private Gui gui;
	private String dataFolderPath;
	private String scriptFolderPath;

	private GuiMessageWriter messageWriter = new GuiMessageWriter();

	/**
	 * @param dataFolderPath
	 * 
	 */
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
		this.defineContent();
		createListeners();

	}

	/**
	 * Contains all listeners of the panel
	 */
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
						logger.warn(
								"Failed to create file "
										+ new File(dataFolderPath + File.separator + "srcPath.sav").getAbsolutePath(),
								ex);
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
						logger.warn("Failed to create file "
								+ new File(dataFolderPath + File.separator + "winScpPath.sav").getAbsolutePath(), ex);
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
						Utilities.writeFile(new File(dataFolderPath + File.separator + "responsePath.sav"),
								responsePath);
					} catch (Exception ex) {
						logger.warn("Failed to create file "
								+ new File(dataFolderPath + File.separator + "responsePath.sav").getAbsolutePath(), ex);
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
				System.out.println("hostnameUebertragen: " + hostnameUebertragen);
			}
		});

		uebertragenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// encrypt the password if it is filled
				char[] password = passwordTextField.getPassword();
				if (password.length == 0) {
					messageWriter.showMessage("Bitte geben Sie ein Passwort ein.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {

					byte[] salt = new byte[16];
					rng.nextBytes(salt);
					// PBKDF2 mit HMAC-SHA-256 (Iterationszahl je nach Policy erhöhen)
					byte[] hash = hashPassword(password, salt, 150_000, 256);
					// typischerweise: Base64(salt | hash) senden/speichern
					String payload = Base64.getEncoder()
							.encodeToString(ByteBuffer.allocate(salt.length + hash.length).put(salt).put(hash).array());
					pwdEncrypted = payload.substring(0, Math.min(60, payload.length()));
//					JOptionPane.showMessageDialog(UebertragenPanel.this,
//							"Zu sendender Hash (Demo):\n" + payload.substring(0, Math.min(60, payload.length())) + "…");

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler: " + ex.getMessage());
				} finally {
					// delete the password
					Arrays.fill(password, '\0');
				}

				// check if the input Fields are filled
				checkInputFields();

				// execute powershell script
				ProcessBuilder processBuilder = new ProcessBuilder("pwsh", "-File", scriptFolderPath);
				processBuilder.inheritIO(); // gibt die Ausgabe direkt auf der Konsole aus
				Process process;
				try {
					process = processBuilder.start();
					process.waitFor();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}

		});

		ladenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = saveFile(FILENAME);
				File file = new File(filename);

				try {
					if (!file.exists()) {
						JOptionPane.showMessageDialog(UebertragenPanel.this, "Keine Datei gefunden: " + filename);
						return;
					}
					Properties p = loadFromXmlFile(file);
					userNameTextField.setText(p.getProperty("user", ""));
					sipPathTextField.setText(p.getProperty("sip", ""));
					winScpPathTextField.setText(p.getProperty("winScp", ""));
					responsePathTextField.setText(p.getProperty("reponse", ""));
					
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler beim Laden: " + ex.getMessage(), "Fehler",
							JOptionPane.ERROR_MESSAGE);
				}

			}

		});

		speichernButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String filename = saveFile(FILENAME);
				File file = new File(filename);
				try {
					saveToXmlFile(file, userNameTextField.getText(), sipPathTextField.getText(),
							winScpPathTextField.getText(), responsePathTextField.getText());

					JOptionPane.showMessageDialog(UebertragenPanel.this, "Gespeichert: " + filename, "Info",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (IOException ex) {
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler beim Speichern: " + ex.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	private Properties loadFromXmlFile(File file) throws IOException {
		Properties p = new Properties();
		try (InputStream is = new FileInputStream(file)) {
			p.loadFromXML(is);
		}
		return p;
	}

	private void saveToXmlFile(File file, String userName, String sipPath, String winScpPath, String responsePath)
			throws IOException {

		Properties p = new Properties();
		p.setProperty("user", safe(userName));
		p.setProperty("sip", safe(sipPath));
		p.setProperty("winScp", safe(winScpPath));
		p.setProperty("reponse", safe(responsePath));

		try (OutputStream os = new FileOutputStream(file)) {
			// storeToXML schreibt XML; encoding wird im XML-Header gesetzt
			p.storeToXML(os, "GUI Eingaben", StandardCharsets.UTF_8.name());
		}
	}

	private static String safe(String s) {
		return s == null ? "" : s;
	}

	private String saveFile(String defaultFileName) {

		File selectedFile = null;

		JFileChooser einstellungenSavePathChooser;
		einstellungenSavePathChooser = new JFileChooser(new File("."));
		einstellungenSavePathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		einstellungenSavePathChooser.setAcceptAllFileFilterUsed(false);
		if (einstellungenSavePathChooser.getSelectedFile() == null)
			einstellungenSavePathChooser.setSelectedFile(new File(defaultFileName));

		einstellungenSavePathChooser.addChoosableFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				return f.getName().toLowerCase().endsWith(".xml");
			}

			public String getDescription() {
				return "XML-Datei (*.xml)";
			}
		});

		if (einstellungenSavePathChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			selectedFile = einstellungenSavePathChooser.getSelectedFile();
			String filePath = selectedFile.getPath();

			if (!filePath.endsWith(".xml"))
				filePath += ".xml";

			return filePath;
		}

		return null;
	}

	/**
	 * Checks, if all needed Input-Fields where filled
	 */
	private void checkInputFields() {
		String userNameTxt = userNameTextField.getText();
		if (userNameTxt == null || userNameTxt.equals("")) {
			messageWriter.showMessage("Bitte geben Sie Ihren Contractor-Namen an.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String sipPathTxt = sipPathTextField.getText();
		if (sipPathTxt == null || sipPathTxt.equals("")) {
			messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem SIP-Ordner an.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String winScpPathTxt = winScpPathTextField.getText();
		if (winScpPathTxt == null || winScpPathTxt.equals("")) {
			messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem WinScp an.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String responsePathTxt = responsePathTextField.getText();
		if (responsePathTxt == null || responsePathTxt.equals("")) {
			messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem Response-Ordner an.",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String hostNameTxt = hostnameCombo.getSelectedItem().toString();
		if (hostNameTxt.equals(BITTE_WAEHLEN)) {
			messageWriter.showMessage("Bitte wählen Sie das Zielsystem aus.", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private static byte[] hashPassword(char[] password, byte[] salt, int iterations, int keyLenBits) throws Exception {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] out = skf.generateSecret(spec).getEncoded();
		spec.clearPassword();
		return out;
	}

	/**
	 * Defines the content of the panel
	 */
	private void defineContent() {
		// Labels
		userNameLabel = new JLabel("Contractor: ");
		userNameLabel.setFont(boldFont.deriveFont(10.0f));
		passwordLabel = new JLabel("Passworteingabe: ");
		passwordLabel.setFont(boldFont.deriveFont(10.0f));
		sipPathLabel = new JLabel("Verzeichnis der SIP's: ");
		sipPathLabel.setFont(boldFont.deriveFont(10.0f));
		winScpLabel = new JLabel("Pfad zum WinScp: ");
		winScpLabel.setFont(boldFont.deriveFont(10.0f));
		responseLabel = new JLabel("Pfad zum Response Ordner:");
		responseLabel.setFont(boldFont.deriveFont(10.0f));
		hostNameLabel = new JLabel("Zielsystem");
		hostNameLabel.setFont(boldFont.deriveFont(10.0f));

		// Textfields
		userNameTextField = new JTextField();
		userNameTextField.setEditable(true);
		userNameTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		passwordTextField = new JPasswordField(30);
		passwordTextField.setEditable(true);
		passwordTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		sipPathTextField = new JTextField();
		sipPathTextField.setEditable(true);
		sipPathTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		winScpPathTextField = new JTextField();
		winScpPathTextField.setEditable(true);
		winScpPathTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		responsePathTextField = new JTextField();
		responsePathTextField.setEditable(true);
		responsePathTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		// nur zum Testen !!!!!!!!!!!!!!!!!!!
//		userNameTextField.setText("LVR-Test");
//		passwordTextField.setText("gigaset55a");
//		sipPathTextField.setText("/home/gbender/SIPBuilderNeu/In");
//		responsePathTextField.setText("/home/gbender/SIPBuilderNeu/response");
//		winScpPathTextField.setText("/home/gbender/Programme/winscp/");

		// Combobobx
		hostnameCombo = new JComboBox<String>(hostnames);
		hostnameCombo.setEditable(true);
		hostnameCombo.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		// FileChooser
		sipPathChooser = new JFileChooser(new File("."));
		sipPathChooserButton = new JButton(new ImageIcon(dotsButtonImage));

		winScpPathChooser = new JFileChooser(new File("."));
		winScpPathChooserButton = new JButton(new ImageIcon(dotsButtonImage));

		responsePathChooser = new JFileChooser(new File("."));
		responsePathChooserButton = new JButton(new ImageIcon(dotsButtonImage));

		buttonSpeichernLadenPanel = new JPanel();
		// buttonSpeichernLadenPanel.setLayout(new GridLayout(1, 2, 30, 20));
		buttonSpeichernLadenPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		buttonSpeichernLadenPanel.setBorder(new TitledBorder("Einstellungen speichern und laden")); // Label für den
																									// Rahmen

		ladenButton = initializeButton(ladenButton, "laden");
		speichernButton = initializeButton(speichernButton, "speichern");

		buttonSpeichernLadenPanel.add(ladenButton);
		buttonSpeichernLadenPanel.add(speichernButton);

		// Übertragen - Button
		uebertragenButton = initializeButton(uebertragenButton, "Übertragen");


	}

	/**
	 * 
	 */
	private JButton initializeButton(JButton button, String text) {
		button = new JButton();
		button.setText(text);
		button.setFont(standardFont.deriveFont(12.0f));
		button.setBorder(new LineBorder(Color.BLACK, 1));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBackground(Color.LIGHT_GRAY);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setPreferredSize(new Dimension(90, 20));
		return button;
	}

	/**
	 * Defines the position of the elements inside the panel
	 */
	public void defineElementsPosition(int startPanelWidth, int startPanelHeight,
			JLabel backgroundUebertragenImageLabel) {

		int jhOffsetY = 50;

		this.backgroundImageLabel = backgroundUebertragenImageLabel;

		this.setBounds(0, 0, startPanelWidth, startPanelHeight);
		backgroundImageLabel.setBounds(0, 0, startPanelWidth, startPanelHeight);

		userNameLabel.setBounds(255, 145 + jhOffsetY, 170, 20);
		userNameTextField.setBounds(420, 145 + jhOffsetY, 260, 20);

		passwordLabel.setBounds(255, 170 + jhOffsetY, 170, 20);
		passwordTextField.setBounds(420, 170 + jhOffsetY, 260, 20);

		sipPathLabel.setBounds(255, 195 + jhOffsetY, 300, 20);
		sipPathTextField.setBounds(420, 195 + jhOffsetY, 260, 20);
		sipPathChooserButton.setBounds(690, 195 + jhOffsetY, 45, 20);

		winScpLabel.setBounds(255, 220 + jhOffsetY, 430, 20);
		winScpPathTextField.setBounds(420, 220 + jhOffsetY, 260, 20);
		winScpPathChooserButton.setBounds(690, 220 + jhOffsetY, 45, 20);

		responseLabel.setBounds(255, 245 + jhOffsetY, 650, 20);
		responsePathTextField.setBounds(420, 245 + jhOffsetY, 260, 20);
		responsePathChooserButton.setBounds(690, 245 + jhOffsetY, 45, 20);

		hostNameLabel.setBounds(255, 270 + jhOffsetY, 780, 20);
		hostnameCombo.setBounds(420, 270 + jhOffsetY, 260, 20);

		buttonSpeichernLadenPanel.setBounds(255, 330 + jhOffsetY, 300, 50);

//		uebertragenButton.setBounds(400, 390 + jhOffsetY, 90, 20);
		uebertragenButton.setBounds(400, 400 + jhOffsetY, 90, 20);

	}

	/**
	 * dem Panel die Elemente hinzufügen
	 * 
	 * @return JPanel: das gefüllte Panel
	 */
	public JPanel addElementsToContent() {
		this.add(userNameLabel);
		this.add(userNameTextField);
		this.add(passwordLabel);
		this.add(passwordTextField);
		this.add(sipPathLabel);
		this.add(sipPathTextField);
		this.add(sipPathChooser);
		this.add(sipPathChooserButton);
		this.add(winScpLabel);
		this.add(winScpPathTextField);
		this.add(winScpPathChooser);
		this.add(winScpPathChooserButton);
		this.add(responseLabel);
		this.add(responsePathTextField);
		this.add(responsePathChooser);
		this.add(responsePathChooserButton);
		this.add(hostNameLabel);
		this.add(hostnameCombo);
		this.add(buttonSpeichernLadenPanel);

		this.add(uebertragenButton);

		this.add(backgroundImageLabel);
		this.setLayout(null);
		return this;
	}

	/**
	 * Checks if the current section (= panel) contains invalid settings/user input
	 * and makes the current panel invisible if no problems exist
	 * 
	 * @return true if the section could be left, otherwise false
	 */
	public boolean leaveSection(JButton uebertragenActivatedIconButton, JButton uebertragenIconButton) {
		boolean leave = true;
//		
//		if (this.isVisible()) {
//			// TODO Prüfung der Felder
//			
//		   
//		} 

		uebertragenActivatedIconButton.setVisible(false);
		uebertragenIconButton.setVisible(true);

		this.setVisible(false);

		return leave;
	}

}
