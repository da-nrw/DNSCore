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

package de.uzk.hki.da.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.metadata.ContractRights;
import de.uzk.hki.da.metadata.ContractSettings;
import de.uzk.hki.da.metadata.PublicationRights;
import de.uzk.hki.da.metadata.PremisXmlWriter;
import de.uzk.hki.da.sb.Logger;
import de.uzk.hki.da.sb.SIPFactory;
import de.uzk.hki.da.sb.UserInputValidator;
import de.uzk.hki.da.sb.SIPFactory.Feedback;
import de.uzk.hki.da.sb.SIPFactory.KindOfSIPBuilding;
import de.uzk.hki.da.utils.Utilities;

/**
 * Displays the graphical user interface and reacts to user input
 * 
 * @author Thomas Kleinke
 * @author Martin Fischer
 */
public class Gui extends JFrame{

	private static final long serialVersionUID = -2783837120567684391L;

	SIPFactory sipFactory = new SIPFactory();
	ContractSettings contractSettings = null;
	PremisXmlWriter premisWriter = new PremisXmlWriter();
	GuiMessageWriter messageWriter = new GuiMessageWriter();
	Logger logger;

	JFileChooser sourcePathChooser = new JFileChooser(new File("."));
	JFileChooser destinationPathChooser = new JFileChooser(new File("."));
	JFileChooser contractFileLoadPathChooser = new JFileChooser(new File("."));
	JFileChooser contractFileSavePathChooser = new JFileChooser(new File("."));

	ClassLoader classloader = Gui.class.getClassLoader();

	Font standardFont, boldFont;

	String confFolderPath, dataFolderPath;


	// GUI Elements

	// Panels
	JPanel startPanel;
	JPanel loadStandardPanel;
	JPanel institutionPanel;
	JPanel institutionTempPanel;
	JPanel institutionRestrictionPanel;
	JPanel publicPanel;
	JPanel publicTempPanel;
	JPanel publicRestrictionPanel;
	JPanel migrationPanel;
	JPanel savePanel;
	JPanel createPanel;

	JTabbedPane institutionTabbedPane;
	JPanel institutionTextPanel;
	JPanel institutionImagePanel;
	JPanel institutionAudioPanel;
	JPanel institutionVideoPanel;

	JTabbedPane publicTabbedPane;
	JPanel publicTextPanel;
	JPanel publicImagePanel;
	JPanel publicAudioPanel;
	JPanel publicVideoPanel;


	// Labels 
	JLabel backgroundStartImageLabel;
	JLabel backgroundLoadStandardImageLabel;
	JLabel backgroundInstitutionImageLabel;
	JLabel backgroundInstitutionTempImageLabel;
	JLabel backgroundInstitutionRestrictionImageLabel;
	JLabel backgroundPublicImageLabel;
	JLabel backgroundPublicTempImageLabel;
	JLabel backgroundPublicRestrictionImageLabel;
	JLabel backgroundMigrationImageLabel;
	JLabel backgroundSaveImageLabel;
	JLabel backgroundCreateImageLabel;

	JLabel versionInfoLabel;
	JLabel welcomeLabel;
	JLabel sourceLabel;
	JLabel destinationLabel;
	JLabel rightsLabel;
	JLabel institutionLabel;
	JLabel institutionStartLabel;
	JLabel institutionTempStartDateLabel;
	JLabel institutionRestrictionHeadlineLabel;
	JLabel institutionRestrictionTextPagesLabel;
	JLabel institutionRestrictionImageLabel;
	JLabel institutionRestrictionImageTextTypeLabel;
	JLabel institutionRestrictionImageTextOpacityLabel;
	JLabel institutionRestrictionImageTextSizeLabel;
	JLabel institutionRestrictionAudioDurationLabel;
	JLabel institutionRestrictionVideoQualityLabel;
	JLabel institutionRestrictionVideoDurationLabel;
	JLabel publicLabel;
	JLabel publicStartLabel;
	JLabel publicTempStartDateLabel;
	JLabel publicRestrictionHeadlineLabel;
	JLabel publicRestrictionTextPagesLabel;
	JLabel publicRestrictionImageLabel;
	JLabel publicRestrictionImageTextTypeLabel;
	JLabel publicRestrictionImageTextOpacityLabel;
	JLabel publicRestrictionImageTextSizeLabel;
	JLabel publicRestrictionAudioDurationLabel;
	JLabel publicRestrictionVideoQualityLabel;
	JLabel publicRestrictionVideoDurationLabel;
	JLabel migrationConversionLabel;
	JLabel migrationConditionLabel;
	JLabel saveLabel;
	JLabel createLabel;
	JLabel sipProgressDisplayLabel;
	JLabel sipProgressStepLabel;


	// TextAreas
	JTextArea welcomeArea;
	JTextArea rightsAreaOne;
	JTextArea rightsAreaTwo;
	JTextArea rightsAreaThree;
	JTextArea institutionArea;
	JTextArea institutionRestrictionArea;
	JTextArea institutionRestrictionTextArea;
	JTextArea institutionRestrictionImageArea;
	JTextArea publicArea;
	JTextArea publicDDBArea;
	JTextArea publicRestrictionArea;
	JTextArea publicRestrictionTextArea;
	JTextArea publicRestrictionImageArea;
	JTextArea migrationArea;
	JTextArea settingsOverviewInfoArea;
	JTextArea saveArea;
	JScrollPane settingsOverviewArea;
	JTextArea settingsOverviewTextArea;
	JTextArea createArea;


	// Buttons
	JButton helpIconButton;
	JButton sourceChooserButton;
	JButton destinationChooserButton;
	JButton goToLoadStandardButton;
	JButton loadContractButton;
	JButton standardContractButton;
	JButton goBackToStartButton;
	JButton goToInstitutionButton;
	JButton goBackToLoadStandardButton;
	JButton goToInstitutionTempButton;
	JButton goBackToInstitutionButton;
	JButton goToInstitutionRestrictionOrPublicButton;
	JButton goBackToInstitutionTempButton;
	JButton goToPublicButton;
	JButton goBackToInstitutionRestrictionOrTempButton;
	JButton goToPublicTempButton;
	JButton goBackToPublicButton;
	JButton goToPublicRestrictionOrMigrationButton;
	JButton goBackToPublicTempButton;
	JButton goToMigrationButton;
	JButton goBackToPublicRestrictionOrTempButton;
	JButton goToSaveButton;
	JButton saveButton;
	JButton goBackToMigrationButton;
	JButton goToCreateButton;
	JButton createButton;
	JButton goBackToSaveButton;
	JButton abortButton;
	JButton quitButton;

	JButton startActivatedIconButton;
	JButton startIconButton;
	JButton loadActivatedIconButton;
	JButton loadIconButton;
	JButton publicationActivatedIconButton;
	JButton publicationIconButton;
	JButton institutionActivatedIconButton;
	JButton institutionIconButton;
	JButton institutionTempActivatedIconButton;
	JButton institutionTempIconButton;
	JButton institutionRestrictionActivatedIconButton;
	JButton institutionRestrictionIconButton;
	JButton publicActivatedIconButton;
	JButton publicIconButton;
	JButton publicTempActivatedIconButton;
	JButton publicTempIconButton;
	JButton publicRestrictionActivatedIconButton;
	JButton publicRestrictionIconButton;
	JButton migrationActivatedIconButton;
	JButton migrationIconButton;
	JButton saveActivatedIconButton;
	JButton saveIconButton;
	JButton createActivatedIconButton;
	JButton createIconButton;


	// Radiobuttons
	ButtonGroup institutionAllowDenyGroup;
	JRadioButton institutionAllowRadioButton;
	JRadioButton institutionDenyRadioButton;
	ButtonGroup institutionTempLawGroup;
	JRadioButton institutionTempRadioButton;
	JRadioButton institutionLawRadioButton;
	JRadioButton institutionNoTempRestrictionRadioButton;
	ButtonGroup publicAllowDenyGroup;
	JRadioButton publicAllowRadioButton;
	JRadioButton publicDenyRadioButton;
	ButtonGroup publicTempLawGroup;
	JRadioButton publicTempRadioButton;
	JRadioButton publicLawRadioButton;
	JRadioButton publicNoTempRestrictionRadioButton;


	// Checkboxes
	JCheckBox collectionCheckBox;
	JCheckBox institutionTextRestrictionCheckBox;
	JCheckBox institutionImageRestrictionCheckBox;
	JCheckBox institutionImageTextCheckBox;
	JCheckBox institutionAudioRestrictionCheckBox;
	JCheckBox institutionVideoRestrictionCheckBox;
	JCheckBox institutionVideoDurationCheckBox;
	JCheckBox publicDDBCheckBox;
	JCheckBox publicTextRestrictionCheckBox;
	JCheckBox publicImageRestrictionCheckBox;
	JCheckBox publicImageTextCheckBox;
	JCheckBox publicAudioRestrictionCheckBox;
	JCheckBox publicVideoRestrictionCheckBox;
	JCheckBox publicVideoDurationCheckBox;
	JCheckBox compressionCheckBox;


	// Textfields     
	JTextField sourcePathTextField;
	JTextField destinationPathTextField;
	JTextField collectionNameTextField;
	JTextField institutionTempStartDateTextField;
	JTextField institutionRestrictionTextPagesTextField;
	JTextField institutionRestrictionImageTextField;
	JTextField publicTempStartDateTextField;
	JTextField publicRestrictionTextPagesTextField;
	JTextField publicRestrictionImageTextField;


	// Comboboxes
	JComboBox kindOfSIPBuildingDropDown;
	JComboBox institutionLawIdDropDown;
	JComboBox institutionImageDropDown;
	JComboBox institutionImageTextDropDown;
	JComboBox institutionImageTextOpacityDropDown;
	JComboBox institutionImageTextSizeDropDown;
	JComboBox institutionAudioDurationDropDown;
	JComboBox institutionVideoQualityDropDown;
	JComboBox institutionVideoDurationDropDown;
	JComboBox publicLawIdDropDown;
	JComboBox publicImageDropDown;
	JComboBox publicImageTextDropDown;
	JComboBox publicImageTextOpacityDropDown;
	JComboBox publicImageTextSizeDropDown;
	JComboBox publicAudioDurationDropDown;
	JComboBox publicVideoQualityDropDown;
	JComboBox publicVideoDurationDropDown;
	JComboBox migrationDropDown;


	// ProgressBar
	JProgressBar progressBar;


	public Gui(String confFolderPath, String dataFolderPath) {

		this.confFolderPath = confFolderPath;
		this.dataFolderPath = dataFolderPath;
		if (!new File(dataFolderPath).exists())
			new File(dataFolderPath).mkdir();
		
		logger = new Logger(dataFolderPath);
		sipFactory.setLogger(logger);
		
		URL icon = classloader.getResource("images/sipBuilderIcon.png");
		URL logo = classloader.getResource(SIPBuilder.getProperties().getProperty("LOGO_FILE"));
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image iconImage = toolkit.createImage(icon);
		Image logoImage = toolkit.createImage(logo);
		this.setIconImage(iconImage);

		messageWriter.setGui(this);
		messageWriter.setIconImage(logoImage);
		
		try {
			standardFont = Font.createFont(Font.TRUETYPE_FONT, classloader.getResourceAsStream("fonts/DejaVuSans.ttf"));
			boldFont = Font.createFont(Font.TRUETYPE_FONT, classloader.getResourceAsStream("fonts/DejaVuSans-Bold.ttf"));
		} catch (Exception e) {
			logger.log("ERROR: Couldn't find font file", e);
			messageWriter.showMessage("Schriftart konnte nicht gefunden werden!\n" + e.getMessage(), JOptionPane.PLAIN_MESSAGE);
		}
		
		try {
			contractSettings = new ContractSettings(confFolderPath);
		} catch (Exception e) {
			logger.log("ERROR: Failed to load contract settings file", e);
			messageWriter.showMessage("Die Contract Settings konnten nicht geladen werden.\n" +
					"Die Datei \"settings.xml\" im Verzeichnis \"conf\" wurde möglicherweise\n" +
					"verändert oder gelöscht.", JOptionPane.ERROR_MESSAGE);
			System.exit(SIPFactory.Feedback.GUI_ERROR.toInt());
		}
		
		initialize();
		
		if (!loadStandardRights())
			System.exit(SIPFactory.Feedback.GUI_ERROR.toInt());
	}		

	/**
	 * Initializes every element of the graphical user interface
	 */
	public void initialize() {

		initializePanels();
		initializeImageLabels();
		initializeTextLabels();
		initializeButtons();
		initializeTextAreas();
		initializeRadioButtons();
		initializeCheckBoxes();
		initializeTextFields();
		initializeComboBoxes();

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		defineElementPositions();	     
		addElementsToContentPane();
		createListeners();

		loadLastSessionSettings();
	}

	/**
	 * Creates the panels ( = SIP-Builder steps)
	 */
	private void initializePanels() {

		institutionTabbedPane = new JTabbedPane();
		UIManager.put("TabbedPane.borderHightlightColor", Color.gray);
		institutionTabbedPane.updateUI(); 
		institutionTextPanel = new JPanel();
		institutionTextPanel.setOpaque(true);
		institutionTabbedPane.addTab("Text", institutionTextPanel);
		institutionImagePanel = new JPanel();
		institutionImagePanel.setOpaque(true);
		institutionTabbedPane.addTab("Bild", institutionImagePanel);
		institutionAudioPanel = new JPanel();
		institutionAudioPanel.setOpaque(true);
		institutionTabbedPane.addTab("Audio", institutionAudioPanel);
		institutionVideoPanel = new JPanel();
		institutionVideoPanel.setOpaque(true);
		institutionTabbedPane.addTab("Video", institutionVideoPanel);

		publicTabbedPane = new JTabbedPane();
		UIManager.put("TabbedPane.borderHightlightColor", Color.gray);
		publicTabbedPane.updateUI(); 
		publicTextPanel = new JPanel();
		publicTextPanel.setOpaque(true);
		publicTabbedPane.addTab("Text", publicTextPanel);
		publicImagePanel = new JPanel();
		publicImagePanel.setOpaque(true);
		publicTabbedPane.addTab("Bild", publicImagePanel);
		publicAudioPanel = new JPanel();
		publicAudioPanel.setOpaque(true);
		publicTabbedPane.addTab("Audio", publicAudioPanel);
		publicVideoPanel = new JPanel();
		publicVideoPanel.setOpaque(true);
		publicTabbedPane.addTab("Video", publicVideoPanel);

		startPanel = new JPanel();
		startPanel.setVisible(true);
		loadStandardPanel = new JPanel();
		loadStandardPanel.setVisible(false);
		institutionPanel = new JPanel();
		institutionPanel.setVisible(false);
		institutionTempPanel = new JPanel();
		institutionTempPanel.setVisible(false);
		institutionRestrictionPanel = new JPanel();
		institutionRestrictionPanel.setVisible(false);
		publicPanel = new JPanel();
		publicPanel.setVisible(false);
		publicTempPanel = new JPanel();
		publicTempPanel.setVisible(false);
		publicRestrictionPanel = new JPanel();
		publicRestrictionPanel.setVisible(false);
		migrationPanel = new JPanel();
		migrationPanel.setVisible(false);
		savePanel = new JPanel();
		savePanel.setVisible(false);
		createPanel = new JPanel();
		createPanel.setVisible(false);		 
	}

	/**
	 * Creates the labels responsible for diplaying the background image
	 */
	private void initializeImageLabels() {

		URL backgroundImage = classloader.getResource(SIPBuilder.getProperties().getProperty("BACKGROUND_FILE"));

		backgroundStartImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundLoadStandardImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionTempImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionRestrictionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicTempImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicRestrictionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundMigrationImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundSaveImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundCreateImageLabel = new JLabel(new ImageIcon(backgroundImage));
	}

	/**
	 * Creates the text labels
	 */
	private void initializeTextLabels() {

		versionInfoLabel = new JLabel("SIP-Builder v" + Utilities.getSipBuilderShortVersion() + 
				" ®2011-2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung");
		versionInfoLabel.setFont(standardFont.deriveFont(10.0f));
		versionInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		versionInfoLabel.setForeground(Color.WHITE);
		welcomeLabel = new JLabel("Herzlich Willkommen im SIP-Builder!");
		welcomeLabel.setFont(boldFont.deriveFont(12.0f));
		sourceLabel = new JLabel("Quellordner:");
		sourceLabel.setFont(standardFont.deriveFont(10.0f));
		destinationLabel = new JLabel("Zielordner:");
		destinationLabel.setFont(standardFont.deriveFont(10.0f));
		rightsLabel = new JLabel("Rechteeinstellungen laden");
		rightsLabel.setFont(boldFont.deriveFont(12.0f));
		institutionLabel = new JLabel("Publikation für die eigene Institution");
		institutionLabel.setFont(boldFont.deriveFont(12.0f));
		institutionStartLabel = new JLabel("Startzeitpunkt der Publikation");
		institutionStartLabel.setFont(boldFont.deriveFont(12.0f));
		institutionTempStartDateLabel = new JLabel("Startdatum der institutionellen Publikation:");
		institutionTempStartDateLabel.setFont(standardFont.deriveFont(12.0f));
		institutionTempStartDateLabel.setEnabled(false);
		institutionRestrictionHeadlineLabel = new JLabel("Vorschaurestriktionen für die eigene Institution");
		institutionRestrictionHeadlineLabel.setFont(boldFont.deriveFont(12.0f));
		institutionRestrictionTextPagesLabel = new JLabel("Welche Seiten sollen angezeigt werden?");
		institutionRestrictionTextPagesLabel.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionTextPagesLabel.setEnabled(false);
		institutionRestrictionImageLabel = new JLabel("Bitte geben Sie den gewünschten Text an:");
		institutionRestrictionImageLabel.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionImageLabel.setEnabled(false);
		institutionRestrictionImageTextTypeLabel = new JLabel("Texttyp:");
		institutionRestrictionImageTextTypeLabel.setFont(standardFont.deriveFont(10.0f));
		institutionRestrictionImageTextTypeLabel.setEnabled(false);
		institutionRestrictionImageTextOpacityLabel = new JLabel("Sichtbarkeit:");
		institutionRestrictionImageTextOpacityLabel.setFont(standardFont.deriveFont(10.0f));
		institutionRestrictionImageTextOpacityLabel.setEnabled(false);
		institutionRestrictionImageTextSizeLabel = new JLabel("Schriftgröße:");
		institutionRestrictionImageTextSizeLabel.setFont(standardFont.deriveFont(10.0f));
		institutionRestrictionImageTextSizeLabel.setEnabled(false);
		institutionRestrictionAudioDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		institutionRestrictionAudioDurationLabel.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionAudioDurationLabel.setEnabled(false);
		institutionRestrictionVideoQualityLabel = new JLabel("In welcher Qualität sollen Ihre Videos abgespielt werden?");
		institutionRestrictionVideoQualityLabel.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionVideoQualityLabel.setEnabled(false);
		institutionRestrictionVideoDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		institutionRestrictionVideoDurationLabel.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionVideoDurationLabel.setEnabled(false);
		publicLabel = new JLabel("Publikation für die Öffentlichkeit");
		publicLabel.setFont(boldFont.deriveFont(12.0f));
		publicStartLabel = new JLabel("Startzeitpunkt der Publikation");
		publicStartLabel.setFont(boldFont.deriveFont(12.0f));
		publicTempStartDateLabel = new JLabel("Startdatum der öffentlichen Publikation:");
		publicTempStartDateLabel.setFont(standardFont.deriveFont(12.0f));
		publicTempStartDateLabel.setEnabled(false);
		publicRestrictionHeadlineLabel = new JLabel("Vorschaurestriktionen für die Öffentlichkeit");
		publicRestrictionHeadlineLabel.setFont(boldFont.deriveFont(12.0f));
		publicRestrictionTextPagesLabel = new JLabel("Welche Seiten sollen angezeigt werden?");
		publicRestrictionTextPagesLabel.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionTextPagesLabel.setEnabled(false);
		publicRestrictionImageLabel = new JLabel("Bitte geben Sie den gewünschten Text an:");
		publicRestrictionImageLabel.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionImageLabel.setEnabled(false);
		publicRestrictionImageTextTypeLabel = new JLabel("Texttyp:");
		publicRestrictionImageTextTypeLabel.setFont(standardFont.deriveFont(10.0f));
		publicRestrictionImageTextTypeLabel.setEnabled(false);
		publicRestrictionImageTextOpacityLabel = new JLabel("Sichtbarkeit:");
		publicRestrictionImageTextOpacityLabel.setFont(standardFont.deriveFont(10.0f));
		publicRestrictionImageTextOpacityLabel.setEnabled(false);
		publicRestrictionImageTextSizeLabel = new JLabel("Schriftgröße:");
		publicRestrictionImageTextSizeLabel.setFont(standardFont.deriveFont(10.0f));
		publicRestrictionImageTextSizeLabel.setEnabled(false);
		publicRestrictionAudioDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		publicRestrictionAudioDurationLabel.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionAudioDurationLabel.setEnabled(false);
		publicRestrictionVideoQualityLabel = new JLabel("In welcher Qualität sollen Ihre Videos abgespielt werden?");
		publicRestrictionVideoQualityLabel.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionVideoQualityLabel.setEnabled(false);
		publicRestrictionVideoDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		publicRestrictionVideoDurationLabel.setEnabled(false);
		publicRestrictionVideoDurationLabel.setFont(standardFont.deriveFont(12.0f));
		migrationConversionLabel = new JLabel("Konversions- und Migrationseinstellungen");
		migrationConversionLabel.setFont(boldFont.deriveFont(12.0f));
		migrationConditionLabel = new JLabel("Migrationsbedingung:");
		migrationConditionLabel.setFont(standardFont.deriveFont(10.0f));
		saveLabel = new JLabel("Einstellungen speichern");
		saveLabel.setFont(boldFont.deriveFont(12.0f));
		createLabel = new JLabel("SIP-Generierungsprozess starten");
		createLabel.setFont(boldFont.deriveFont(12.0f));
		sipProgressDisplayLabel = new JLabel();
		sipProgressDisplayLabel.setFont(standardFont.deriveFont(12.0f));
		sipProgressDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
		sipProgressStepLabel = new JLabel();
		sipProgressStepLabel.setFont(standardFont.deriveFont(12.0f));
		sipProgressStepLabel.setHorizontalAlignment(JLabel.CENTER);
	}

	/**
	 * Creates the buttons
	 */
	private void initializeButtons() {

		URL helpImage = classloader.getResource("images/help.png");
		URL createButtonImage = classloader.getResource("images/createButton.png");
		URL dotsButtonImage = classloader.getResource("images/dotsButton.png");
		URL saveButtonImage = classloader.getResource("images/saveButton.png");
		URL loadButtonImage = classloader.getResource("images/loadButton.png");
		URL standardButtonImage = classloader.getResource("images/standardButton.png");
		URL goToButtonImage = classloader.getResource("images/goToButton.png");
		URL goBackToButtonImage = classloader.getResource("images/goBackToButton.png");
		URL abortButtonImage = classloader.getResource("images/abortButton.png");
		URL quitButtonImage = classloader.getResource("images/quitButton.png");

		helpIconButton = new JButton(new ImageIcon(helpImage));
		helpIconButton.setBorder(null);
		helpIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpIconButton.setBackground(new Color(0,0,0,0));
		helpIconButton.setContentAreaFilled(false);
		sourceChooserButton = new JButton(new ImageIcon(dotsButtonImage));
		sourceChooserButton.setBorder(null);
		sourceChooserButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		sourceChooserButton.setBackground(new Color(0,0,0,0));
		sourceChooserButton.setContentAreaFilled(false);
		destinationChooserButton = new JButton(new ImageIcon(dotsButtonImage));
		destinationChooserButton.setBorder(null);
		destinationChooserButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		destinationChooserButton.setBackground(new Color(0,0,0,0));
		destinationChooserButton.setContentAreaFilled(false);
		goToLoadStandardButton = new JButton(new ImageIcon(goToButtonImage));
		goToLoadStandardButton.setBorder(null);
		goToLoadStandardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToLoadStandardButton.setBackground(new Color(0,0,0,0));
		goToLoadStandardButton.setContentAreaFilled(false);
		loadContractButton = new JButton(new ImageIcon(loadButtonImage));
		loadContractButton.setBorder(null);
		loadContractButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadContractButton.setBackground(new Color(0,0,0,0));
		loadContractButton.setContentAreaFilled(false);
		standardContractButton = new JButton(new ImageIcon(standardButtonImage));
		standardContractButton.setBorder(null);
		standardContractButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		standardContractButton.setBackground(new Color(0,0,0,0));
		standardContractButton.setContentAreaFilled(false);
		goBackToStartButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToStartButton.setBorder(null);
		goBackToStartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToStartButton.setBackground(new Color(0,0,0,0));
		goBackToStartButton.setContentAreaFilled(false);
		goToInstitutionButton = new JButton(new ImageIcon(goToButtonImage));
		goToInstitutionButton.setBorder(null);
		goToInstitutionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToInstitutionButton.setBackground(new Color(0,0,0,0));
		goToInstitutionButton.setContentAreaFilled(false);
		goBackToLoadStandardButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToLoadStandardButton.setBorder(null);
		goBackToLoadStandardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToLoadStandardButton.setBackground(new Color(0,0,0,0));
		goBackToLoadStandardButton.setContentAreaFilled(false);
		goToInstitutionTempButton = new JButton(new ImageIcon(goToButtonImage));
		goToInstitutionTempButton.setBorder(null);
		goToInstitutionTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToInstitutionTempButton.setBackground(new Color(0,0,0,0));
		goToInstitutionTempButton.setContentAreaFilled(false);
		goBackToInstitutionButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToInstitutionButton.setBorder(null);
		goBackToInstitutionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToInstitutionButton.setBackground(new Color(0,0,0,0));
		goBackToInstitutionButton.setContentAreaFilled(false);
		goToInstitutionRestrictionOrPublicButton = new JButton(new ImageIcon(goToButtonImage));
		goToInstitutionRestrictionOrPublicButton.setBorder(null);
		goToInstitutionRestrictionOrPublicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToInstitutionRestrictionOrPublicButton.setBackground(new Color(0,0,0,0));
		goToInstitutionRestrictionOrPublicButton.setContentAreaFilled(false);
		goBackToInstitutionTempButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToInstitutionTempButton.setBorder(null);
		goBackToInstitutionTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToInstitutionTempButton.setBackground(new Color(0,0,0,0));
		goBackToInstitutionTempButton.setContentAreaFilled(false);
		goToPublicButton = new JButton(new ImageIcon(goToButtonImage));
		goToPublicButton.setBorder(null);
		goToPublicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToPublicButton.setBackground(new Color(0,0,0,0));
		goToPublicButton.setContentAreaFilled(false);
		goBackToInstitutionRestrictionOrTempButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToInstitutionRestrictionOrTempButton.setBorder(null);
		goBackToInstitutionRestrictionOrTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToInstitutionRestrictionOrTempButton.setBackground(new Color(0,0,0,0));
		goBackToInstitutionRestrictionOrTempButton.setContentAreaFilled(false);
		goToPublicTempButton = new JButton(new ImageIcon(goToButtonImage));
		goToPublicTempButton.setBorder(null);
		goToPublicTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToPublicTempButton.setBackground(new Color(0,0,0,0));
		goToPublicTempButton.setContentAreaFilled(false);
		goBackToPublicButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToPublicButton.setBorder(null);
		goBackToPublicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToPublicButton.setBackground(new Color(0,0,0,0));
		goBackToPublicButton.setContentAreaFilled(false);
		goToPublicRestrictionOrMigrationButton = new JButton(new ImageIcon(goToButtonImage));
		goToPublicRestrictionOrMigrationButton.setBorder(null);
		goToPublicRestrictionOrMigrationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToPublicRestrictionOrMigrationButton.setBackground(new Color(0,0,0,0));
		goToPublicRestrictionOrMigrationButton.setContentAreaFilled(false);
		goBackToPublicTempButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToPublicTempButton.setBorder(null);
		goBackToPublicTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToPublicTempButton.setBackground(new Color(0,0,0,0));
		goBackToPublicTempButton.setContentAreaFilled(false);
		goToMigrationButton = new JButton(new ImageIcon(goToButtonImage));
		goToMigrationButton.setBorder(null);
		goToMigrationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToMigrationButton.setBackground(new Color(0,0,0,0));
		goToMigrationButton.setContentAreaFilled(false);
		goBackToPublicRestrictionOrTempButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToPublicRestrictionOrTempButton.setBorder(null);
		goBackToPublicRestrictionOrTempButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToPublicRestrictionOrTempButton.setBackground(new Color(0,0,0,0));
		goBackToPublicRestrictionOrTempButton.setContentAreaFilled(false);
		goToSaveButton = new JButton(new ImageIcon(goToButtonImage));
		goToSaveButton.setBorder(null);
		goToSaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToSaveButton.setBackground(new Color(0,0,0,0));
		goToSaveButton.setContentAreaFilled(false);
		saveButton = new JButton(new ImageIcon(saveButtonImage));
		saveButton.setBorder(null);
		saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveButton.setBackground(new Color(0,0,0,0));
		saveButton.setContentAreaFilled(false);
		goBackToMigrationButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToMigrationButton.setBorder(null);
		goBackToMigrationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToMigrationButton.setBackground(new Color(0,0,0,0));
		goBackToMigrationButton.setContentAreaFilled(false);
		goToCreateButton = new JButton(new ImageIcon(goToButtonImage));
		goToCreateButton.setBorder(null);
		goToCreateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goToCreateButton.setBackground(new Color(0,0,0,0));
		goToCreateButton.setContentAreaFilled(false);
		createButton = new JButton(new ImageIcon(createButtonImage));
		createButton.setBorder(null);
		createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		createButton.setBackground(new Color(0,0,0,0));
		createButton.setContentAreaFilled(false);
		goBackToSaveButton = new JButton(new ImageIcon(goBackToButtonImage));
		goBackToSaveButton.setBorder(null);
		goBackToSaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		goBackToSaveButton.setBackground(new Color(0,0,0,0));
		goBackToSaveButton.setContentAreaFilled(false);
		abortButton = new JButton(new ImageIcon(abortButtonImage));
		abortButton.setBorder(null);
		abortButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		abortButton.setBackground(new Color(0,0,0,0));
		abortButton.setContentAreaFilled(false);
		abortButton.setEnabled(false);
		abortButton.setVisible(false);
		quitButton = new JButton(new ImageIcon(quitButtonImage));
		quitButton.setBorder(null);
		quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		quitButton.setBackground(new Color(0,0,0,0));
		quitButton.setContentAreaFilled(false);
		quitButton.setEnabled(false);
		quitButton.setVisible(false);

		URL startActivatedIconImage = classloader.getResource("images/startActivated.png");
		URL startIconImage = classloader.getResource("images/start.png");
		URL loadActivatedIconImage = classloader.getResource("images/loadActivated.png");
		URL loadIconImage = classloader.getResource("images/load.png");
		URL publicationActivatedIconImage = classloader.getResource("images/publicationActivated.png");
		URL publicationIconImage = classloader.getResource("images/publication.png");
		URL institutionActivatedIconImage = classloader.getResource("images/institutionActivated.png");
		URL institutionIconImage = classloader.getResource("images/institution.png");
		URL institutionTempActivatedIconImage = classloader.getResource("images/tempActivated.png");
		URL institutionTempIconImage = classloader.getResource("images/temp.png");
		URL institutionRestrictionActivatedIconImage = classloader.getResource("images/restrictionActivated.png");
		URL institutionRestrictionIconImage = classloader.getResource("images/restriction.png");
		URL publicActivatedIconImage = classloader.getResource("images/publicActivated.png");
		URL publicIconImage = classloader.getResource("images/public.png");
		URL publicTempActivatedIconImage = classloader.getResource("images/tempActivated.png");
		URL publicTempIconImage = classloader.getResource("images/temp.png");
		URL publicRestrictionActivatedIconImage = classloader.getResource("images/restrictionActivated.png");
		URL publicRestrictionIconImage = classloader.getResource("images/restriction.png");
		URL migrationActivatedIconImage = classloader.getResource("images/migrationActivated.png");
		URL migrationIconImage = classloader.getResource("images/migration.png");
		URL saveActivatedIconImage = classloader.getResource("images/saveActivated.png");
		URL saveIconImage = classloader.getResource("images/save.png");
		URL createActivatedIconImage = classloader.getResource("images/createActivated.png");
		URL createIconImage = classloader.getResource("images/create.png");

		startActivatedIconButton = new JButton(new ImageIcon(startActivatedIconImage));
		startActivatedIconButton.setVisible(true);
		startActivatedIconButton.setBorder(null);
		startActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		startIconButton = new JButton(new ImageIcon(startIconImage));
		startIconButton.setVisible(false);
		startIconButton.setBorder(null);
		startIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadActivatedIconButton = new JButton(new ImageIcon(loadActivatedIconImage));
		loadActivatedIconButton.setVisible(false);
		loadActivatedIconButton.setBorder(null);
		loadActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadIconButton = new JButton(new ImageIcon(loadIconImage));
		loadIconButton.setVisible(true);
		loadIconButton.setBorder(null);
		loadIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicationActivatedIconButton = new JButton(new ImageIcon(publicationActivatedIconImage));
		publicationActivatedIconButton.setVisible(false);
		publicationActivatedIconButton.setBorder(null);
		publicationActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicationIconButton = new JButton(new ImageIcon(publicationIconImage));
		publicationIconButton.setVisible(true);
		publicationIconButton.setBorder(null);
		publicationIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionActivatedIconButton = new JButton(new ImageIcon(institutionActivatedIconImage));
		institutionActivatedIconButton.setVisible(false);
		institutionActivatedIconButton.setBorder(null);
		institutionActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionIconButton = new JButton(new ImageIcon(institutionIconImage));
		institutionIconButton.setVisible(true);
		institutionIconButton.setBorder(null);
		institutionIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionTempActivatedIconButton = new JButton(new ImageIcon(institutionTempActivatedIconImage));
		institutionTempActivatedIconButton.setVisible(false);
		institutionTempActivatedIconButton.setBorder(null);
		institutionTempActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionTempIconButton = new JButton(new ImageIcon(institutionTempIconImage));
		institutionTempIconButton.setVisible(true);
		institutionTempIconButton.setBorder(null);
		institutionTempIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionRestrictionActivatedIconButton = new JButton(new ImageIcon(institutionRestrictionActivatedIconImage));
		institutionRestrictionActivatedIconButton.setVisible(false);
		institutionRestrictionActivatedIconButton.setBorder(null);
		institutionRestrictionActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		institutionRestrictionIconButton = new JButton(new ImageIcon(institutionRestrictionIconImage));
		institutionRestrictionIconButton.setVisible(true);
		institutionRestrictionIconButton.setBorder(null);
		institutionRestrictionIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicActivatedIconButton = new JButton(new ImageIcon(publicActivatedIconImage));
		publicActivatedIconButton.setVisible(false);
		publicActivatedIconButton.setBorder(null);
		publicActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicIconButton = new JButton(new ImageIcon(publicIconImage));
		publicIconButton.setVisible(true);
		publicIconButton.setBorder(null);
		publicIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicTempActivatedIconButton = new JButton(new ImageIcon(publicTempActivatedIconImage));
		publicTempActivatedIconButton.setVisible(false);
		publicTempActivatedIconButton.setBorder(null);
		publicTempActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicTempIconButton = new JButton(new ImageIcon(publicTempIconImage));
		publicTempIconButton.setVisible(true);
		publicTempIconButton.setBorder(null);
		publicTempIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicRestrictionActivatedIconButton = new JButton(new ImageIcon(publicRestrictionActivatedIconImage));
		publicRestrictionActivatedIconButton.setVisible(false);
		publicRestrictionActivatedIconButton.setBorder(null);
		publicRestrictionActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		publicRestrictionIconButton = new JButton(new ImageIcon(publicRestrictionIconImage));
		publicRestrictionIconButton.setVisible(true);
		publicRestrictionIconButton.setBorder(null);
		publicRestrictionIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		migrationActivatedIconButton = new JButton(new ImageIcon(migrationActivatedIconImage));
		migrationActivatedIconButton.setVisible(false);
		migrationActivatedIconButton.setBorder(null);
		migrationActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		migrationIconButton = new JButton(new ImageIcon(migrationIconImage));
		migrationIconButton.setVisible(true);
		migrationIconButton.setBorder(null);
		migrationIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveActivatedIconButton = new JButton(new ImageIcon(saveActivatedIconImage));
		saveActivatedIconButton.setVisible(false);
		saveActivatedIconButton.setBorder(null);
		saveActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveIconButton = new JButton(new ImageIcon(saveIconImage));
		saveIconButton.setVisible(true);
		saveIconButton.setBorder(null);
		saveIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		createActivatedIconButton = new JButton(new ImageIcon(createActivatedIconImage));
		createActivatedIconButton.setVisible(false);
		createActivatedIconButton.setBorder(null);
		createActivatedIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		createIconButton = new JButton(new ImageIcon(createIconImage));
		createIconButton.setVisible(true);
		createIconButton.setBorder(null);
		createIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Creates the text areas
	 */
	private void initializeTextAreas() {

		welcomeArea = new JTextArea();
		welcomeArea.setEditable(false);
		welcomeArea.setOpaque(false);
		welcomeArea.setLineWrap(true);
		welcomeArea.setWrapStyleWord(true);
		welcomeArea.setFocusable(false);
		welcomeArea.setFont(standardFont.deriveFont(12.0f));
		welcomeArea.setText("Wählen Sie zunächst aus, wo die Daten aufzufinden sind, die Sie " +
				"einliefern wollen.\n\n" + "Geben Sie dann den Pfad zum Zielordner " +
				"an, in dem die SIPs gespeichert werden sollen.");

		rightsAreaOne = new JTextArea();
		rightsAreaOne.setEditable(false);
		rightsAreaOne.setOpaque(false);
		rightsAreaOne.setLineWrap(true);
		rightsAreaOne.setWrapStyleWord(true);
		rightsAreaOne.setFocusable(false);
		rightsAreaOne.setFont(standardFont.deriveFont(12.0f));
		rightsAreaOne.setText("In den folgenden Schritten können Sie die Publikations- und Migrationsrechte für " +
				"Ihre SIPs festlegen.\n\n" +
				"Falls Sie diese Einstellungen schon einmal vorgenommen und gespeichert " +
				"haben, können Sie sie jetzt laden:");	     

		rightsAreaTwo = new JTextArea();
		rightsAreaTwo.setEditable(false);
		rightsAreaTwo.setOpaque(false);
		rightsAreaTwo.setLineWrap(true);
		rightsAreaTwo.setWrapStyleWord(true);
		rightsAreaTwo.setFocusable(false);
		rightsAreaTwo.setFont(standardFont.deriveFont(12.0f));
		rightsAreaTwo.setText("Alternativ können Sie die Standard-Einstellungen wählen:");

		rightsAreaThree = new JTextArea();
		rightsAreaThree.setEditable(false);
		rightsAreaThree.setOpaque(false);
		rightsAreaThree.setLineWrap(true);
		rightsAreaThree.setWrapStyleWord(true);
		rightsAreaThree.setFocusable(false);
		rightsAreaThree.setFont(standardFont.deriveFont(12.0f));
		rightsAreaThree.setText("Sie haben im Folgenden die Möglichkeit, die Einstellungen\n" +
				"anzupassen bzw. komplett neu zu wählen. Wenn Sie dabei\n" + 
				"getroffene Entscheidungen rückgängig machen möchten,\n" +
				"können Sie jederzeit hierher zurückkehren und die Standard-\n" +
				"Einstellungen neu laden.");

		institutionArea = new JTextArea();
		institutionArea.setEditable(false);
		institutionArea.setOpaque(false);
		institutionArea.setLineWrap(true);
		institutionArea.setWrapStyleWord(true);
		institutionArea.setFocusable(false);
		institutionArea.setFont(standardFont.deriveFont(12.0f));
		institutionArea.setText("Möchten Sie - unabhängig von den Einstellungen für die Öffentlichkeit - zusätzliche Präsentationsdaten " +
				"generieren lassen, die nur Ihrer Institution über die Schnittstellen des Presentation Repository zugänglich sind?\n" +
				"Es können dabei Rechteeinstellungen gewählt werden, die von denen der öffentlich einsehbaren Daten abweichen.");

		institutionRestrictionArea = new JTextArea();
		institutionRestrictionArea.setEditable(false);
		institutionRestrictionArea.setOpaque(false);
		institutionRestrictionArea.setLineWrap(true);
		institutionRestrictionArea.setWrapStyleWord(true);
		institutionRestrictionArea.setFocusable(false);
		institutionRestrictionArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionArea.setText("Bitte wählen Sie die Vorschaurestriktionen für die verschiedenen " +
				"Medientypen.");
		
		institutionRestrictionTextArea = new JTextArea();
		institutionRestrictionTextArea.setEditable(false);
		institutionRestrictionTextArea.setOpaque(false);
		institutionRestrictionTextArea.setLineWrap(true);
		institutionRestrictionTextArea.setWrapStyleWord(true);
		institutionRestrictionTextArea.setFocusable(false);
		institutionRestrictionTextArea.setDisabledTextColor(Color.LIGHT_GRAY);
		institutionRestrictionTextArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionTextArea.setText("Geben Sie die gewünschten Seitenzahlen durch Kommas voneinander getrennt an. " + 
				"Um mehrere aufeinander folgende Seiten festzulegen, genügt es, die erste und " +
				"letzte Seitenzahl mit einem dazwischen liegenden Bindestrich anzugeben.\n\n" +
				"Beispiel:\n" +
				"Die Eingabe \"1,14-17,24,30\" drückt aus, dass die Seiten 1, 14, 15, 16, 17, 24 und 30 " +
				"angezeigt werden sollen.");
		institutionRestrictionTextArea.setEnabled(false);

		institutionRestrictionImageArea = new JTextArea();
		institutionRestrictionImageArea.setEditable(false);
		institutionRestrictionImageArea.setOpaque(false);
		institutionRestrictionImageArea.setLineWrap(true);
		institutionRestrictionImageArea.setWrapStyleWord(true);
		institutionRestrictionImageArea.setFocusable(false);
		institutionRestrictionImageArea.setDisabledTextColor(Color.LIGHT_GRAY);
		institutionRestrictionImageArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionImageArea.setText("Geben Sie die Bildqualität absolut oder im Verhältnis " +
				"zum Original an.");
		institutionRestrictionImageArea.setEnabled(false);

		publicArea = new JTextArea();
		publicArea.setEditable(false);
		publicArea.setOpaque(false);
		publicArea.setLineWrap(true);
		publicArea.setWrapStyleWord(true);
		publicArea.setFocusable(false);
		publicArea.setFont(standardFont.deriveFont(12.0f));
		publicArea.setText("Möchten Sie den Zugriff auf Ihre Daten über öffentliche Portale ermöglichen?");

		publicDDBArea = new JTextArea();
		publicDDBArea.setEditable(false);
		publicDDBArea.setOpaque(false);
		publicDDBArea.setLineWrap(true);
		publicDDBArea.setWrapStyleWord(true);
		publicDDBArea.setFocusable(false);
		publicDDBArea.setDisabledTextColor(Color.LIGHT_GRAY);
		publicDDBArea.setFont(standardFont.deriveFont(12.0f));
		publicDDBArea.setText("Im Regelfall werden veröffentlichte Daten automatisch der Deutschen Digitalen " +
				"Bibliothek zur Verfügung gestellt. Wünschen Sie dies nicht, können Sie diesen Prozess durch Deaktivieren " +
				"der Option verhindern.");
		
		publicRestrictionArea = new JTextArea();
		publicRestrictionArea.setEditable(false);
		publicRestrictionArea.setOpaque(false);
		publicRestrictionArea.setLineWrap(true);
		publicRestrictionArea.setWrapStyleWord(true);
		publicRestrictionArea.setFocusable(false);
		publicRestrictionArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionArea.setText("Bitte wählen Sie die Vorschaurestriktionen für die verschiedenen " +
				"Medientypen.");
		
		publicRestrictionTextArea = new JTextArea();
		publicRestrictionTextArea.setEditable(false);
		publicRestrictionTextArea.setOpaque(false);
		publicRestrictionTextArea.setLineWrap(true);
		publicRestrictionTextArea.setWrapStyleWord(true);
		publicRestrictionTextArea.setFocusable(false);
		publicRestrictionTextArea.setDisabledTextColor(Color.LIGHT_GRAY);
		publicRestrictionTextArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionTextArea.setText("Geben Sie die gewünschten Seitenzahlen durch Kommas voneinander getrennt an. " + 
				"Um mehrere aufeinander folgende Seiten festzulegen, genügt es, die erste und " +
				"letzte Seitenzahl mit einem dazwischen liegenden Bindestrich anzugeben.\n\n" +
				"Beispiel:\n" +
				"Die Eingabe \"1,14-17,24,30\" drückt aus, dass die Seiten 1, 14, 15, 16, 17, 24 und 30 " +
				"angezeigt werden sollen.");
		publicRestrictionTextArea.setEnabled(false);

		publicRestrictionImageArea = new JTextArea();
		publicRestrictionImageArea.setEditable(false);
		publicRestrictionImageArea.setOpaque(false);
		publicRestrictionImageArea.setLineWrap(true);
		publicRestrictionImageArea.setWrapStyleWord(true);
		publicRestrictionImageArea.setFocusable(false);
		publicRestrictionImageArea.setDisabledTextColor(Color.LIGHT_GRAY);
		publicRestrictionImageArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionImageArea.setText("Geben Sie die Bildqualität absolut oder im Verhältnis " +
				"zum Original an.");
		publicRestrictionImageArea.setEnabled(false);

		migrationArea = new JTextArea();
		migrationArea.setEditable(false);
		migrationArea.setOpaque(false);
		migrationArea.setLineWrap(true);
		migrationArea.setWrapStyleWord(true);
		migrationArea.setFocusable(false);
		migrationArea.setFont(standardFont.deriveFont(12.0f));
		migrationArea.setText("Bei der Einlieferung Ihrer Daten findet im Regelfall eine Erstkonversion in Dateiformate statt, die " +
				"für die Langzeitarchivierung geeignet sind. Darüber hinaus können spätere Konversionen folgen, um die " +
				"dauerhafte Lesbarkeit der Daten zu gewährleisten (Migration).\n" +
				"Wenn Sie im Falle einer Migration benachrichtigt oder um ausdrückliche Zustimmung gefragt werden " +
				"möchten, können Sie die entsprechenden Einstellungen hier vornehmen." );

		settingsOverviewInfoArea = new JTextArea();
		settingsOverviewInfoArea.setEditable(false);
		settingsOverviewInfoArea.setOpaque(false);
		settingsOverviewInfoArea.setLineWrap(true);
		settingsOverviewInfoArea.setWrapStyleWord(true);
		settingsOverviewInfoArea.setFocusable(false);
		settingsOverviewInfoArea.setFont(standardFont.deriveFont(12.0f));
		settingsOverviewInfoArea.setText("Sie haben folgende Rechteeinstellungen gewählt:");
		
		settingsOverviewTextArea = new JTextArea();
		settingsOverviewTextArea.setEditable(false);
		settingsOverviewTextArea.setOpaque(false);
		settingsOverviewTextArea.setLineWrap(true);
		settingsOverviewTextArea.setWrapStyleWord(true);
		settingsOverviewTextArea.setFocusable(false);
		settingsOverviewTextArea.setFont(standardFont.deriveFont(12.0f));
		settingsOverviewArea = new JScrollPane(settingsOverviewTextArea);
		settingsOverviewArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		settingsOverviewArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		saveArea = new JTextArea();
		saveArea.setEditable(false);
		saveArea.setOpaque(false);
		saveArea.setLineWrap(true);
		saveArea.setWrapStyleWord(true);
		saveArea.setFocusable(false);
		saveArea.setFont(standardFont.deriveFont(12.0f));
		saveArea.setText("Falls Sie Ihre Rechteeinstellungen später für weitere SIP-Generierungen " +
				"wiederverwenden möchten, können Sie diese nun sichern:");
		
		createArea = new JTextArea();
		createArea.setEditable(false);
		createArea.setOpaque(false);
		createArea.setLineWrap(true);
		createArea.setWrapStyleWord(true);
		createArea.setFocusable(false);
		createArea.setFont(standardFont.deriveFont(12.0f));
		createArea.setText("Wenn Sie mit den vorgenommenen Einstellungen zufrieden sind, können Sie " +
				"die SIP-Generierung starten, indem Sie den Button \"Erstellen\" betätigen.\n\n" +
				"Sie können den Prozess beschleunigen, indem Sie die Kompression deaktivieren. " +
				"Bitte beachten Sie, dass in diesem Fall größere SIP-Dateien erzeugt werden.");
	}

	/**
	 * Creates the radio buttons and radio button groups
	 */
	private void initializeRadioButtons() {

		institutionAllowRadioButton = new JRadioButton("Ja", false);
		institutionAllowRadioButton.setOpaque(false);
		institutionAllowRadioButton.setFont(standardFont.deriveFont(12.0f));

		institutionDenyRadioButton = new JRadioButton("Nein", true);
		institutionDenyRadioButton.setOpaque(false);
		institutionDenyRadioButton.setFont(standardFont.deriveFont(12.0f));

		institutionAllowDenyGroup = new ButtonGroup();
		institutionAllowDenyGroup.add(institutionAllowRadioButton);
		institutionAllowDenyGroup.add(institutionDenyRadioButton);
		
		institutionNoTempRestrictionRadioButton = new JRadioButton("Publikation nicht begrenzen", true);
		institutionNoTempRestrictionRadioButton.setOpaque(false);
		institutionNoTempRestrictionRadioButton.setFont(standardFont.deriveFont(12.0f));
		institutionTempRadioButton = new JRadioButton("Publikation zeitlich begrenzen", false);
		institutionTempRadioButton.setOpaque(false);
		institutionTempRadioButton.setFont(standardFont.deriveFont(12.0f));
		institutionLawRadioButton = new JRadioButton("Publikation mit Sperrgesetz begrenzen", false);
		institutionLawRadioButton.setOpaque(false);
		institutionLawRadioButton.setFont(standardFont.deriveFont(12.0f));		

		institutionTempLawGroup = new ButtonGroup();
		institutionTempLawGroup.add(institutionNoTempRestrictionRadioButton);
		institutionTempLawGroup.add(institutionTempRadioButton);
		institutionTempLawGroup.add(institutionLawRadioButton);
		
		publicAllowRadioButton = new JRadioButton("Ja", true);
		publicAllowRadioButton.setOpaque(false);
		publicAllowRadioButton.setFont(standardFont.deriveFont(12.0f));

		publicDenyRadioButton = new JRadioButton("Nein", false);
		publicDenyRadioButton.setOpaque(false);
		publicDenyRadioButton.setFont(standardFont.deriveFont(12.0f));

		publicAllowDenyGroup = new ButtonGroup();
		publicAllowDenyGroup.add(publicAllowRadioButton);
		publicAllowDenyGroup.add(publicDenyRadioButton);
		
		publicNoTempRestrictionRadioButton = new JRadioButton("Publikation nicht begrenzen", true);
		publicNoTempRestrictionRadioButton.setOpaque(false);
		publicNoTempRestrictionRadioButton.setFont(standardFont.deriveFont(12.0f));
		publicTempRadioButton = new JRadioButton("Publikation zeitlich begrenzen", false);
		publicTempRadioButton.setOpaque(false);
		publicTempRadioButton.setFont(standardFont.deriveFont(12.0f));
		publicLawRadioButton = new JRadioButton("Publikation mit Sperrgesetz begrenzen", false);
		publicLawRadioButton.setOpaque(false);
		publicLawRadioButton.setFont(standardFont.deriveFont(12.0f));
		
		publicTempLawGroup = new ButtonGroup();
		publicTempLawGroup.add(publicNoTempRestrictionRadioButton);
		publicTempLawGroup.add(publicTempRadioButton);
		publicTempLawGroup.add(publicLawRadioButton);
	}

	/**
	 * Creates the check boxes
	 */
	private void initializeCheckBoxes() {

		collectionCheckBox = new JCheckBox("SIPs zu einer Lieferung bündeln", false);
		collectionCheckBox.setFont(standardFont.deriveFont(12.0f));
		collectionCheckBox.setFocusable(false);
		collectionCheckBox.setOpaque(false);
		collectionCheckBox.setEnabled(false);

		institutionTextRestrictionCheckBox = new JCheckBox("Einsehbare Seiten festlegen", false);
		institutionTextRestrictionCheckBox.setOpaque(false);
		institutionTextRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		institutionImageRestrictionCheckBox = new JCheckBox("Bildqualität begrenzen", false);
		institutionImageRestrictionCheckBox.setOpaque(false);
		institutionImageRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		institutionImageTextCheckBox = new JCheckBox("Fußzeile oder Wasserzeichen angeben", false);
		institutionImageTextCheckBox.setOpaque(false);
		institutionImageTextCheckBox.setFont(standardFont.deriveFont(12.0f));
		institutionAudioRestrictionCheckBox = new JCheckBox("Länge von Audio-Dateien begrenzen", false);
		institutionAudioRestrictionCheckBox.setOpaque(false);
		institutionAudioRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		institutionVideoRestrictionCheckBox = new JCheckBox("Bildqualität von Videos begrenzen", false);
		institutionVideoRestrictionCheckBox.setOpaque(false);
		institutionVideoRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		institutionVideoDurationCheckBox = new JCheckBox("Länge von Videos begrenzen", false);
		institutionVideoDurationCheckBox.setOpaque(false);
		institutionVideoDurationCheckBox.setFont(standardFont.deriveFont(12.0f));

		publicDDBCheckBox = new JCheckBox("DDB-Harvesting erlauben", true);
		publicDDBCheckBox.setOpaque(false);
		publicDDBCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicTextRestrictionCheckBox = new JCheckBox("Einsehbare Seiten festlegen", false);
		publicTextRestrictionCheckBox.setOpaque(false);
		publicTextRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicImageRestrictionCheckBox = new JCheckBox("Bildqualität begrenzen", false);
		publicImageRestrictionCheckBox.setOpaque(false);
		publicImageRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicImageTextCheckBox = new JCheckBox("Fußzeile oder Wasserzeichen angeben", false);
		publicImageTextCheckBox.setOpaque(false);
		publicImageTextCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicAudioRestrictionCheckBox = new JCheckBox("Länge von Audio-Dateien begrenzen", false);
		publicAudioRestrictionCheckBox.setOpaque(false);
		publicAudioRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicVideoRestrictionCheckBox = new JCheckBox("Bildqualität von Videos begrenzen", false);
		publicVideoRestrictionCheckBox.setOpaque(false);
		publicVideoRestrictionCheckBox.setFont(standardFont.deriveFont(12.0f));
		publicVideoDurationCheckBox = new JCheckBox("Länge von Videos begrenzen", false);
		publicVideoDurationCheckBox.setOpaque(false);
		publicVideoDurationCheckBox.setFont(standardFont.deriveFont(12.0f));
		compressionCheckBox = new JCheckBox("SIP-Datei komprimieren", true);
		compressionCheckBox.setOpaque(false);
		compressionCheckBox.setFont(standardFont.deriveFont(12.0f));
	}

	/**
	 * Creates the text fields
	 */
	private void initializeTextFields() {

		sourcePathTextField = new JTextField();
		sourcePathTextField.setEditable(true);
		sourcePathTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		destinationPathTextField = new JTextField();
		destinationPathTextField.setEditable(true);
		destinationPathTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		collectionNameTextField = new JTextField();
		collectionNameTextField.setEditable(false);
		collectionNameTextField.setEnabled(false);
		collectionNameTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		institutionTempStartDateTextField = new JTextField();
		institutionTempStartDateTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		institutionTempStartDateTextField.setEditable(false);
		institutionTempStartDateTextField.setEnabled(false);
		institutionRestrictionTextPagesTextField = new JTextField();
		institutionRestrictionTextPagesTextField.setEditable(false);
		institutionRestrictionTextPagesTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		institutionRestrictionTextPagesTextField.setEnabled(false);
		institutionRestrictionImageTextField = new JTextField();
		institutionRestrictionImageTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		institutionRestrictionImageTextField.setEnabled(false);
		institutionRestrictionImageTextField.setEditable(false);
		publicTempStartDateTextField = new JTextField();
		publicTempStartDateTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		publicTempStartDateTextField.setEditable(false);
		publicTempStartDateTextField.setEnabled(false);
		publicRestrictionTextPagesTextField = new JTextField();
		publicRestrictionTextPagesTextField.setEditable(false);
		publicRestrictionTextPagesTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		publicRestrictionTextPagesTextField.setEnabled(false);
		publicRestrictionImageTextField = new JTextField();
		publicRestrictionImageTextField.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.gray));
		publicRestrictionImageTextField.setEnabled(false);
		publicRestrictionImageTextField.setEditable(false);
	}

	/**
	 * Creates the combo boxes (drop down menus)
	 */
	private void initializeComboBoxes() {

		kindOfSIPBuildingDropDown = new JComboBox();
		kindOfSIPBuildingDropDown.setFont(standardFont.deriveFont(11.0f));
		kindOfSIPBuildingDropDown.addItem("Einzelnes SIP aus dem Quellverzeichnis erstellen");
		kindOfSIPBuildingDropDown.addItem("Mehrere SIPs aus Unterordnern des Quellverzeichnisses erstellen");

		institutionLawIdDropDown = new JComboBox();
		institutionLawIdDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionLawIdDropDown.setEnabled(false);
		institutionLawIdDropDown.addItem("ePflicht");
		institutionLawIdDropDown.addItem("UrhG DE");

		institutionImageDropDown = new JComboBox();
		institutionImageDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionImageDropDown.addItem("Niedrig (" + contractSettings.getWidthImage(0) + "x" + contractSettings.getHeightImage(0) + ")");
		institutionImageDropDown.addItem("Mittel (" + contractSettings.getWidthImage(1) + "x" + contractSettings.getHeightImage(1) + ")");
		institutionImageDropDown.addItem("Hoch (" + contractSettings.getWidthImage(2) + "x" + contractSettings.getHeightImage(2) + ")");
		institutionImageDropDown.addItem(contractSettings.getPercentImage(0));
		institutionImageDropDown.addItem(contractSettings.getPercentImage(1));
		institutionImageDropDown.addItem(contractSettings.getPercentImage(2));
		institutionImageDropDown.setEnabled(false);

		institutionImageTextDropDown = new JComboBox();
		institutionImageTextDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionImageTextDropDown.addItem("Fußzeile");
		institutionImageTextDropDown.addItem("Wasserzeichen (oben)");
		institutionImageTextDropDown.addItem("Wasserzeichen (mittig)");
		institutionImageTextDropDown.addItem("Wasserzeichen (unten)");
		institutionImageTextDropDown.setEnabled(false);

		institutionImageTextOpacityDropDown = new JComboBox();
		institutionImageTextOpacityDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(0) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(1) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(2) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(3) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(4) + "%");
		institutionImageTextOpacityDropDown.setEnabled(false);

		institutionImageTextSizeDropDown = new JComboBox();
		institutionImageTextSizeDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(0));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(1));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(2));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(3));
		institutionImageTextSizeDropDown.setEnabled(false);

		institutionAudioDurationDropDown = new JComboBox();
		institutionAudioDurationDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(0));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(1));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(2));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(3));
		institutionAudioDurationDropDown.setEnabled(false);

		institutionVideoQualityDropDown = new JComboBox();
		institutionVideoQualityDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionVideoQualityDropDown.addItem("Niedrig");
		institutionVideoQualityDropDown.addItem("Mittel");
		institutionVideoQualityDropDown.addItem("Hoch");
		institutionVideoQualityDropDown.setEnabled(false);

		institutionVideoDurationDropDown = new JComboBox();
		institutionVideoDurationDropDown.setFont(standardFont.deriveFont(11.0f));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(0));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(1));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(2));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(3));
		institutionVideoDurationDropDown.setEnabled(false);

		publicLawIdDropDown = new JComboBox();
		publicLawIdDropDown.setFont(standardFont.deriveFont(11.0f));
		publicLawIdDropDown.setEnabled(false);
		publicLawIdDropDown.addItem("ePflicht");
		publicLawIdDropDown.addItem("UrhG DE");

		publicImageDropDown = new JComboBox();
		publicImageDropDown.setFont(standardFont.deriveFont(11.0f));
		publicImageDropDown.addItem("Niedrig (" + contractSettings.getWidthImage(0) + "x" + contractSettings.getHeightImage(0) + ")");
		publicImageDropDown.addItem("Mittel (" + contractSettings.getWidthImage(1) + "x" + contractSettings.getHeightImage(1) + ")");
		publicImageDropDown.addItem("Hoch (" + contractSettings.getWidthImage(2) + "x" + contractSettings.getHeightImage(2) + ")");
		publicImageDropDown.addItem(contractSettings.getPercentImage(0));
		publicImageDropDown.addItem(contractSettings.getPercentImage(1));
		publicImageDropDown.addItem(contractSettings.getPercentImage(2));
		publicImageDropDown.setEnabled(false);

		publicImageTextDropDown = new JComboBox();
		publicImageTextDropDown.setFont(standardFont.deriveFont(11.0f));
		publicImageTextDropDown.addItem("Fußzeile");
		publicImageTextDropDown.addItem("Wasserzeichen (oben)");
		publicImageTextDropDown.addItem("Wasserzeichen (mittig)");
		publicImageTextDropDown.addItem("Wasserzeichen (unten)");
		publicImageTextDropDown.setEnabled(false);

		publicImageTextOpacityDropDown = new JComboBox();
		publicImageTextOpacityDropDown.setFont(standardFont.deriveFont(11.0f));
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(0) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(1) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(2) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(3) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(4) + "%");
		publicImageTextOpacityDropDown.setEnabled(false);

		publicImageTextSizeDropDown = new JComboBox();
		publicImageTextSizeDropDown.setFont(standardFont.deriveFont(11.0f));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(0));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(1));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(2));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(3));
		publicImageTextSizeDropDown.setEnabled(false);

		publicAudioDurationDropDown = new JComboBox();
		publicAudioDurationDropDown.setFont(standardFont.deriveFont(11.0f));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(0));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(1));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(2));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(3));
		publicAudioDurationDropDown.setEnabled(false);

		publicVideoQualityDropDown = new JComboBox();
		publicVideoQualityDropDown.setFont(standardFont.deriveFont(11.0f));
		publicVideoQualityDropDown.addItem("Niedrig");
		publicVideoQualityDropDown.addItem("Mittel");
		publicVideoQualityDropDown.addItem("Hoch");
		publicVideoQualityDropDown.setEnabled(false);

		publicVideoDurationDropDown = new JComboBox();
		publicVideoDurationDropDown.setFont(standardFont.deriveFont(11.0f));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(0));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(1));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(2));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(3));
		publicVideoDurationDropDown.setEnabled(false);

		migrationDropDown = new JComboBox();
		migrationDropDown.setFont(standardFont.deriveFont(11.0f));
		migrationDropDown.addItem("Keine");
		migrationDropDown.addItem("Über Migration informieren");
		migrationDropDown.addItem("Zustimmung für Migration einholen");
	}

	/**
	 * Sets the initial position of every GUI element
	 */
	private void defineElementPositions() {

		// overall elements
		versionInfoLabel.setBounds(0, 472, 750, 20);
		startActivatedIconButton.setBounds(10, 70, 171, 20);
		startIconButton.setBounds(10, 70, 171, 20);
		loadActivatedIconButton.setBounds(10, 95, 171, 20);
		loadIconButton.setBounds(10, 95, 171, 20);
		publicationActivatedIconButton.setBounds(10, 120, 171, 20);
		publicationIconButton.setBounds(10, 120, 171, 20);
		publicActivatedIconButton.setBounds(10, 145, 171, 20);
		publicIconButton.setBounds(10, 145, 171, 20);
		publicTempActivatedIconButton.setBounds(10, 165, 171, 20);
		publicTempIconButton.setBounds(10, 165, 171, 20);
		publicRestrictionActivatedIconButton.setBounds(10, 185, 171, 20);
		publicRestrictionIconButton.setBounds(10, 185, 171, 20);
		institutionActivatedIconButton.setBounds(10, 210, 171, 20);
		institutionIconButton.setBounds(10, 210, 171, 20);
		institutionTempActivatedIconButton.setBounds(10, 230, 171, 20);
		institutionTempIconButton.setBounds(10, 230, 171, 20);
		institutionRestrictionActivatedIconButton.setBounds(10, 250, 171, 20);
		institutionRestrictionIconButton.setBounds(10, 250, 171, 20);
		migrationActivatedIconButton.setBounds(10, 275, 171, 20);
		migrationIconButton.setBounds(10, 275, 171, 20);
		saveActivatedIconButton.setBounds(10, 300, 171, 20);
		saveIconButton.setBounds(10, 300, 171, 20);
		createActivatedIconButton.setBounds(10, 325, 171, 20);
		createIconButton.setBounds(10, 325, 171, 20);
		helpIconButton.setBounds(709, 15, 20, 22);

		// startPanel
		startPanel.setBounds(0, 0, 750, 526);
		backgroundStartImageLabel.setBounds(0, 0, 750, 526);
		welcomeLabel.setBounds(255, 70, 300, 20);
		welcomeArea.setBounds(255, 100, 420, 80);
		sourceLabel.setBounds(255, 195, 70, 20);
		sourcePathTextField.setBounds(335, 195, 260, 20);
		sourceChooserButton.setBounds(600, 195, 45, 20);
		destinationLabel.setBounds(255, 220, 70, 20);
		destinationPathTextField.setBounds(335, 220, 260, 20);
		destinationChooserButton.setBounds(600, 220, 45, 20);
		kindOfSIPBuildingDropDown.setBounds(255, 255, 410, 20);
		
		
		
		goToLoadStandardButton.setBounds(575, 445, 90, 20);

		// loadStandardPanel
		loadStandardPanel.setBounds(0, 0, 750, 526);
		rightsLabel.setBounds(255, 70, 300, 20);
		rightsAreaOne.setBounds(255, 100, 430, 85);
		loadContractButton.setBounds(350, 189, 90, 20);
		rightsAreaTwo.setBounds(255, 223, 430, 20);
		standardContractButton.setBounds(350, 250, 90, 20);
		rightsAreaThree.setBounds(255, 283, 430, 100);
		goBackToStartButton.setBounds(450, 445, 90, 20);
		goToInstitutionButton.setBounds(575, 445, 90, 20);
		backgroundLoadStandardImageLabel.setBounds(0, 0, 750, 526);

		// institutionPanel
		institutionPanel.setBounds(0, 0, 750, 526);
		institutionLabel.setBounds(255, 70, 300, 20);
		institutionArea.setBounds(255, 100, 400, 90);
		institutionAllowRadioButton.setBounds(251, 210, 40, 20);
		institutionDenyRadioButton.setBounds(251, 235, 60, 20);
		goBackToLoadStandardButton.setBounds(450, 445, 90, 20);
		goToInstitutionTempButton.setBounds(575, 445, 90, 20);
		backgroundInstitutionImageLabel.setBounds(0, 0, 750, 526);

		// institutionTempPanel
		institutionTempPanel.setBounds(0, 0, 750, 526);
		institutionStartLabel.setBounds(255, 70, 300, 20);
		institutionNoTempRestrictionRadioButton.setBounds(251, 100, 215, 20);
		institutionTempRadioButton.setBounds(251, 125, 215, 20);
		institutionTempStartDateLabel.setBounds(255, 147, 280, 20);
		institutionTempStartDateTextField.setBounds(255, 172, 310, 20);
		institutionLawRadioButton.setBounds(251, 202, 275, 20);
		institutionLawIdDropDown.setBounds(255, 232, 310, 20);
		goBackToInstitutionButton.setBounds(450, 445, 90, 20);
		goToInstitutionRestrictionOrPublicButton.setBounds(575, 445, 90, 20);
		backgroundInstitutionTempImageLabel.setBounds(0, 0, 750, 526);

		// institutionRestrictionPanel
		institutionRestrictionPanel.setBounds(0, 0, 750, 526);
		institutionRestrictionHeadlineLabel.setBounds(255, 70, 350, 20);
		institutionRestrictionArea.setBounds(255, 100, 360, 40);
		institutionTabbedPane.setBounds(255, 140, 380, 295);
		goBackToInstitutionTempButton.setBounds(450, 445, 90, 20);
		goToPublicButton.setBounds(575, 445, 90, 20);
		backgroundInstitutionRestrictionImageLabel.setBounds(0, 0, 750, 526);

		// institutionRestrictionTextPanel
		institutionTextRestrictionCheckBox.setBounds(6, 10, 212, 20);
		institutionRestrictionTextPagesLabel.setBounds(10, 32, 320, 20);
		institutionRestrictionTextPagesTextField.setBounds(10, 57, 355, 20);
		institutionRestrictionTextArea.setBounds(10, 87, 360, 170);

		// institutionRestrictionImagePanel
		institutionImageRestrictionCheckBox.setBounds(6, 10, 192, 20);
		institutionRestrictionImageArea.setBounds(10, 35, 320, 40);
		institutionImageDropDown.setBounds(10, 75, 150, 20);
		institutionImageTextCheckBox.setBounds(6, 105, 270, 20);
		institutionRestrictionImageLabel.setBounds(10, 127, 310, 20);
		institutionRestrictionImageTextField.setBounds(10, 152, 355, 20);
		institutionRestrictionImageTextTypeLabel.setBounds(10, 182, 80, 20);
		institutionImageTextDropDown.setBounds(105, 182, 165, 20);
		institutionRestrictionImageTextOpacityLabel.setBounds(10, 212, 80, 20);
		institutionImageTextOpacityDropDown.setBounds(105, 212, 165, 20);
		institutionRestrictionImageTextSizeLabel.setBounds(10, 242, 80, 20);
		institutionImageTextSizeDropDown.setBounds(105, 242, 165, 20);

		// institutionRestrictionAudioPanel
		institutionAudioRestrictionCheckBox.setBounds(6, 10, 252, 20);
		institutionRestrictionAudioDurationLabel.setBounds(10, 32, 320, 20);
		institutionAudioDurationDropDown.setBounds(10, 57, 100, 20);

		// institutionRestrictionVideoPanel
		institutionVideoRestrictionCheckBox.setBounds(6, 10, 240, 20);
		institutionRestrictionVideoQualityLabel.setBounds(10, 32, 420, 20);
		institutionVideoQualityDropDown.setBounds(10, 57, 100, 20);
		institutionVideoDurationCheckBox.setBounds(6, 87, 214, 20);
		institutionRestrictionVideoDurationLabel.setBounds(10, 109, 320, 20);
		institutionVideoDurationDropDown.setBounds(10, 134, 100, 20);

		// publicPanel
		publicPanel.setBounds(0, 0, 750, 526);
		publicLabel.setBounds(255, 70, 300, 20);
		publicArea.setBounds(255, 100, 400, 40);
		publicAllowRadioButton.setBounds(251, 150, 40, 20);
		publicDenyRadioButton.setBounds(251, 175, 60, 20);
		publicDDBArea.setBounds(255, 215, 400, 60);
		publicDDBCheckBox.setBounds(251, 285, 190, 20);
		goBackToInstitutionRestrictionOrTempButton.setBounds(450, 445, 90, 20);
		goToPublicTempButton.setBounds(575, 445, 90, 20);
		backgroundPublicImageLabel.setBounds(0, 0, 750, 526);

		// publicTempPanel
		publicTempPanel.setBounds(0, 0, 750, 526);
		publicStartLabel.setBounds(255, 70, 300, 20);
		publicNoTempRestrictionRadioButton.setBounds(251, 100, 215, 20);
		publicTempRadioButton.setBounds(251, 125, 215, 20);
		publicTempStartDateLabel.setBounds(255, 147, 280, 20);
		publicTempStartDateTextField.setBounds(255, 172, 310, 20);
		publicLawRadioButton.setBounds(251, 202, 275, 20);
		publicLawIdDropDown.setBounds(255, 232, 310, 20);
		goBackToPublicButton.setBounds(450, 445, 90, 20);
		goToPublicRestrictionOrMigrationButton.setBounds(575, 445, 90, 20);
		backgroundPublicTempImageLabel.setBounds(0, 0, 750, 526);

		// publicRestrictionPanel
		publicRestrictionPanel.setBounds(0, 0, 750, 526);
		publicRestrictionHeadlineLabel.setBounds(255, 70, 350, 20);
		publicRestrictionArea.setBounds(255, 100, 360, 40);
		publicTabbedPane.setBounds(255, 140, 380, 295);
		goBackToPublicTempButton.setBounds(450, 445, 90, 20);
		goToMigrationButton.setBounds(575, 445, 90, 20); 
		backgroundPublicRestrictionImageLabel.setBounds(0, 0, 750, 526);

		// publicRestrictionTextPanel
		publicTextRestrictionCheckBox.setBounds(6, 10, 212, 20);
		publicRestrictionTextPagesLabel.setBounds(10, 32, 320, 20);
		publicRestrictionTextPagesTextField.setBounds(10, 57, 355, 20);
		publicRestrictionTextArea.setBounds(10, 87, 360, 170);

		// publicRestrictionImagePanel
		publicImageRestrictionCheckBox.setBounds(6, 10, 192, 20);
		publicRestrictionImageArea.setBounds(10, 35, 320, 40);
		publicImageDropDown.setBounds(10, 75, 150, 20);
		publicImageTextCheckBox.setBounds(6, 105, 270, 20);
		publicRestrictionImageLabel.setBounds(10, 127, 310, 20);
		publicRestrictionImageTextField.setBounds(10, 152, 355, 20);
		publicRestrictionImageTextTypeLabel.setBounds(10, 182, 80, 20);
		publicImageTextDropDown.setBounds(105, 182, 165, 20);
		publicRestrictionImageTextOpacityLabel.setBounds(10, 212, 80, 20);
		publicImageTextOpacityDropDown.setBounds(105, 212, 165, 20);
		publicRestrictionImageTextSizeLabel.setBounds(10, 242, 80, 20);
		publicImageTextSizeDropDown.setBounds(105, 242, 165, 20);

		// publicRestrictionAudioPanel
		publicAudioRestrictionCheckBox.setBounds(6, 10, 252, 20);
		publicRestrictionAudioDurationLabel.setBounds(10, 32, 320, 20);
		publicAudioDurationDropDown.setBounds(10, 57, 100, 20);

		// publicRestrictionVideoPanel
		publicVideoRestrictionCheckBox.setBounds(6, 10, 240, 20);
		publicRestrictionVideoQualityLabel.setBounds(10, 32, 420, 20);
		publicVideoQualityDropDown.setBounds(10, 57, 100, 20);
		publicVideoDurationCheckBox.setBounds(6, 87, 214, 20);
		publicRestrictionVideoDurationLabel.setBounds(10, 109, 320, 20);
		publicVideoDurationDropDown.setBounds(10, 134, 100, 20);

		// migrationPanel
		migrationPanel.setBounds(0, 0, 750, 526);
		migrationConversionLabel.setBounds(255, 70, 330, 20);
		migrationArea.setBounds(255, 100, 450, 125);
		migrationConditionLabel.setBounds(255, 225, 115, 20);
		migrationDropDown.setBounds(370, 227, 240, 20);
		goBackToPublicRestrictionOrTempButton.setBounds(450, 445, 90, 20);
		goToSaveButton.setBounds(575, 445, 90, 20);
		backgroundMigrationImageLabel.setBounds(0, 0, 750, 526);

		// savePanel
		savePanel.setBounds(0, 0, 750, 526);
		saveLabel.setBounds(255, 70, 300, 20);
		settingsOverviewInfoArea.setBounds(255, 100, 450, 25);
		settingsOverviewArea.setBounds(255, 130, 450, 205);
		saveArea.setBounds(255, 350, 460, 30);		
		saveButton.setBounds(400, 390, 90, 20);
		goBackToMigrationButton.setBounds(450, 445, 90, 20);
		goToCreateButton.setBounds(575, 445, 90, 20);
		backgroundSaveImageLabel.setBounds(0, 0, 750, 526);

		// createPanel
		createPanel.setBounds(0, 0, 750, 526);
		createLabel.setBounds(255, 70, 350, 20);
		createArea.setBounds(255, 100, 400, 110);
		compressionCheckBox.setBounds(251, 220, 175, 20);
		progressBar.setBounds(255, 260, 350, 20);
		sipProgressDisplayLabel.setBounds(240, 285, 380, 20);
		sipProgressStepLabel.setBounds(255, 310, 350, 20);
		goBackToSaveButton.setBounds(450, 445, 90, 20);
		createButton.setBounds(575, 445, 90, 20);
		abortButton.setBounds(575, 445, 90, 20);
		quitButton.setBounds(575, 445, 90, 20);
		backgroundCreateImageLabel.setBounds(0, 0, 750, 526);	 
	}

	/**
	 * Adds the GUI elements to the content pane and the panels they belong to
	 */
	private void addElementsToContentPane() {

		getContentPane().add(startActivatedIconButton);
		getContentPane().add(startIconButton);
		getContentPane().add(loadActivatedIconButton);
		getContentPane().add(loadIconButton);
		getContentPane().add(publicationActivatedIconButton);
		getContentPane().add(publicationIconButton);
		getContentPane().add(institutionActivatedIconButton);
		getContentPane().add(institutionIconButton);
		getContentPane().add(institutionTempActivatedIconButton);
		getContentPane().add(institutionTempIconButton);
		getContentPane().add(institutionRestrictionActivatedIconButton);
		getContentPane().add(institutionRestrictionIconButton);
		getContentPane().add(publicActivatedIconButton);
		getContentPane().add(publicIconButton);
		getContentPane().add(publicTempActivatedIconButton);
		getContentPane().add(publicTempIconButton);
		getContentPane().add(publicRestrictionActivatedIconButton);
		getContentPane().add(publicRestrictionIconButton);
		getContentPane().add(migrationActivatedIconButton);
		getContentPane().add(migrationIconButton);
		getContentPane().add(saveActivatedIconButton);
		getContentPane().add(saveIconButton);
		getContentPane().add(createActivatedIconButton);
		getContentPane().add(createIconButton);
		getContentPane().add(helpIconButton);
		getContentPane().add(versionInfoLabel);

		getContentPane().add(startPanel);
		startPanel.add(welcomeLabel);
		startPanel.add(welcomeArea);
		startPanel.add(sourceLabel);
		startPanel.add(sourcePathTextField);
		startPanel.add(sourceChooserButton);
		startPanel.add(destinationLabel);
		startPanel.add(destinationPathTextField);
		startPanel.add(destinationChooserButton);
		startPanel.add(kindOfSIPBuildingDropDown);
		startPanel.add(collectionNameTextField);
		startPanel.add(goToLoadStandardButton);
		startPanel.add(backgroundStartImageLabel);
		startPanel.setLayout(null);

		getContentPane().add(loadStandardPanel);
		loadStandardPanel.add(rightsLabel);
		loadStandardPanel.add(rightsAreaOne);
		loadStandardPanel.add(loadContractButton);
		loadStandardPanel.add(rightsAreaTwo);
		loadStandardPanel.add(standardContractButton);
		loadStandardPanel.add(rightsAreaThree);
		loadStandardPanel.add(goBackToStartButton);
		loadStandardPanel.add(goToPublicButton);
		loadStandardPanel.add(backgroundLoadStandardImageLabel);
		loadStandardPanel.setLayout(null);

		getContentPane().add(publicPanel);
		publicPanel.add(publicLabel);
		publicPanel.add(publicArea);
		publicPanel.add(publicAllowRadioButton);
		publicPanel.add(publicDenyRadioButton);
		publicPanel.add(goBackToLoadStandardButton);
		publicPanel.add(publicDDBArea);
		publicPanel.add(publicDDBCheckBox);
		publicPanel.add(goToPublicTempButton);
		publicPanel.add(backgroundPublicImageLabel);
		publicPanel.setLayout(null);

		getContentPane().add(publicTempPanel);
		publicTempPanel.add(publicStartLabel);
		publicTempPanel.add(publicNoTempRestrictionRadioButton);
		publicTempPanel.add(publicTempRadioButton);
		publicTempPanel.add(publicTempStartDateLabel);
		publicTempPanel.add(publicTempStartDateTextField);
		publicTempPanel.add(publicLawIdDropDown);
		publicTempPanel.add(publicLawRadioButton);
		publicTempPanel.add(goBackToPublicButton);
		publicTempPanel.add(goToPublicRestrictionOrMigrationButton);
		publicTempPanel.add(backgroundPublicTempImageLabel);
		publicTempPanel.setLayout(null);

		getContentPane().add(publicRestrictionPanel);
		publicRestrictionPanel.add(publicRestrictionHeadlineLabel);
		publicRestrictionPanel.add(publicRestrictionArea);
		publicRestrictionPanel.add(publicTabbedPane);
		publicRestrictionPanel.add(goBackToPublicTempButton);
		publicRestrictionPanel.add(goToInstitutionButton); 
		publicRestrictionPanel.add(backgroundPublicRestrictionImageLabel);
		publicRestrictionPanel.setLayout(null);

		publicTextPanel.add(publicTextRestrictionCheckBox);
		publicTextPanel.add(publicRestrictionTextPagesLabel);
		publicTextPanel.add(publicRestrictionTextPagesTextField);
		publicTextPanel.add(publicRestrictionTextArea);
		publicTextPanel.setLayout(null);

		publicImagePanel.add(publicImageRestrictionCheckBox);
		publicImagePanel.add(publicRestrictionImageArea);
		publicImagePanel.add(publicImageDropDown);
		publicImagePanel.add(publicImageTextCheckBox);
		publicImagePanel.add(publicRestrictionImageTextTypeLabel);
		publicImagePanel.add(publicRestrictionImageTextOpacityLabel);
		publicImagePanel.add(publicRestrictionImageTextSizeLabel);
		publicImagePanel.add(publicImageTextDropDown);
		publicImagePanel.add(publicImageTextOpacityDropDown);
		publicImagePanel.add(publicImageTextSizeDropDown);
		publicImagePanel.add(publicRestrictionImageLabel);
		publicImagePanel.add(publicRestrictionImageTextField);
		publicImagePanel.setLayout(null);

		publicAudioPanel.add(publicAudioRestrictionCheckBox);
		publicAudioPanel.add(publicRestrictionAudioDurationLabel);
		publicAudioPanel.add(publicAudioDurationDropDown);
		publicAudioPanel.setLayout(null);

		publicVideoPanel.add(publicVideoRestrictionCheckBox);
		publicVideoPanel.add(publicVideoDurationCheckBox);
		publicVideoPanel.add(publicRestrictionVideoQualityLabel);
		publicVideoPanel.add(publicVideoQualityDropDown);
		publicVideoPanel.add(publicRestrictionVideoDurationLabel);
		publicVideoPanel.add(publicVideoDurationDropDown);
		publicVideoPanel.setLayout(null);
		
		getContentPane().add(institutionPanel);
		institutionPanel.add(institutionLabel);
		institutionPanel.add(institutionArea);
		institutionPanel.add(institutionAllowRadioButton);
		institutionPanel.add(institutionDenyRadioButton);
		institutionPanel.add(goBackToPublicRestrictionOrTempButton);
		institutionPanel.add(goToInstitutionTempButton);
		institutionPanel.add(backgroundInstitutionImageLabel);
		institutionPanel.setLayout(null);

		getContentPane().add(institutionTempPanel);
		institutionTempPanel.add(institutionStartLabel);
		institutionTempPanel.add(institutionNoTempRestrictionRadioButton);
		institutionTempPanel.add(institutionTempRadioButton);
		institutionTempPanel.add(institutionTempStartDateLabel);
		institutionTempPanel.add(institutionTempStartDateTextField);
		institutionTempPanel.add(institutionLawRadioButton);
		institutionTempPanel.add(institutionLawIdDropDown);
		institutionTempPanel.add(goBackToInstitutionButton);
		institutionTempPanel.add(goToInstitutionRestrictionOrPublicButton);
		institutionTempPanel.add(backgroundInstitutionTempImageLabel);
		institutionTempPanel.setLayout(null);

		getContentPane().add(institutionRestrictionPanel);
		institutionRestrictionPanel.add(institutionRestrictionHeadlineLabel);
		institutionRestrictionPanel.add(institutionRestrictionArea);
		institutionRestrictionPanel.add(institutionTabbedPane);
		institutionRestrictionPanel.add(goBackToInstitutionTempButton);
		institutionRestrictionPanel.add(goToMigrationButton);
		institutionRestrictionPanel.add(backgroundInstitutionRestrictionImageLabel);
		institutionRestrictionPanel.setLayout(null);

		institutionTextPanel.add(institutionTextRestrictionCheckBox);
		institutionTextPanel.add(institutionRestrictionTextPagesLabel);
		institutionTextPanel.add(institutionRestrictionTextPagesTextField);
		institutionTextPanel.add(institutionRestrictionTextArea);
		institutionTextPanel.setLayout(null);

		institutionImagePanel.add(institutionImageRestrictionCheckBox);
		institutionImagePanel.add(institutionRestrictionImageArea);
		institutionImagePanel.add(institutionImageDropDown);
		institutionImagePanel.add(institutionImageTextCheckBox);
		institutionImagePanel.add(institutionRestrictionImageTextTypeLabel);
		institutionImagePanel.add(institutionRestrictionImageTextOpacityLabel);
		institutionImagePanel.add(institutionRestrictionImageTextSizeLabel);
		institutionImagePanel.add(institutionImageTextDropDown);
		institutionImagePanel.add(institutionImageTextOpacityDropDown);
		institutionImagePanel.add(institutionImageTextSizeDropDown);
		institutionImagePanel.add(institutionRestrictionImageLabel);
		institutionImagePanel.add(institutionRestrictionImageTextField);
		institutionImagePanel.setLayout(null);

		institutionAudioPanel.add(institutionAudioRestrictionCheckBox);
		institutionAudioPanel.add(institutionRestrictionAudioDurationLabel);
		institutionAudioPanel.add(institutionAudioDurationDropDown);
		institutionAudioPanel.setLayout(null);

		institutionVideoPanel.add(institutionVideoRestrictionCheckBox);
		institutionVideoPanel.add(institutionVideoDurationCheckBox);
		institutionVideoPanel.add(institutionRestrictionVideoQualityLabel);
		institutionVideoPanel.add(institutionVideoQualityDropDown);
		institutionVideoPanel.add(institutionRestrictionVideoDurationLabel);
		institutionVideoPanel.add(institutionVideoDurationDropDown);
		institutionVideoPanel.setLayout(null);

		getContentPane().add(migrationPanel);
		migrationPanel.add(migrationConversionLabel);
		migrationPanel.add(migrationArea);
		migrationPanel.add(migrationConditionLabel);
		migrationPanel.add(migrationDropDown);
		migrationPanel.add(goBackToInstitutionRestrictionOrTempButton);
		migrationPanel.add(goToSaveButton);
		migrationPanel.add(backgroundMigrationImageLabel);
		migrationPanel.setLayout(null);

		getContentPane().add(savePanel);
		savePanel.add(saveLabel);
		savePanel.add(settingsOverviewInfoArea);
		savePanel.add(saveArea);
		savePanel.add(settingsOverviewArea);
		savePanel.add(saveButton);
		savePanel.add(goBackToMigrationButton);
		savePanel.add(goToCreateButton);
		savePanel.add(backgroundSaveImageLabel);
		savePanel.setLayout(null);

		getContentPane().add(createPanel);
		createPanel.add(createLabel);
		createPanel.add(createArea);
		createPanel.add(compressionCheckBox);
		createPanel.add(createButton);
		createPanel.add(abortButton);
		createPanel.add(quitButton);
		createPanel.add(progressBar);
		createPanel.add(sipProgressDisplayLabel);
		createPanel.add(sipProgressStepLabel);
		createPanel.add(goBackToSaveButton);
		createPanel.add(backgroundCreateImageLabel);
		createPanel.setLayout(null);

		getContentPane().setLayout(null);
	}

	/**
	 * Creates action listeners for certain GUI elements to let them react with user input
	 */
	private void createListeners() {

		helpIconButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				messageWriter.showMessage(SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder v" + Utilities.getSipBuilderVersion() + "\n\n" +
										  "Copyright (C) 2014 Historisch-Kulturwissenschaftliche\n" +
										  "Informationsverarbeitung Universität zu Köln\n\n" +
										  "www.danrw.de");
			}

		});
		
		sourceChooserButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				String sourcePath = searchFolder(sourcePathChooser);
				if (sourcePath != null) {
					sourcePathTextField.setText(sourcePath);
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "srcPath.sav"), sourcePath);
					} catch (Exception ex) {
						logger.log("WARNING: Failed to create file " + new File(dataFolderPath + File.separator + "srcPath.sav").getAbsolutePath(), ex);
					}
				}
			}

		});

		destinationChooserButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				String destinationPath = searchFolder(destinationPathChooser);
				if (destinationPath != null) {
					destinationPathTextField.setText(destinationPath);
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "destPath.sav"), destinationPath);
					} catch (Exception ex) {
						logger.log("WARNING: Failed to create file " + new File(dataFolderPath + File.separator + "destPath.sav").getAbsolutePath(), ex);
					}
				}
			}

		});

		goToLoadStandardButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterLoadSection();
			}
		});

		goBackToStartButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterStartSection();
			}
		});

		loadContractButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				File contractRightsFile;
				String contractRightsFilePath = searchXmlFile();
				if (contractRightsFilePath != null) {
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "crloadPath.sav"), contractRightsFilePath);
					} catch (Exception ex) {
						logger.log("WARNING: Failed to create file " + new File(dataFolderPath + File.separator + "crloadPath.sav").getAbsolutePath(), ex);
					}

					contractRightsFile = new File(contractRightsFilePath);

					try {
						sipFactory.getContractRights().loadContractRightsFromFile(contractRightsFile);
					} catch (Exception ex) {
						logger.log("ERROR: Failed to load contract rights from file " + contractRightsFile.getAbsolutePath(), ex);
						messageWriter.showMessage("Beim Einlesen der Datei ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
						return;
					}

					updateValues();			    		 
					messageWriter.showMessage("Die Rechteeinstellungen wurden erfolgreich geladen.", JOptionPane.PLAIN_MESSAGE);
				}
			}

		});

		standardContractButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				
				if (loadStandardRights())
					messageWriter.showMessage("Die Standardeinstellungen wurden erfolgreich geladen.", JOptionPane.PLAIN_MESSAGE);
			}
		});

		goToInstitutionButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationInstitutionSection();
			}
		});

		goBackToLoadStandardButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterLoadSection();
			}
		});

		goToInstitutionTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				if (!enterPublicationInstitutionTempSection())
					enterMigrationSection();
			}
		});

		goBackToInstitutionButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationInstitutionSection();
			}
		});

		goToInstitutionRestrictionOrPublicButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationInstitutionRestrictionsSection();
			}
		});

		goBackToInstitutionTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationInstitutionTempSection();
			}
		});

		goToPublicButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationPublicSection();
			}
		});

		goBackToInstitutionRestrictionOrTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				if (!enterPublicationInstitutionRestrictionsSection())
					enterPublicationInstitutionSection();
			}
		});

		goToPublicTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				if (!enterPublicationPublicTempSection())
					enterPublicationInstitutionSection();
			}
		});

		goBackToPublicButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationPublicSection();
			}
		});

		goToPublicRestrictionOrMigrationButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationPublicRestrictionsSection();
			}
		});

		goBackToPublicTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterPublicationPublicTempSection();
			}
		});

		goToMigrationButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterMigrationSection();
			}
		});

		goBackToPublicRestrictionOrTempButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				if (!enterPublicationPublicRestrictionsSection())
					enterPublicationPublicSection();
			}
		});

		goToSaveButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterSaveSection();
			}
		});

		goBackToMigrationButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterMigrationSection();
			}
		});

		saveButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				String fileName = saveFile("contractRights.xml");

				if (fileName != null && !fileName.equals("")) {
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "crsavePath.sav"), fileName);
					} catch (Exception ex) {
						logger.log("WARNING: Failed to write file " + new File(dataFolderPath + File.separator +
								   "crsavePath.sav").getAbsolutePath(), ex);
					}

					try {
						premisWriter.createContractRightsFile(sipFactory.getContractRights(), new File(fileName));
						messageWriter.showMessage("Ihre Rechte-Einstellungen wurden erfolgreich gespeichert!", JOptionPane.PLAIN_MESSAGE);
					} catch (Exception ex) {
						logger.log("ERROR: Failed to create file " + new File(fileName).getAbsolutePath(), ex);
						messageWriter.showMessage(ex.getMessage() + "\n" + ex.getStackTrace(), JOptionPane.ERROR_MESSAGE);
						messageWriter.showMessage("Beim Erstellen der Datei ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		});

		goToCreateButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterCreateSection();
			}
		});

		goBackToSaveButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){	
				enterSaveSection();
			}
		});

		createButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				compressionCheckBox.setEnabled(false);
				createButton.setEnabled(false);
				createButton.setVisible(false);
				abortButton.setEnabled(true);
				abortButton.setVisible(true);
				goBackToSaveButton.setEnabled(false);
				startActivatedIconButton.setEnabled(false);
				startIconButton.setEnabled(false);
				loadActivatedIconButton.setEnabled(false);
				loadIconButton.setEnabled(false);
				publicationActivatedIconButton.setEnabled(false);
				publicationIconButton.setEnabled(false);
				institutionActivatedIconButton.setEnabled(false);
				institutionIconButton.setEnabled(false);
				institutionTempActivatedIconButton.setEnabled(false);
				institutionTempIconButton.setEnabled(false);
				institutionRestrictionActivatedIconButton.setEnabled(false);
				institutionRestrictionIconButton.setEnabled(false);
				publicActivatedIconButton.setEnabled(false);
				publicIconButton.setEnabled(false);
				publicTempActivatedIconButton.setEnabled(false);
				publicTempIconButton.setEnabled(false);
				publicRestrictionActivatedIconButton.setEnabled(false);
				publicRestrictionIconButton.setEnabled(false);
				migrationActivatedIconButton.setEnabled(false);
				migrationIconButton.setEnabled(false);
				saveActivatedIconButton.setEnabled(false);
				saveIconButton.setEnabled(false);
				createActivatedIconButton.setEnabled(false);
				createIconButton.setEnabled(false);
				
				progressBar.setVisible(true);
				sipProgressDisplayLabel.setVisible(true);
				sipProgressStepLabel.setVisible(true);

				GuiProgressManager progressManager =
						new GuiProgressManager(progressBar,
								sipProgressDisplayLabel, sipProgressStepLabel);

				sipFactory.setCompress(compressionCheckBox.isSelected());
				sipFactory.setProgressManager(progressManager);
				sipFactory.setMessageWriter(messageWriter);
				sipFactory.startSIPBuilding();
			}
		});
		
		abortButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				abortButton.setEnabled(false);
				sipFactory.abort();
			}
		});
		
		quitButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});

		startIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterStartSection();
			}
		});

		loadIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterLoadSection();
			}
		});

		publicationIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationPublicSection();
			}
		});

		publicationActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationPublicSection();
			}
		});

		institutionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationInstitutionSection();
			}
		});

		institutionActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationInstitutionSection();
			}
		});

		institutionTempIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if (!enterPublicationInstitutionTempSection())
					enterPublicationInstitutionSection();
			}
		});

		institutionRestrictionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if (!enterPublicationInstitutionRestrictionsSection())
					enterPublicationInstitutionSection();
			}
		});

		publicIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationPublicSection();
			}
		});

		publicActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterPublicationPublicSection();
			}
		});

		publicTempIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if (!enterPublicationPublicTempSection())
					enterPublicationPublicSection();
			}
		});

		publicRestrictionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if (!enterPublicationPublicRestrictionsSection())
					enterPublicationPublicSection();
			}
		});

		migrationIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterMigrationSection();
			}
		});

		saveIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterSaveSection();
			}
		});

		createIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				enterCreateSection();
			}
		});

		publicAllowRadioButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				publicDDBArea.setEnabled(true);
				publicDDBCheckBox.setEnabled(true);
			}
		});
		
		publicDenyRadioButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				publicDDBArea.setEnabled(false);
				publicDDBCheckBox.setEnabled(false);
			}
		});
		
		institutionNoTempRestrictionRadioButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				if (institutionNoTempRestrictionRadioButton.isSelected()){
					institutionTempStartDateLabel.setEnabled(false);
					institutionTempStartDateTextField.setEditable(false);
					institutionTempStartDateTextField.setEnabled(false);
					institutionLawIdDropDown.setEnabled(false);
				}
			}			
		});

		institutionTempRadioButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionTempRadioButton.isSelected()){
					institutionTempStartDateLabel.setEnabled(true);
					institutionTempStartDateTextField.setEditable(true);
					institutionTempStartDateTextField.setEnabled(true);
					institutionLawIdDropDown.setEnabled(false);					
				}
				else{
					institutionTempStartDateLabel.setEnabled(false);
					institutionTempStartDateTextField.setEditable(false);
					institutionTempStartDateTextField.setEnabled(false);
				}
			}
		});

		institutionLawRadioButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionLawRadioButton.isSelected()) {
					institutionLawIdDropDown.setEnabled(true);
					institutionTempStartDateLabel.setEnabled(false);
					institutionTempStartDateTextField.setEditable(false);
					institutionTempStartDateTextField.setEnabled(false);
				}
				else
					institutionLawIdDropDown.setEnabled(false);
			}
		});

		institutionTextRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionTextRestrictionCheckBox.isSelected()){
					institutionRestrictionTextPagesTextField.setEnabled(true);
					institutionRestrictionTextPagesTextField.setEditable(true);
					institutionRestrictionTextPagesLabel.setEnabled(true);
					institutionRestrictionTextArea.setEnabled(true);
				}
				else{
					institutionRestrictionTextPagesTextField.setEnabled(false);
					institutionRestrictionTextPagesTextField.setEditable(false);
					institutionRestrictionTextPagesLabel.setEnabled(false);
					institutionRestrictionTextArea.setEnabled(false);
				}     
			}
		});

		institutionImageRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionImageRestrictionCheckBox.isSelected()){
					institutionImageDropDown.setEnabled(true);
					institutionRestrictionImageArea.setEnabled(true);
				}
				else{
					institutionImageDropDown.setEnabled(false);
					institutionRestrictionImageArea.setEnabled(false);
				}     
			}
		});

		institutionImageTextCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionImageTextCheckBox.isSelected()){
					institutionRestrictionImageTextField.setEnabled(true);
					institutionRestrictionImageTextField.setEditable(true);
					institutionRestrictionImageLabel.setEnabled(true);
					institutionRestrictionImageLabel.setEnabled(true);
					institutionImageTextDropDown.setEnabled(true);
					institutionRestrictionImageTextTypeLabel.setEnabled(true);

					if (!((String) institutionImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
						institutionRestrictionImageTextOpacityLabel.setEnabled(true);
						institutionImageTextOpacityDropDown.setEnabled(true);
						institutionRestrictionImageTextSizeLabel.setEnabled(true);
						institutionImageTextSizeDropDown.setEnabled(true);
					}	    			 
				}
				else{
					institutionRestrictionImageTextField.setEnabled(false);
					institutionRestrictionImageTextField.setEditable(false);
					institutionRestrictionImageLabel.setEnabled(false);
					institutionRestrictionImageLabel.setEnabled(false);
					institutionImageTextDropDown.setEnabled(false);
					institutionImageTextOpacityDropDown.setEnabled(false);
					institutionRestrictionImageTextTypeLabel.setEnabled(false);
					institutionRestrictionImageTextOpacityLabel.setEnabled(false);
					institutionRestrictionImageTextSizeLabel.setEnabled(false);
					institutionImageTextSizeDropDown.setEnabled(false);
				}     
			}
		});

		institutionAudioRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionAudioRestrictionCheckBox.isSelected()){
					institutionAudioDurationDropDown.setEnabled(true);
					institutionRestrictionAudioDurationLabel.setEnabled(true);
				}
				else{
					institutionAudioDurationDropDown.setEnabled(false);
					institutionRestrictionAudioDurationLabel.setEnabled(false);
				}     
			}
		});

		institutionVideoRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionVideoRestrictionCheckBox.isSelected()){
					institutionVideoQualityDropDown.setEnabled(true);
					institutionRestrictionVideoQualityLabel.setEnabled(true);

				}
				else{
					institutionVideoQualityDropDown.setEnabled(false);
					institutionRestrictionVideoQualityLabel.setEnabled(false);
				}     
			}
		});
		
		institutionVideoDurationCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(institutionVideoDurationCheckBox.isSelected()){
					institutionVideoDurationDropDown.setEnabled(true);
					institutionRestrictionVideoDurationLabel.setEnabled(true);

				}
				else{
					institutionVideoDurationDropDown.setEnabled(false);
					institutionRestrictionVideoDurationLabel.setEnabled(false);
				}     
			}
		});

		publicNoTempRestrictionRadioButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				if (publicNoTempRestrictionRadioButton.isSelected()){
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateTextField.setEditable(false);
					publicTempStartDateTextField.setEnabled(false);
					publicLawIdDropDown.setEnabled(false);
				}
			}			
		});
		
		publicTempRadioButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicTempRadioButton.isSelected()){
					publicTempStartDateLabel.setEnabled(true);
					publicTempStartDateTextField.setEditable(true);
					publicTempStartDateTextField.setEnabled(true);
					publicLawIdDropDown.setEnabled(false);
				}
				else{
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateTextField.setEditable(false);
					publicTempStartDateTextField.setEnabled(false);
				}
			}
		});

		publicLawRadioButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicLawRadioButton.isSelected()) {
					publicLawIdDropDown.setEnabled(true);
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateTextField.setEditable(false);
					publicTempStartDateTextField.setEnabled(false);
				}
				else
					publicLawIdDropDown.setEnabled(false);
			}
		});

		publicTextRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicTextRestrictionCheckBox.isSelected()){
					publicRestrictionTextPagesTextField.setEnabled(true);
					publicRestrictionTextPagesTextField.setEditable(true);
					publicRestrictionTextPagesLabel.setEnabled(true);
					publicRestrictionTextArea.setEnabled(true);
				}
				else{
					publicRestrictionTextPagesTextField.setEnabled(false);
					publicRestrictionTextPagesTextField.setEditable(false);
					publicRestrictionTextPagesLabel.setEnabled(false);
					publicRestrictionTextArea.setEnabled(false);
				}     
			}
		});

		publicImageRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicImageRestrictionCheckBox.isSelected()){
					publicImageDropDown.setEnabled(true);
					publicRestrictionImageArea.setEnabled(true);
				}
				else{
					publicImageDropDown.setEnabled(false);
					publicRestrictionImageArea.setEnabled(false);
				}     
			}
		});

		publicImageTextCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicImageTextCheckBox.isSelected()){
					publicRestrictionImageTextField.setEnabled(true);
					publicRestrictionImageTextField.setEditable(true);
					publicRestrictionImageLabel.setEnabled(true);
					publicRestrictionImageLabel.setEnabled(true);
					publicImageTextDropDown.setEnabled(true);
					publicRestrictionImageTextTypeLabel.setEnabled(true);

					if (!((String) publicImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
						publicRestrictionImageTextOpacityLabel.setEnabled(true);
						publicImageTextOpacityDropDown.setEnabled(true);
						publicRestrictionImageTextSizeLabel.setEnabled(true);
						publicImageTextSizeDropDown.setEnabled(true);
					}
				}
				else{
					publicRestrictionImageTextField.setEnabled(false);
					publicRestrictionImageTextField.setEditable(false);
					publicRestrictionImageLabel.setEnabled(false);
					publicRestrictionImageLabel.setEnabled(false);
					publicImageTextDropDown.setEnabled(false);
					publicImageTextOpacityDropDown.setEnabled(false);
					publicRestrictionImageTextTypeLabel.setEnabled(false);
					publicRestrictionImageTextOpacityLabel.setEnabled(false);
					publicRestrictionImageTextSizeLabel.setEnabled(false);
					publicImageTextSizeDropDown.setEnabled(false);
				}     
			}
		});

		publicAudioRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicAudioRestrictionCheckBox.isSelected()){
					publicAudioDurationDropDown.setEnabled(true);
					publicRestrictionAudioDurationLabel.setEnabled(true);
				}
				else{
					publicAudioDurationDropDown.setEnabled(false);
					publicRestrictionAudioDurationLabel.setEnabled(false);
				}     
			}
		});

		publicVideoRestrictionCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicVideoRestrictionCheckBox.isSelected()){
					publicVideoQualityDropDown.setEnabled(true);
					publicRestrictionVideoQualityLabel.setEnabled(true);
				}
				else{
					publicVideoQualityDropDown.setEnabled(false);
					publicRestrictionVideoQualityLabel.setEnabled(false);
				}     
			}
		});
		
		publicVideoDurationCheckBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){	     
				if(publicVideoDurationCheckBox.isSelected()){
					publicVideoDurationDropDown.setEnabled(true);
					publicRestrictionVideoDurationLabel.setEnabled(true);
				}
				else{
					publicVideoDurationDropDown.setEnabled(false);
					publicRestrictionVideoDurationLabel.setEnabled(false);
				}     
			}
		});
		
		compressionCheckBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				quitButton.setEnabled(false);
				quitButton.setVisible(false);
				createButton.setEnabled(true);
				createButton.setVisible(true);
				
				String compressionSetting = String.valueOf(compressionCheckBox.isSelected()); 
				try {
					Utilities.writeFile(new File(dataFolderPath + File.separator + "compSetting.sav"), compressionSetting);
				} catch (Exception ex) {
					logger.log("WARNING: Failed to create file " + new File(dataFolderPath + File.separator + "compSetting.sav").getAbsolutePath(), ex);
				}
			}			
		});

		kindOfSIPBuildingDropDown.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				if (Utilities.translateKindOfSIPBuilding((String) kindOfSIPBuildingDropDown.getSelectedItem())
						== SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS) {
					;
				} else {
					;
				} 
			}

		});

		institutionImageTextDropDown.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				if (((String) institutionImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
					institutionRestrictionImageTextOpacityLabel.setEnabled(false);
					institutionImageTextOpacityDropDown.setEnabled(false);
					institutionRestrictionImageTextSizeLabel.setEnabled(false);
					institutionImageTextSizeDropDown.setEnabled(false);
				} else {
					institutionRestrictionImageTextOpacityLabel.setEnabled(true);
					institutionImageTextOpacityDropDown.setEnabled(true);
					institutionRestrictionImageTextSizeLabel.setEnabled(true);
					institutionImageTextSizeDropDown.setEnabled(true);
				} 
			}

		});

		publicImageTextDropDown.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				if (((String) publicImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
					publicRestrictionImageTextOpacityLabel.setEnabled(false);
					publicImageTextOpacityDropDown.setEnabled(false);
					publicRestrictionImageTextSizeLabel.setEnabled(false);
					publicImageTextSizeDropDown.setEnabled(false);
				} else {
					publicRestrictionImageTextOpacityLabel.setEnabled(true);
					publicImageTextOpacityDropDown.setEnabled(true);
					publicRestrictionImageTextSizeLabel.setEnabled(true);
					publicImageTextSizeDropDown.setEnabled(true);
				} 
			}

		});

		progressBar.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				if (progressBar.getValue() == 100 ||
						!progressBar.isEnabled()) {

					compressionCheckBox.setEnabled(true);
					abortButton.setEnabled(false);
					abortButton.setVisible(false);

					if (progressBar.getValue() == 100) {					
						quitButton.setEnabled(true);
						quitButton.setVisible(true);
					}
					else if (!progressBar.isEnabled()) {
						createButton.setEnabled(true);
						createButton.setVisible(true);
					}
					goBackToSaveButton.setEnabled(true);
					startActivatedIconButton.setEnabled(true);
					startIconButton.setEnabled(true);
					loadActivatedIconButton.setEnabled(true);
					loadIconButton.setEnabled(true);
					publicationActivatedIconButton.setEnabled(true);
					publicationIconButton.setEnabled(true);
					institutionActivatedIconButton.setEnabled(true);
					institutionIconButton.setEnabled(true);
					institutionTempActivatedIconButton.setEnabled(true);
					institutionTempIconButton.setEnabled(true);
					institutionRestrictionActivatedIconButton.setEnabled(true);
					institutionRestrictionIconButton.setEnabled(true);
					publicActivatedIconButton.setEnabled(true);
					publicIconButton.setEnabled(true);
					publicTempActivatedIconButton.setEnabled(true);
					publicTempIconButton.setEnabled(true);
					publicRestrictionActivatedIconButton.setEnabled(true);
					publicRestrictionIconButton.setEnabled(true);
					migrationActivatedIconButton.setEnabled(true);
					migrationIconButton.setEnabled(true);
					saveActivatedIconButton.setEnabled(true);
					saveIconButton.setEnabled(true);
					createActivatedIconButton.setEnabled(true);
					createIconButton.setEnabled(true);
				}
			}

		});
	}

	/**
	 * Restores the file paths and compression setting chosen by the user in the last session
	 */
	private void loadLastSessionSettings() {
		File sourceFolderFile = new File(dataFolderPath + File.separator + "srcPath.sav");
		File destinationFolderFile = new File(dataFolderPath + File.separator + "destPath.sav");
		File contractRightsLoadFolderFile = new File(dataFolderPath + File.separator + "crloadPath.sav");
		File contractRightsSaveFolderFile = new File(dataFolderPath + File.separator + "crsavePath.sav");
		File compressionSettingFile = new File(dataFolderPath + File.separator + "compSetting.sav");

		if (sourceFolderFile.exists()) {
			try {
				String sourceFolderPath = Utilities.readFile(sourceFolderFile);
				sourcePathChooser.setCurrentDirectory(new File(sourceFolderPath));				
			} catch (Exception e) {
				logger.log("WARNING: Failed to read file " + sourceFolderFile.getAbsolutePath(), e);
			}
		}

		if (destinationFolderFile.exists()) {
			try {
				String destinationFolderPath = Utilities.readFile(destinationFolderFile);
				destinationPathChooser.setCurrentDirectory(new File(destinationFolderPath));				
			} catch (Exception e) {
				logger.log("WARNING: Failed to read file " + destinationFolderFile.getAbsolutePath(), e);
			}
		}

		if (contractRightsLoadFolderFile.exists()) {
			try {
				String contractFileLoadPath = Utilities.readFile(contractRightsLoadFolderFile);
				contractFileLoadPathChooser.setSelectedFile(new File(contractFileLoadPath));				
			} catch (Exception e) {
				logger.log("WARNING: Failed to read file " + contractRightsLoadFolderFile.getAbsolutePath(), e);
			}
		}

		if (contractRightsSaveFolderFile.exists()) {
			try {
				String contractFileSavePath = Utilities.readFile(contractRightsSaveFolderFile);
				contractFileSavePathChooser.setSelectedFile(new File(contractFileSavePath));				
			} catch (Exception e) {
				logger.log("WARNING: Failed to read file " + contractRightsSaveFolderFile.getAbsolutePath(), e);
			}
		}
		
		if (compressionSettingFile.exists()) {
			try {
				String compressionSetting = Utilities.readFile(compressionSettingFile);
				compressionCheckBox.setSelected(Boolean.valueOf(compressionSetting));
			} catch (Exception e) {
				logger.log("WARNING: Failed to read file " + compressionSettingFile.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Checks if the current section (= panel) contains invalid settings/user input and makes the current panel invisible if no
	 * problems exist
	 * 
	 * @return true if the section could be left, otherwise false
	 */
	private boolean leaveSection() {

		if (startPanel.isVisible()) {

			UserInputValidator.Feedback feedback = UserInputValidator.checkPaths(sourcePathTextField.getText(),
					destinationPathTextField.getText(),
					Utilities.translateKindOfSIPBuilding(
							(String) kindOfSIPBuildingDropDown.getSelectedItem()));

			switch(feedback) {
			case NO_SOURCE_PATH:
				messageWriter.showMessage("Bitte geben Sie einen Quellordner an.", JOptionPane.ERROR_MESSAGE);
				return false;

			case SOURCE_PATH_DOES_NOT_EXIST:
				messageWriter.showMessage("Der von Ihnen angegebene Quellordner existiert nicht.", JOptionPane.ERROR_MESSAGE);
				return false;		    			

			case NO_DESTINATION_PATH:
				messageWriter.showMessage("Bitte geben Sie einen Zielordner an.", JOptionPane.ERROR_MESSAGE);
				return false;

			case FOLDER_EQUALITY:
				messageWriter.showMessage("Bitte stellen Sie sicher, dass Quell- und Zielordner nicht identisch sind.", JOptionPane.ERROR_MESSAGE);
				return false;

			case SUBFOLDER:
				messageWriter.showMessage("Bitte stellen Sie sicher, dass der Zielordner kein Unterordner des Quellordners ist. ", JOptionPane.ERROR_MESSAGE);
				return false;

			case NON_DIRECTORY_FILES_EXIST:
				messageWriter.showMessage("Das von Ihnen angegebene Quellverzeichnis enthält Dateien, die keine Ordner sind.\n" +
						"Bitte überprüfen Sie, ob Sie die richtige Einstellung zur SIP-Generierung gewählt haben.", JOptionPane.ERROR_MESSAGE);
				return false;

			default:
				break;		    			
			}

			if (collectionCheckBox.isSelected()) {
				feedback = UserInputValidator.checkCollectionName(collectionNameTextField.getText(), destinationPathTextField.getText());

				switch(feedback) {
				case NO_COLLECTION_NAME:
					messageWriter.showMessage("Bitte geben Sie den gewünschten Namen der Lieferung an.", JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_COLLECTION_NAME:
					messageWriter.showMessage("Der von Ihnen gewählte Lieferungsname enthält Zeichen, die auf manchen Betriebssystemen für\n" +
							"die Benennung von Dateien nicht erlaubt sind.", JOptionPane.ERROR_MESSAGE);
					return false;

				case COLLECTION_ALREADY_EXISTS:
					messageWriter.showMessage("Im Zielverzeichnis existiert bereits eine Lieferung namens \"" + collectionNameTextField.getText() + "\".\n" + 
							"Bitte löschen Sie die bestehende Lieferung oder wählen Sie einen anderen Namen.", JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}
		}

		if (institutionTempPanel.isVisible()) {

			if (institutionTempRadioButton.isSelected()) {
				UserInputValidator.Feedback feedback = UserInputValidator.checkDate(institutionTempStartDateTextField.getText());

				switch(feedback) {

				case NO_DATE:
					messageWriter.showMessage("Bitte geben Sie ein Startdatum für die Publikation an.", JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_DATE:
					messageWriter.showMessage("Das von Ihnen angegebene Startdatum für die Publikation\n" +
							"existiert nicht oder ist nicht korrekt formuliert.\n\n" +
							"Bitte geben Sie das Datum in der Form [Tag].[Monat].[Jahr] an.\n" +
							"(Beispiel: 14.07.2020)", JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}			 
		}

		if (publicTempPanel.isVisible()) {

			if (publicTempRadioButton.isSelected()) {
				UserInputValidator.Feedback feedback = UserInputValidator.checkDate(publicTempStartDateTextField.getText());

				switch(feedback) {

				case NO_DATE:
					messageWriter.showMessage("Bitte geben Sie ein Startdatum für die Publikation an.", JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_DATE:
					messageWriter.showMessage("Das von Ihnen angegebene Startdatum für die Publikation\n" +
							"existiert nicht oder ist nicht korrekt formuliert.\n\n" +
							"Bitte geben Sie das Datum in der Form [Tag].[Monat].[Jahr] an.\n" +
							"(Beispiel: 14.07.2020)", JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}
		}

		if (institutionRestrictionPanel.isVisible()) {

			if (institutionImageTextCheckBox.isSelected()) {
				if (institutionRestrictionImageTextField.getText() == null ||
						institutionRestrictionImageTextField.getText().equals("")) {

					if (Utilities.translateTextType((String) institutionImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer)
						messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.", JOptionPane.ERROR_MESSAGE);
					else
						messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.", JOptionPane.ERROR_MESSAGE);

					return false;
				}

				int imageTextSize = institutionRestrictionImageTextField.getText().length();
				if (Utilities.translateTextType((String) institutionImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer
						&& imageTextSize > 65) {
					messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n" +
							"Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");
					return false;
				}

				if (Utilities.translateTextType((String) institutionImageTextDropDown.getSelectedItem()) != PublicationRights.TextType.footer
						&& imageTextSize > 20) {
					messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n" +
							"Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");
					return false;
				}
			}

			if (institutionTextRestrictionCheckBox.isSelected()) {
				
				String pages = institutionRestrictionTextPagesTextField.getText();
				
				if (pages.equals("")) {
					messageWriter.showMessage("Bitte geben Sie die Seitennummern der zu\n" +
							"berücksichtigenden Seiten an.", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				sipFactory.getContractRights().getInstitutionRights().setPages(pages);
				if (sipFactory.getContractRights().getInstitutionRights().parsePages().equals("")) {
					messageWriter.showMessage("Die Angabe der Seitenzahlen ist nicht \n" +
							"korrekt formuliert.", JOptionPane.ERROR_MESSAGE);
					return false;
				}				
			}
		}

		if (publicRestrictionPanel.isVisible()) {

			if (publicImageTextCheckBox.isSelected()) {
				if (publicRestrictionImageTextField.getText() == null ||
						publicRestrictionImageTextField.getText().equals("")) {

					if (Utilities.translateTextType((String) publicImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer)
						messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.", JOptionPane.ERROR_MESSAGE);
					else
						messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.", JOptionPane.ERROR_MESSAGE);

					return false;
				}

				int imageTextSize = publicRestrictionImageTextField.getText().length();
				if (Utilities.translateTextType((String) publicImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer
						&& imageTextSize > 65) {
					messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n" +
							"Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");
					return false;
				}

				if (Utilities.translateTextType((String) publicImageTextDropDown.getSelectedItem()) != PublicationRights.TextType.footer
						&& imageTextSize > 20) {
					messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n" +
							"Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");
					return false;
				}
			}

			if (publicTextRestrictionCheckBox.isSelected()) {
				
				String pages = publicRestrictionTextPagesTextField.getText();
				
				if (pages.equals("")) {
					messageWriter.showMessage("Bitte geben Sie die Seitennummern der zu\n" +
							"berücksichtigenden Seiten an.", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				sipFactory.getContractRights().getPublicRights().setPages(pages);				
				if (sipFactory.getContractRights().getPublicRights().parsePages().equals("")) {
					messageWriter.showMessage("Die Angabe der Seitenzahlen ist nicht \n" +
							"korrekt formuliert.", JOptionPane.ERROR_MESSAGE);
					return false;
				}				
			}	
		}
		
		if (createPanel.isVisible()) {
			
			progressBar.setVisible(false);
			sipProgressDisplayLabel.setVisible(false);
			sipProgressStepLabel.setVisible(false);
			quitButton.setEnabled(false);
			quitButton.setVisible(false);
			createButton.setEnabled(true);
			createButton.setVisible(true);
		}

		startActivatedIconButton.setVisible(false);
		startIconButton.setVisible(true);
		loadActivatedIconButton.setVisible(false);
		loadIconButton.setVisible(true);
		publicationActivatedIconButton.setVisible(false);
		publicationIconButton.setVisible(true);
		institutionActivatedIconButton.setVisible(false);
		institutionIconButton.setVisible(true);
		institutionTempActivatedIconButton.setVisible(false);
		institutionTempIconButton.setVisible(true);
		institutionRestrictionActivatedIconButton.setVisible(false);
		institutionRestrictionIconButton.setVisible(true);
		publicActivatedIconButton.setVisible(false);
		publicIconButton.setVisible(true);
		publicTempActivatedIconButton.setVisible(false);
		publicTempIconButton.setVisible(true);
		publicRestrictionActivatedIconButton.setVisible(false);
		publicRestrictionIconButton.setVisible(true);
		migrationActivatedIconButton.setVisible(false);
		migrationIconButton.setVisible(true);
		saveActivatedIconButton.setVisible(false);
		saveIconButton.setVisible(true);
		createActivatedIconButton.setVisible(false);
		createIconButton.setVisible(true);

		startPanel.setVisible(false);
		loadStandardPanel.setVisible(false);
		institutionPanel.setVisible(false);
		institutionTempPanel.setVisible(false);
		institutionRestrictionPanel.setVisible(false);
		publicPanel.setVisible(false);
		publicTempPanel.setVisible(false);
		publicRestrictionPanel.setVisible(false);
		migrationPanel.setVisible(false);
		savePanel.setVisible(false);
		createPanel.setVisible(false);

		return true;
	}

	/**
	 * Shows the start panel
	 */
	private void enterStartSection() {

		if (!startPanel.isVisible() && leaveSection()) {
			startPanel.setVisible(true);
			startActivatedIconButton.setVisible(true);
			startIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the load panel
	 */
	private void enterLoadSection() {

		if (!loadStandardPanel.isVisible() && leaveSection()) {
			loadStandardPanel.setVisible(true);
			loadActivatedIconButton.setVisible(true);
			loadIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the publication for institution panel
	 */
	private void enterPublicationInstitutionSection() {

		if (!institutionPanel.isVisible() && leaveSection()) {
			institutionPanel.setVisible(true);
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			institutionActivatedIconButton.setVisible(true);
			institutionIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the publication for institution temp restriction panel
	 * 
	 * @return false if publication for institution is deactivated and the section
	 * can't be entered, otherwise true
	 */
	private boolean enterPublicationInstitutionTempSection() {

		if (institutionDenyRadioButton.isSelected())
			return false;
		else if (!institutionTempPanel.isVisible() && leaveSection()) {
			institutionTempPanel.setVisible(true);			 
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			institutionActivatedIconButton.setVisible(true);
			institutionIconButton.setVisible(false);
			institutionTempActivatedIconButton.setVisible(true);
			institutionTempIconButton.setVisible(false);
		}

		return true;
	}

	/**
	 * Shows the publication for institution restrictions panel
	 * 
	 * @return false if publication for institution is deactivated and the section
	 * can't be entered, otherwise true
	 */
	private boolean enterPublicationInstitutionRestrictionsSection() {

		if (institutionDenyRadioButton.isSelected())
			return false;		 
		else if (!institutionRestrictionPanel.isVisible() && leaveSection()) {
			institutionRestrictionPanel.setVisible(true);			 
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			institutionActivatedIconButton.setVisible(true);
			institutionIconButton.setVisible(false);
			institutionRestrictionActivatedIconButton.setVisible(true);
			institutionRestrictionIconButton.setVisible(false);
		}

		return true;
	}

	/**
	 * Shows the publication for public panel
	 */
	private void enterPublicationPublicSection() {

		if (!publicPanel.isVisible() && leaveSection()) {
			publicPanel.setVisible(true);
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			publicActivatedIconButton.setVisible(true);
			publicIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the publication for public temp restriction panel 
	 * 
	 * @return false if publication for public is deactivated and the section
	 * can't be entered, otherwise true
	 */
	private boolean enterPublicationPublicTempSection() {

		if (publicDenyRadioButton.isSelected())
			return false;
		else if (!publicTempPanel.isVisible() && leaveSection()) {
			publicTempPanel.setVisible(true);			 
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			publicActivatedIconButton.setVisible(true);
			publicIconButton.setVisible(false);
			publicTempActivatedIconButton.setVisible(true);
			publicTempIconButton.setVisible(false);
		}

		return true;
	}

	/**
	 * Shows the publication for public restrictions panel
	 * 
	 * @return false if publication for public is deactivated and the section
	 * can't be entered, otherwise true
	 */
	private boolean enterPublicationPublicRestrictionsSection() {

		if (publicDenyRadioButton.isSelected())
			return false;		  
		else if (!publicRestrictionPanel.isVisible() && leaveSection()) {
			publicRestrictionPanel.setVisible(true);			 
			publicationActivatedIconButton.setVisible(true);
			publicationIconButton.setVisible(false);
			publicActivatedIconButton.setVisible(true);
			publicIconButton.setVisible(false);
			publicRestrictionActivatedIconButton.setVisible(true);
			publicRestrictionIconButton.setVisible(false);
		}

		return true;
	}

	/**
	 * Shows the migration panel
	 */
	private void enterMigrationSection() {

		if (!migrationPanel.isVisible() && leaveSection()) {
			migrationPanel.setVisible(true);
			migrationActivatedIconButton.setVisible(true);
			migrationIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the save panel
	 */
	private void enterSaveSection() {

		if (!savePanel.isVisible() && leaveSection()) {
			
			updateContractRights();
			
			settingsOverviewTextArea.setText(createSettingsOverviewText());
			settingsOverviewTextArea.setCaretPosition(0);

			savePanel.setVisible(true);
			saveActivatedIconButton.setVisible(true);
			saveIconButton.setVisible(false);
		}
	}

	/**
	 * Shows the create panel
	 */
	private void enterCreateSection() {

		if (!createPanel.isVisible() && leaveSection()) {
			
			updateContractRights();
			
			createPanel.setVisible(true);
			createActivatedIconButton.setVisible(true);
			createIconButton.setVisible(false);
		}
	}
	
	/**
	 * Loads standard rights from standardRights.xml
	 * 
	 * @return true if the rights were loaded successfully, otherwise false 
	 */
	private boolean loadStandardRights() {
		
		File standardRightsFile = new File(confFolderPath + File.separator + "standardRights.xml");

		if (!standardRightsFile.exists()) {
			messageWriter.showMessage("Die Datei \"" + confFolderPath + File.separator + "standardRights.xml\", in der die Standardrechte\n" +
					"hinterlegt sind, konnte nicht gefunden werden.", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			sipFactory.getContractRights().loadContractRightsFromFile(standardRightsFile);
		} catch (Exception ex) {
			logger.log("ERROR: Failed to load standard rights from file " + standardRightsFile.getAbsolutePath(), ex);
			messageWriter.showMessage("Beim Einlesen der Standardrechte ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		updateValues();
		
		return true;
	}
	
	/**
	 * Copies the GUI settings made by the user to the contract rights
	 */
	private void updateContractRights() {
		
		ContractRights contractRights = sipFactory.getContractRights();
		PublicationRights institutionRights = contractRights.getInstitutionRights();
		PublicationRights publicRights = contractRights.getPublicRights();

		sipFactory.setKindofSIPBuilding((String) kindOfSIPBuildingDropDown.getSelectedItem());
		sipFactory.setSourcePath(sourcePathTextField.getText());
		sipFactory.setDestinationPath(destinationPathTextField.getText());
		sipFactory.setCreateCollection(collectionCheckBox.isSelected());
		sipFactory.setCollectionName(collectionNameTextField.getText());
		institutionRights.setAllowPublication(institutionAllowRadioButton.isSelected());
		institutionRights.setTempPublication(institutionTempRadioButton.isSelected());
		institutionRights.setLawPublication(institutionLawRadioButton.isSelected());
		institutionRights.setStartDate(institutionTempStartDateTextField.getText());
		institutionRights.setLaw(Utilities.translateLaw((String) institutionLawIdDropDown.getSelectedItem()));
		institutionRights.setTextRestriction(institutionTextRestrictionCheckBox.isSelected());
		institutionRights.setImageRestriction(institutionImageRestrictionCheckBox.isSelected());
		institutionRights.setAudioRestriction(institutionAudioRestrictionCheckBox.isSelected());
		institutionRights.setVideoRestriction(institutionVideoRestrictionCheckBox.isSelected());
		institutionRights.setVideoDurationRestriction(institutionVideoDurationCheckBox.isSelected());
		institutionRights.setImageWidth(contractSettings.getWidthImage(institutionImageDropDown.getSelectedIndex()));
		institutionRights.setImageHeight(contractSettings.getHeightImage(institutionImageDropDown.getSelectedIndex()));
		institutionRights.setImageRestrictionText(institutionImageTextCheckBox.isSelected());
		institutionRights.setFooterText(institutionRestrictionImageTextField.getText());
		institutionRights.setImageTextType(Utilities.translateTextType((String) institutionImageTextDropDown.getSelectedItem()));
		institutionRights.setWatermarkSize(contractSettings.getTextSizeImage(institutionImageTextSizeDropDown.getSelectedIndex()));
		institutionRights.setWatermarkOpacity(contractSettings.getOpacityImage(institutionImageTextOpacityDropDown.getSelectedIndex()));
		institutionRights.setAudioDuration(contractSettings.getDuration(institutionAudioDurationDropDown.getSelectedIndex()));
		institutionRights.setVideoSize(contractSettings.getHeightVideo(institutionVideoQualityDropDown.getSelectedIndex()));
		institutionRights.setVideoDuration(contractSettings.getDuration(institutionVideoDurationDropDown.getSelectedIndex()));
		publicRights.setAllowPublication(publicAllowRadioButton.isSelected());
		publicRights.setTempPublication(publicTempRadioButton.isSelected());
		publicRights.setLawPublication(publicLawRadioButton.isSelected());
		publicRights.setStartDate(publicTempStartDateTextField.getText());
		publicRights.setLaw(Utilities.translateLaw((String) publicLawIdDropDown.getSelectedItem()));
		publicRights.setTextRestriction(publicTextRestrictionCheckBox.isSelected());
		publicRights.setImageRestriction(publicImageRestrictionCheckBox.isSelected());
		publicRights.setAudioRestriction(publicAudioRestrictionCheckBox.isSelected());
		publicRights.setVideoRestriction(publicVideoRestrictionCheckBox.isSelected());
		publicRights.setVideoDurationRestriction(publicVideoDurationCheckBox.isSelected());
		publicRights.setImageWidth(contractSettings.getWidthImage(publicImageDropDown.getSelectedIndex()));
		publicRights.setImageHeight(contractSettings.getHeightImage(publicImageDropDown.getSelectedIndex()));
		publicRights.setImageRestrictionText(publicImageTextCheckBox.isSelected());
		publicRights.setFooterText(publicRestrictionImageTextField.getText());
		publicRights.setImageTextType(Utilities.translateTextType((String) publicImageTextDropDown.getSelectedItem()));
		publicRights.setWatermarkSize(contractSettings.getTextSizeImage(publicImageTextSizeDropDown.getSelectedIndex()));
		publicRights.setWatermarkOpacity(contractSettings.getOpacityImage(publicImageTextOpacityDropDown.getSelectedIndex()));
		publicRights.setAudioDuration(contractSettings.getDuration(publicAudioDurationDropDown.getSelectedIndex()));
		publicRights.setVideoSize(contractSettings.getHeightVideo(publicVideoQualityDropDown.getSelectedIndex()));
		publicRights.setVideoDuration(contractSettings.getDuration(publicVideoDurationDropDown.getSelectedIndex()));
		contractRights.setConversionCondition((String) migrationDropDown.getSelectedItem());
		contractRights.setDdbExclusion(!publicDDBCheckBox.isSelected());
	}

	/**
	 * Copies the contract rights settings to the respective GUI elements
	 */
	private void updateValues() {

		ContractRights contractRights = sipFactory.getContractRights();
		PublicationRights institutionRights = contractRights.getInstitutionRights();
		PublicationRights publicRights = contractRights.getPublicRights();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

		int widthImageIndex = contractSettings.getWidthImageIndex(institutionRights.getImageWidth());
		int heightImageIndex = contractSettings.getHeightImageIndex(institutionRights.getImageHeight());
		if (widthImageIndex == heightImageIndex && widthImageIndex >= 0)
			institutionImageDropDown.setSelectedIndex(widthImageIndex);
		else
			institutionImageDropDown.setSelectedIndex(0);

		institutionRestrictionImageTextField.setText(institutionRights.getFooterText());

		institutionImageTextDropDown.setSelectedItem(Utilities.translateTextType(institutionRights.getImageTextType()));

		int imageTextOpacityIndex = contractSettings.getOpacityImageIndex(institutionRights.getWatermarkOpacity());
		if (imageTextOpacityIndex >= 0)
			institutionImageTextOpacityDropDown.setSelectedIndex(imageTextOpacityIndex);
		else
			institutionImageTextOpacityDropDown.setSelectedIndex(0);

		int imageTextSizeIndex = contractSettings.getTextSizeImageIndex(institutionRights.getWatermarkSize());
		if (imageTextSizeIndex >= 0)
			institutionImageTextSizeDropDown.setSelectedIndex(imageTextSizeIndex);
		else
			institutionImageTextSizeDropDown.setSelectedIndex(0);

		int audioDurationIndex = contractSettings.getDurationIndex(institutionRights.getAudioDuration());
		if (audioDurationIndex >= 0)
			institutionAudioDurationDropDown.setSelectedIndex(audioDurationIndex);
		else
			institutionAudioDurationDropDown.setSelectedIndex(0);

		int videoSizeIndex = contractSettings.getHeightVideoIndex(institutionRights.getVideoSize());
		if (videoSizeIndex >= 0)
			institutionVideoQualityDropDown.setSelectedIndex(videoSizeIndex);
		else
			institutionVideoQualityDropDown.setSelectedIndex(0);

		int videoDurationIndex = contractSettings.getDurationIndex(institutionRights.getVideoDuration());
		if (videoDurationIndex >= 0)
			institutionVideoDurationDropDown.setSelectedIndex(videoDurationIndex);
		else
			institutionVideoDurationDropDown.setSelectedIndex(0);

		if (institutionRights.getTempPublication()) {
			institutionTempStartDateLabel.setEnabled(true);
			institutionTempStartDateTextField.setEditable(true);
			institutionTempStartDateTextField.setEnabled(true);
		} else {
			institutionTempStartDateLabel.setEnabled(false);
			institutionTempStartDateTextField.setEditable(false);
			institutionTempStartDateTextField.setEnabled(false);
		}

		if (institutionRights.getLawPublication())
			institutionLawIdDropDown.setEnabled(true);
		else
			institutionLawIdDropDown.setEnabled(false);

		if (institutionRights.getTextRestriction()) {
			institutionRestrictionTextPagesTextField.setEnabled(true);
			institutionRestrictionTextPagesTextField.setEditable(true);
			institutionRestrictionTextPagesLabel.setEnabled(true);
			institutionRestrictionTextArea.setEnabled(true);
		}
		else {
			institutionRestrictionTextPagesTextField.setEnabled(false);
			institutionRestrictionTextPagesTextField.setEditable(false);
			institutionRestrictionTextPagesLabel.setEnabled(false);
			institutionRestrictionTextArea.setEnabled(false);
		}     

		institutionImageDropDown.setEnabled(false);
		institutionRestrictionImageArea.setEnabled(false);
		institutionRestrictionImageTextField.setEnabled(false);
		institutionRestrictionImageTextField.setEditable(false);
		institutionRestrictionImageLabel.setEnabled(false);
		institutionImageTextDropDown.setEnabled(false);
		institutionImageTextOpacityDropDown.setEnabled(false);
		institutionImageTextSizeDropDown.setEnabled(false);	
		institutionRestrictionImageTextTypeLabel.setEnabled(false);
		institutionRestrictionImageTextOpacityLabel.setEnabled(false);
		institutionRestrictionImageTextSizeLabel.setEnabled(false);		

		if (institutionRights.getImageRestriction()) {
			institutionImageDropDown.setEnabled(true);
			institutionRestrictionImageArea.setEnabled(true);
		}

		if (institutionRights.getImageRestrictionText()) {
			institutionRestrictionImageTextField.setEnabled(true);
			institutionRestrictionImageTextField.setEditable(true);
			institutionRestrictionImageLabel.setEnabled(true);
			institutionImageTextDropDown.setEnabled(true);
			institutionRestrictionImageTextTypeLabel.setEnabled(true);

			if (institutionRights.getImageTextType() != PublicationRights.TextType.footer) {
				institutionRestrictionImageTextOpacityLabel.setEnabled(true);
				institutionImageTextOpacityDropDown.setEnabled(true);
				institutionRestrictionImageTextSizeLabel.setEnabled(true);
				institutionImageTextSizeDropDown.setEnabled(true);	
			}			
		}		

		if (institutionRights.getAudioRestriction()) {
			institutionAudioDurationDropDown.setEnabled(true);
			institutionRestrictionAudioDurationLabel.setEnabled(true);
		} else {
			institutionAudioDurationDropDown.setEnabled(false);
			institutionRestrictionAudioDurationLabel.setEnabled(false);
		}     

		if (institutionRights.getVideoRestriction()) {
			institutionVideoQualityDropDown.setEnabled(true);
			institutionRestrictionVideoQualityLabel.setEnabled(true);
		} else {
			institutionVideoQualityDropDown.setEnabled(false);
			institutionRestrictionVideoQualityLabel.setEnabled(false);
		}
		
		if (institutionRights.getVideoDurationRestriction()) {
			institutionVideoDurationDropDown.setEnabled(true);
			institutionRestrictionVideoDurationLabel.setEnabled(true);
		} else {
			institutionVideoDurationDropDown.setEnabled(false);
			institutionRestrictionVideoDurationLabel.setEnabled(false);
		}

		institutionAllowRadioButton.setSelected(institutionRights.getAllowPublication());
		institutionDenyRadioButton.setSelected(!institutionRights.getAllowPublication());
		institutionNoTempRestrictionRadioButton.setSelected(true);
		institutionTempRadioButton.setSelected(institutionRights.getTempPublication());
		institutionLawRadioButton.setSelected(institutionRights.getLawPublication());
		if (institutionRights.getStartDate() != null)
			institutionTempStartDateTextField.setText(dateFormat.format(institutionRights.getStartDate()));
		else
			institutionTempStartDateTextField.setText("");
		institutionLawIdDropDown.setSelectedItem(Utilities.translateLaw(institutionRights.getLaw()));

		institutionTextRestrictionCheckBox.setSelected(institutionRights.getTextRestriction());
		institutionImageRestrictionCheckBox.setSelected(institutionRights.getImageRestriction());
		institutionImageTextCheckBox.setSelected(institutionRights.getImageRestrictionText());
		institutionAudioRestrictionCheckBox.setSelected(institutionRights.getAudioRestriction());
		institutionVideoRestrictionCheckBox.setSelected(institutionRights.getVideoRestriction());
		institutionVideoDurationCheckBox.setSelected(institutionRights.getVideoDurationRestriction());

		institutionRestrictionTextPagesTextField.setText(institutionRights.getPages());

		publicImageTextDropDown.setSelectedItem(Utilities.translateTextType(publicRights.getImageTextType()));

		imageTextOpacityIndex = contractSettings.getOpacityImageIndex(publicRights.getWatermarkOpacity());
		if (imageTextOpacityIndex >= 0)
			publicImageTextOpacityDropDown.setSelectedIndex(imageTextOpacityIndex);
		else
			publicImageTextOpacityDropDown.setSelectedIndex(0);

		imageTextSizeIndex = contractSettings.getTextSizeImageIndex(publicRights.getWatermarkSize());
		if (imageTextSizeIndex >= 0)
			publicImageTextSizeDropDown.setSelectedIndex(imageTextSizeIndex);
		else
			publicImageTextSizeDropDown.setSelectedIndex(0);

		audioDurationIndex = contractSettings.getDurationIndex(publicRights.getAudioDuration());
		if (audioDurationIndex >= 0)
			publicAudioDurationDropDown.setSelectedIndex(audioDurationIndex);
		else
			publicAudioDurationDropDown.setSelectedIndex(0);

		videoSizeIndex = contractSettings.getHeightVideoIndex(publicRights.getVideoSize());
		if (videoSizeIndex >= 0)
			publicVideoQualityDropDown.setSelectedIndex(videoSizeIndex);
		else
			publicVideoQualityDropDown.setSelectedIndex(0);

		videoDurationIndex = contractSettings.getDurationIndex(publicRights.getVideoDuration());
		if (videoDurationIndex >= 0)
			publicVideoDurationDropDown.setSelectedIndex(videoDurationIndex);
		else
			publicVideoDurationDropDown.setSelectedIndex(0);

		if (publicRights.getTempPublication()) {
			publicTempStartDateLabel.setEnabled(true);
			publicTempStartDateTextField.setEditable(true);
			publicTempStartDateTextField.setEnabled(true);
		}
		else {
			publicTempStartDateLabel.setEnabled(false);
			publicTempStartDateTextField.setEditable(false);
			publicTempStartDateTextField.setEnabled(false);
		}

		if (publicRights.getLawPublication())
			publicLawIdDropDown.setEnabled(true);
		else
			publicLawIdDropDown.setEnabled(false);

		if (publicRights.getTextRestriction()) {
			publicRestrictionTextPagesTextField.setEnabled(true);
			publicRestrictionTextPagesTextField.setEditable(true);
			publicRestrictionTextPagesLabel.setEnabled(true);
			publicRestrictionTextArea.setEnabled(true);
		}
		else {
			publicRestrictionTextPagesTextField.setEnabled(false);
			publicRestrictionTextPagesTextField.setEditable(false);
			publicRestrictionTextPagesLabel.setEnabled(false);
			publicRestrictionTextArea.setEnabled(false);
		}     

		publicImageDropDown.setEnabled(false);
		publicRestrictionImageArea.setEnabled(false);
		publicRestrictionImageTextField.setEnabled(false);
		publicRestrictionImageTextField.setEditable(false);
		publicRestrictionImageLabel.setEnabled(false);
		publicImageTextDropDown.setEnabled(false);
		publicImageTextOpacityDropDown.setEnabled(false);
		publicImageTextSizeDropDown.setEnabled(false);	
		publicRestrictionImageTextTypeLabel.setEnabled(false);
		publicRestrictionImageTextOpacityLabel.setEnabled(false);
		publicRestrictionImageTextSizeLabel.setEnabled(false);		

		if (publicRights.getImageRestriction()) {
			publicImageDropDown.setEnabled(true);
			publicRestrictionImageArea.setEnabled(true);
		}

		if (publicRights.getImageRestrictionText()) {
			publicRestrictionImageTextField.setEnabled(true);
			publicRestrictionImageTextField.setEditable(true);
			publicRestrictionImageLabel.setEnabled(true);
			publicImageTextDropDown.setEnabled(true);
			publicRestrictionImageTextTypeLabel.setEnabled(true);

			if (publicRights.getImageTextType() != PublicationRights.TextType.footer) {
				publicRestrictionImageTextOpacityLabel.setEnabled(true);
				publicImageTextOpacityDropDown.setEnabled(true);
				publicRestrictionImageTextSizeLabel.setEnabled(true);
				publicImageTextSizeDropDown.setEnabled(true);	
			}			
		}			

		if (publicRights.getAudioRestriction()) {
			publicAudioDurationDropDown.setEnabled(true);
			publicRestrictionAudioDurationLabel.setEnabled(true);
		}
		else{
			publicAudioDurationDropDown.setEnabled(false);
			publicRestrictionAudioDurationLabel.setEnabled(false);
		}     

		if (publicRights.getVideoRestriction()) {
			publicVideoQualityDropDown.setEnabled(true);
			publicRestrictionVideoQualityLabel.setEnabled(true);
		}
		else{
			publicVideoQualityDropDown.setEnabled(false);
			publicRestrictionVideoQualityLabel.setEnabled(false);
		}
		
		if (publicRights.getVideoDurationRestriction()) {
			publicVideoDurationDropDown.setEnabled(true);
			publicRestrictionVideoDurationLabel.setEnabled(true);
		}
		else{
			publicVideoDurationDropDown.setEnabled(false);
			publicRestrictionVideoDurationLabel.setEnabled(false);
		}

		publicAllowRadioButton.setSelected(publicRights.getAllowPublication());
		publicDenyRadioButton.setSelected(!publicRights.getAllowPublication());
		
		if (publicRights.getAllowPublication()) {
			publicDDBArea.setEnabled(true);
			publicDDBCheckBox.setEnabled(true);
		} else {
			publicDDBArea.setEnabled(false);
			publicDDBCheckBox.setEnabled(false);
		}
		
		publicNoTempRestrictionRadioButton.setSelected(true);
		publicTempRadioButton.setSelected(publicRights.getTempPublication());
		publicLawRadioButton.setSelected(publicRights.getLawPublication());
		if (publicRights.getStartDate() != null)
			publicTempStartDateTextField.setText(dateFormat.format(publicRights.getStartDate()));
		else
			publicTempStartDateTextField.setText("");
		publicLawIdDropDown.setSelectedItem(Utilities.translateLaw(publicRights.getLaw()));

		publicTextRestrictionCheckBox.setSelected(publicRights.getTextRestriction());
		publicImageRestrictionCheckBox.setSelected(publicRights.getImageRestriction());
		publicImageTextCheckBox.setSelected(publicRights.getImageRestrictionText());
		publicAudioRestrictionCheckBox.setSelected(publicRights.getAudioRestriction());
		publicVideoRestrictionCheckBox.setSelected(publicRights.getVideoRestriction());
		publicVideoDurationCheckBox.setSelected(publicRights.getVideoDurationRestriction());

		publicRestrictionTextPagesTextField.setText(publicRights.getPages());

		widthImageIndex = contractSettings.getWidthImageIndex(publicRights.getImageWidth());
		heightImageIndex = contractSettings.getHeightImageIndex(publicRights.getImageHeight());
		if (widthImageIndex == heightImageIndex && widthImageIndex >= 0)
			publicImageDropDown.setSelectedIndex(widthImageIndex);
		else
			publicImageDropDown.setSelectedIndex(0);

		publicRestrictionImageTextField.setText(publicRights.getFooterText());

		migrationDropDown.setSelectedItem(Utilities.translateConversionCondition(contractRights.getConversionCondition()));
		publicDDBCheckBox.setSelected(!contractRights.getDdbExclusion());
	}
	
	/**
	 * Creates a text that summarizes the settings chosen by the user
	 * 
	 * @return The settings overview text
	 */
	private String createSettingsOverviewText() {
		ContractRights contractRights = sipFactory.getContractRights();
		PublicationRights institutionRights = contractRights.getInstitutionRights();
		PublicationRights publicRights = contractRights.getPublicRights();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		 	
		String settingsOverview = "Publikation für die Öffentlichkeit: ";
		if (publicRights.getAllowPublication()) {
			settingsOverview += "Ja\n";
			settingsOverview += "     DDB-Harvesting: ";
			if (contractRights.getDdbExclusion())
				settingsOverview += "Nein\n";
			else
				settingsOverview += "Ja\n";
			if (publicRights.getTempPublication())
				settingsOverview += "     Startdatum: " + dateFormat.format(publicRights.getStartDate()) + "\n";
			else if (publicRights.getLawPublication())
				settingsOverview += "     Sperrgesetz: " + Utilities.translateLaw(publicRights.getLaw()) + "\n";
			if (publicRights.getTextRestriction())
				settingsOverview += "     Angezeigte Textseiten: " + publicRights.getPages() + "\n";
			if (publicRights.getImageRestriction())
					settingsOverview += "     Bildqualität: " + (String) publicImageDropDown.getSelectedItem() + "\n";
			if (publicRights.getImageRestrictionText()) {
				if (publicRights.getImageTextType() == PublicationRights.TextType.footer)
					settingsOverview += "     Fußzeile: \"" + publicRights.getFooterText() + "\"\n";
				else {
					settingsOverview += "     Wasserzeichen: \"" + publicRights.getFooterText() + "\"\n";
					settingsOverview += "          Position: " + Utilities.translateTextTypePosition(publicRights.getImageTextType()) + "\n";
					settingsOverview += "          Sichtbarkeit: " + publicRights.getWatermarkOpacity() + "%\n";
					settingsOverview += "          Schriftgröße: " + publicRights.getWatermarkSize() + "\n";
				}					
			}
			if (publicRights.getAudioRestriction())
				settingsOverview += "     Maximale Abspieldauer von Audiodateien: " + publicRights.getAudioDuration() + " Sekunden\n";
			if (publicRights.getVideoRestriction())
				settingsOverview += "     Bildqualität von Videodateien: " + (String) publicVideoQualityDropDown.getSelectedItem() + "\n";
			if (publicRights.getVideoDurationRestriction())	
				settingsOverview += "     Maximale Abspieldauer von Videodateien: " + publicRights.getVideoDuration() + " Sekunden\n";
			
		} else
			settingsOverview += "Nein\n";
		
		settingsOverview += "\n";
		
		settingsOverview += "Publikation für die eigene Institution: ";
		if (institutionRights.getAllowPublication()) {
			settingsOverview += "Ja\n";
			if (institutionRights.getTempPublication())
				settingsOverview += "     Startdatum: " + dateFormat.format(institutionRights.getStartDate()) + "\n";
			else if (institutionRights.getLawPublication())
				settingsOverview += "     Sperrgesetz: " + Utilities.translateLaw(institutionRights.getLaw()) + "\n";
			if (institutionRights.getTextRestriction())
				settingsOverview += "     Angezeigte Textseiten: " + institutionRights.getPages() + "\n";
			if (institutionRights.getImageRestriction())
					settingsOverview += "     Bildqualität: " + (String) institutionImageDropDown.getSelectedItem() + "\n";
			if (institutionRights.getImageRestrictionText()) {
				if (institutionRights.getImageTextType() == PublicationRights.TextType.footer)
					settingsOverview += "     Fußzeile: \"" + institutionRights.getFooterText() + "\"\n";
				else {
					settingsOverview += "     Wasserzeichen: \"" + institutionRights.getFooterText() + "\"\n";
					settingsOverview += "          Position: " + Utilities.translateTextTypePosition(institutionRights.getImageTextType()) + "\n";
					settingsOverview += "          Sichtbarkeit: " + institutionRights.getWatermarkOpacity() + "%\n";
					settingsOverview += "          Schriftgröße: " + institutionRights.getWatermarkSize() + "\n";
				}					
			}
			if (institutionRights.getAudioRestriction())
				settingsOverview += "     Maximale Abspieldauer von Audiodateien: " + institutionRights.getAudioDuration() + " Sekunden\n";
			if (institutionRights.getVideoRestriction())
				settingsOverview += "     Bildqualität von Videodateien: " + (String) institutionVideoQualityDropDown.getSelectedItem() + "\n";
			if (institutionRights.getVideoDurationRestriction())	
				settingsOverview += "     Maximale Abspieldauer von Videodateien: " + institutionRights.getVideoDuration() + " Sekunden\n";
		} else
			settingsOverview += "Nein\n";
		
		settingsOverview += "\nMigrationsbedingung: " + (String) migrationDropDown.getSelectedItem();
			
		return settingsOverview;
	}

	/**
	 * Shows a file dialog that lets the user choose a certain directory
	 * 
	 * @param fileChooser The file chooser object used to display the file dialog
	 * @return The path to the folder chosen by the user
	 */
	private String searchFolder(JFileChooser fileChooser) {

		File selectedFile = null;

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			selectedFile = fileChooser.getSelectedFile();

			if (selectedFile != null)
				return selectedFile.getPath() + File.separator;
			else
				return null;
		}
		else
			return null;
	}

	/**
	 * Shows a file dialog that lets the user choose a certain xml file
	 * 
	 * @return The path to the xml file chosen by the user
	 */
	private String searchXmlFile() {

		File selectedFile = null;

		contractFileLoadPathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		contractFileLoadPathChooser.setAcceptAllFileFilterUsed(false);
		
		contractFileLoadPathChooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				return f.getName().toLowerCase().endsWith(".xml");
			}
			public String getDescription() {
				return "XML-Datei (*.xml)";
			}
		});

		if (contractFileLoadPathChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selectedFile = contractFileLoadPathChooser.getSelectedFile();
			if (selectedFile != null)
				return selectedFile.getPath();
			else
				return null;
		}

		return null;
	}

	/**
	 * Saves the contract rights settings to an xml file
	 * 
	 * @param defaultFileName The default name of the target file 
	 * @return The path to the newly created xml file 
	 */
	private String saveFile(String defaultFileName) {

		File selectedFile = null;

		contractFileSavePathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		contractFileSavePathChooser.setAcceptAllFileFilterUsed(false);
		if (contractFileSavePathChooser.getSelectedFile() == null)
			contractFileSavePathChooser.setSelectedFile(new File(defaultFileName));
		
		contractFileSavePathChooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				return f.getName().toLowerCase().endsWith(".xml");
			}
			public String getDescription() {
				return "XML-Datei (*.xml)";
			}
		});

		if (contractFileSavePathChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			selectedFile = contractFileSavePathChooser.getSelectedFile();
			String filePath = selectedFile.getPath();

			if (!filePath.endsWith(".xml"))
				filePath += ".xml";

			return filePath;
		}

		return null;
	}

}
