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
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
	private JLabel proxyHostNameLabel;
	private JLabel proxyPortLabel;
	private JLabel proxyUserNameLabel;
	private JLabel proxyPasswordLabel;

	
	// Eingabefelder
	private JTextField userNameTextField;
	private JTextField sipPathTextField;
	private JTextField winScpPathTextField;
	private JTextField responsePathTextField;

	private JTextField proxyHostNameTextField;
	private JTextField proxyPortTextField;
	private JTextField proxyUserTextField;

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
	private JPasswordField proxyPasswordTextField;

	// Buttons
	// 3 Punkte
	private URL dotsButtonImage;

	// Sip-auswählen
	private JButton sipPathChooserButton;
	private JFileChooser sipPathChooser;
	
	private JCheckBox proxyIsUsed; 

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
	private String proxyPwdEncrypted = "";

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
		// this.scriptFolderPath = scriptFolderPathIn;
		this.scriptFolderPath = "D:\\workspace\\ci\\DNSCore\\SIP-Builder\\src\\main\\scripts";
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
				char[] proxyPassword = proxyPasswordTextField.getPassword();
				String keyBase64 = "";
                String passwordContractorEnc = "";
                String passwordProxyUserEnc = "";
                
				if (password.length == 0) {
					messageWriter.showMessage("Bitte geben Sie ein Passwort für den Contractor ein.", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (proxyIsUsed.isSelected() && proxyPassword.length == 0) {
					messageWriter.showMessage("Bitte geben Sie ein Passwort für den Proxy-USer ein.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					SecretKey aesKey = generateAesKey();
	                 keyBase64 = Base64.getEncoder().encodeToString(aesKey.getEncoded());
	                 passwordContractorEnc = encryptPassword(password, aesKey);
	                 passwordProxyUserEnc = encryptPassword(proxyPassword, aesKey);
	                
//					byte[] salt = new byte[16];
//					rng.nextBytes(salt);
//					// PBKDF2 mit HMAC-SHA-256 (Iterationszahl je nach Policy erhöhen)
//					byte[] hashPwd = hashPassword(password, salt, 150_000, 256);
//					// typischerweise: Base64(salt | hash) senden/speichern
//					String base64EncPwd = Base64.getEncoder()
//							.encodeToString(ByteBuffer.allocate(salt.length + hashPwd.length).put(salt).put(hashPwd).array());
//					pwdEncrypted = base64EncPwd.substring(0, Math.min(60, base64EncPwd.length()));
////					JOptionPane.showMessageDialog(UebertragenPanel.this,
////							"Zu sendender Hash (Demo):\n" + payload.substring(0, Math.min(60, payload.length())) + "…");
//					 if (proxyIsUsed.isSelected()) {
//						 byte[] hashProxyPwd = hashPassword(proxyPassword, salt, 150_000, 256);
//							// typischerweise: Base64(salt | hash) senden/speichern
//							String base64EncProxyPwd = Base64.getEncoder()
//									.encodeToString(ByteBuffer.allocate(salt.length + hashProxyPwd.length).put(salt).put(hashProxyPwd).array());
//							proxyPwdEncrypted = base64EncProxyPwd.substring(0, Math.min(60, base64EncProxyPwd.length()));
//					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler: " + ex.getMessage());
				} finally {
					// delete the password
					Arrays.fill(password, '\0');
					Arrays.fill(proxyPassword, '\0');
				}

				// check if the input Fields are filled
				checkInputFields();

				// check the operating system
				String os = System.getProperty("os.name").toLowerCase();
				System.out.println(" os:  " + os);

				boolean isWindow = os.contains("win");
				String shell = "";
				if (isWindow) {
					shell = "powershell.exe";
				} else {
					shell = "pwsh";
				}

				if (!scriptFolderPath.endsWith(".ps1")) {
					scriptFolderPath = scriptFolderPath + "\\createAndTransfer.ps1";
				}
				System.out.println(" scriptFolderPath : " + scriptFolderPath);

				List<String> scriptParams = new ArrayList<String>();
				scriptParams.add(shell);
				scriptParams.add("-NoProfile");
				scriptParams.add("-ExecutionPolicy");
				scriptParams.add("Bypass");
				scriptParams.add("-File");
				scriptParams.add(scriptFolderPath);

				// params for the powershell script
				scriptParams.add(sipPathTextField.getText().trim());
				scriptParams.add(winScpPathTextField.getText().trim());
				scriptParams.add(hostnameUebertragen);
				scriptParams.add(userNameTextField.getText().trim());
				//scriptParams.add(passwordTextField.getPassword().toString());
				scriptParams.add(passwordContractorEnc);
				scriptParams.add(responsePathTextField.getText().trim());
				scriptParams.add(proxyHostNameTextField.getText().trim());
				scriptParams.add(proxyPortTextField.getText().trim());
				scriptParams.add(proxyUserTextField.getText().trim());
				//scriptParams.add(proxyPasswordTextField.getPassword().toString());
				scriptParams.add(passwordProxyUserEnc);
				scriptParams.add(keyBase64);

				ProcessBuilder processBuilder = new ProcessBuilder(scriptParams);
				processBuilder.redirectErrorStream(true); // stderr in stdout mergen

				ScriptOutputDialog dlg = new ScriptOutputDialog(SwingUtilities.getWindowAncestor(uebertragenButton),
						"PowerShell Output");

				dlg.setVisible(true);
				dlg.appendLine("Starte Script: " + scriptFolderPath);
				dlg.runProcess(processBuilder);

			}

			

		});
		
		ladenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = savePropertiesFile(FILENAME);
				File file = new File(filename);

				try {
					if (!file.exists()) {
						JOptionPane.showMessageDialog(UebertragenPanel.this, "Keine Datei gefunden: " + filename);
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
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler beim Laden: " + ex.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}

		});

		speichernButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String filename = savePropertiesFile(FILENAME);
				File file = new File(filename);
				try {
					savePropertiesToXmlFile(file, userNameTextField.getText(), sipPathTextField.getText(),
							winScpPathTextField.getText(), responsePathTextField.getText(),
							proxyHostNameTextField.getText(), proxyPortTextField.getText(),
							proxyUserTextField.getText());

					JOptionPane.showMessageDialog(UebertragenPanel.this, "Gespeichert: " + filename, "Info",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (IOException ex) {
					JOptionPane.showMessageDialog(UebertragenPanel.this, "Fehler beim Speichern: " + ex.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
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
	
	/**
	 * load the data from the properties file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Properties loadPropertiesFromXmlFile(File file) throws IOException {
		Properties p = new Properties();
		try (InputStream is = new FileInputStream(file)) {
			p.loadFromXML(is);
		}
		return p;
	}

	/**
	 * save the properties for load again
	 * 
	 * @param file
	 * @param userName
	 * @param sipPath
	 * @param winScpPath
	 * @param responsePath
	 * @param string3
	 * @param string2
	 * @param string
	 * @throws IOException
	 */
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

		try (OutputStream os = new FileOutputStream(file)) {
			// storeToXML schreibt XML; encoding wird im XML-Header gesetzt
			p.storeToXML(os, "GUI Eingaben", StandardCharsets.UTF_8.name());
		}
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private static String safe(String s) {
		return s == null ? "" : s;
	}

	/**
	 * Save the properties file
	 * 
	 * @param defaultFileName
	 * @return
	 */
	private String savePropertiesFile(String defaultFileName) {

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
			messageWriter.showMessage("Bitte geben Sie den Pfad zu Ihrem Response-Ordner an.",JOptionPane.ERROR_MESSAGE);
			return;
		}

		String hostNameTxt = hostnameCombo.getSelectedItem().toString();
		if (hostNameTxt.equals(BITTE_WAEHLEN)) {
			messageWriter.showMessage("Bitte wählen Sie das Zielsystem aus.", JOptionPane.ERROR_MESSAGE);
			return;
		}
        
		if (proxyIsUsed.isSelected()) {
			String proxyPortString = proxyPortTextField.getText();
			try {
				int port = Integer.parseInt(proxyPortString);
	
				if (port < 1 || port > 65535) {
					messageWriter.showMessage("Ungültiger Port!", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (NumberFormatException nfe) {
				messageWriter.showMessage("Ungültiger Port!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	

	}

	/**
	 * Encrypt the password
	 * 
	 * @param password
	 * @param salt
	 * @param iterations
	 * @param keyLenBits
	 * @return
	 * @throws Exception
	 */
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

		proxyHostNameLabel = new JLabel("Proxy Host Name");
		proxyHostNameLabel.setFont(boldFont.deriveFont(10.0f));
		proxyPortLabel = new JLabel("Proxy Port Name");
		proxyPortLabel.setFont(boldFont.deriveFont(10.0f));
		proxyUserNameLabel = new JLabel("Proxy User Name");
		proxyUserNameLabel.setFont(boldFont.deriveFont(10.0f));
		proxyPasswordLabel = new JLabel("Proxy Password Name");
		proxyPasswordLabel.setFont(boldFont.deriveFont(10.0f));

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

		proxyIsUsed = new JCheckBox("Proxy Einstlelungen", false);
		proxyIsUsed.setOpaque(false);
		proxyIsUsed.setFont(standardFont.deriveFont(12.0f));
		proxyHostNameTextField = new JTextField();
		proxyHostNameTextField.setEditable(true);
		proxyHostNameTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		proxyPortTextField = new JTextField();
		proxyPortTextField.setEditable(true);
		proxyPortTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		proxyUserTextField = new JTextField();
		proxyUserTextField.setEditable(true);
		proxyUserTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		proxyPasswordTextField = new JPasswordField();
		proxyPasswordTextField.setEditable(true);
		proxyPasswordTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

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
	 * initialize the buttons
	 * 
	 * @param button
	 * @param text
	 * @return
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
	 * 
	 * @param startPanelWidth
	 * @param startPanelHeight
	 * @param backgroundUebertragenImageLabel
	 */

	public void defineElementsPosition(int startPanelWidth, int startPanelHeight,
			JLabel backgroundUebertragenImageLabel) {

		int jhOffsetY = 50;

		this.backgroundImageLabel = backgroundUebertragenImageLabel;

		this.setBounds(0, 0, startPanelWidth, startPanelHeight);
		backgroundImageLabel.setBounds(0, 0, startPanelWidth, startPanelHeight);

		userNameLabel.setBounds(255, 45 + jhOffsetY, 170, 20);
		userNameTextField.setBounds(420, 45 + jhOffsetY, 260, 20);

		passwordLabel.setBounds(255, 70 + jhOffsetY, 170, 20);
		passwordTextField.setBounds(420, 70 + jhOffsetY, 260, 20);

		sipPathLabel.setBounds(255, 95 + jhOffsetY, 300, 20);
		sipPathTextField.setBounds(420, 95 + jhOffsetY, 260, 20);
		sipPathChooserButton.setBounds(690, 95 + jhOffsetY, 45, 20);

		winScpLabel.setBounds(255, 120 + jhOffsetY, 430, 20);
		winScpPathTextField.setBounds(420, 120 + jhOffsetY, 260, 20);
		winScpPathChooserButton.setBounds(690, 120 + jhOffsetY, 45, 20);

		responseLabel.setBounds(255, 145 + jhOffsetY, 650, 20);
		responsePathTextField.setBounds(420, 145 + jhOffsetY, 260, 20);
		responsePathChooserButton.setBounds(690, 145 + jhOffsetY, 45, 20);

		hostNameLabel.setBounds(255, 170 + jhOffsetY, 780, 20);
		hostnameCombo.setBounds(420, 170 + jhOffsetY, 260, 20);

		proxyHostNameLabel.setBounds(255, 195 + jhOffsetY, 910, 20);
		proxyHostNameTextField.setBounds(420, 195 + jhOffsetY, 260, 20);

		proxyPortLabel.setBounds(255, 220 + jhOffsetY, 1040, 20);
		proxyPortTextField.setBounds(420, 220 + jhOffsetY, 260, 20);

		proxyUserNameLabel.setBounds(255, 245 + jhOffsetY, 1170, 20);
		proxyUserTextField.setBounds(420, 245 + jhOffsetY, 260, 20);

		proxyPasswordLabel.setBounds(255, 270 + jhOffsetY, 1170, 20);
		proxyPasswordTextField.setBounds(420, 270 + jhOffsetY, 260, 20);

		buttonSpeichernLadenPanel.setBounds(255, 310 + jhOffsetY, 300, 50);

		uebertragenButton.setBounds(400, 440 + jhOffsetY, 90, 20);

	}

	/**
	 * add the elements to the panel
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
		this.add(proxyHostNameLabel);
		this.add(proxyHostNameTextField);
		this.add(proxyPortLabel);
		this.add(proxyPortTextField);
		this.add(proxyPasswordTextField);
		this.add(proxyUserNameLabel);
		this.add(proxyUserTextField);
		this.add(proxyPasswordLabel);
		this.add(proxyPasswordTextField);
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
