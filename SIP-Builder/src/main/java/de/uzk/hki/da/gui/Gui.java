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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toedter.calendar.JDateChooser;

import de.uzk.hki.da.gui.panels.HistoriePanel;
import de.uzk.hki.da.gui.panels.UebertragenPanel;
import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.metadata.ContractRights;
import de.uzk.hki.da.metadata.ContractRights.CCLicense;
import de.uzk.hki.da.metadata.ContractSettings;
import de.uzk.hki.da.metadata.PremisXmlWriter;
import de.uzk.hki.da.metadata.PublicationRights;
import de.uzk.hki.da.sb.Feedback;
import de.uzk.hki.da.sb.SIPFactory;
import de.uzk.hki.da.sb.UserInputValidator;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.SetMaxText;
import de.uzk.hki.da.utils.StringUtilities;
import de.uzk.hki.da.utils.Utilities;

/**
 * Displays the graphical user interface and reacts to user input
 * 
 * @author Thomas Kleinke
 * @author Martin Fischer
 * @author Polina Gubaidullina
 * @author Josef Hammer
 */

public class Gui extends JFrame {

	private static Logger logger = LogManager.getLogger(Gui.class);

	private static final long serialVersionUID = -2783837120567684391L;

	private String buildNumber = Utilities.getBuildNumber();

	SIPFactory sipFactory = new SIPFactory();
	ContractSettings contractSettings = null;
	PremisXmlWriter premisWriter = new PremisXmlWriter();
	GuiMessageWriter messageWriter = new GuiMessageWriter();

	JFileChooser sourcePathChooser;

	JFileChooser destinationPathChooser;
	JFileChooser workingPathChooser;
	JFileChooser contractFileLoadPathChooser;
	JFileChooser contractFileSavePathChooser;

	ClassLoader classloader = Gui.class.getClassLoader();

	Font standardFont, boldFont;
	Font standardFont12;

	String confFolderPath, dataFolderPath, scriptFolderPath;

	int startPanelWidth = 760;
	int startPanelHeight = 566;

	// GUI Elements

	// Panels
	JPanel startPanel;
	JPanel loadStandardPanel;
	JPanel institutionPanel;
	JPanel institutionTempPanel;
	JPanel institutionRestrictionPanel;
	JPanel publicPanel;
	JPanel publicLicensePanel;
	JPanel publicTempPanel;
	JPanel publicRestrictionPanel;
	JPanel migrationPanel;
	JPanel savePanel;
	JPanel createPanel;

	UebertragenPanel uebertragenPanel;
	HistoriePanel historiePanel;

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

	// Content panes
	private CardLayout cardLayout;
	private JPanel contentCardPanel;
	private JPanel sidebarPanel;
	private final Map<JPanel, String> cardNames = new HashMap<JPanel, String>();
	private final Map<JPanel, SidebarButton> sidebarButtonsByCard = new HashMap<JPanel, SidebarButton>();
	private final List<SidebarButton> sidebarButtons = new ArrayList<SidebarButton>();

	// Labels
	JLabel backgroundStartImageLabel;
	JLabel backgroundLoadStandardImageLabel;
	JLabel backgroundLicenseImageLabel;
	JLabel backgroundInstitutionImageLabel;
	JLabel backgroundInstitutionTempImageLabel;
	JLabel backgroundInstitutionRestrictionImageLabel;
	JLabel backgroundPublicImageLabel;
	JLabel backgroundPublicTempImageLabel;
	JLabel backgroundPublicRestrictionImageLabel;
	JLabel backgroundMigrationImageLabel;
	JLabel backgroundSaveImageLabel;
	JLabel backgroundCreateImageLabel;
	JLabel backgroundUebertragenImageLabel;
	JLabel backgroundHistorieImageLabel;

	JLabel versionInfoLabel;
	JLabel welcomeLabel;
	JLabel sourceLabel;
	JLabel destinationLabel;
	JLabel workingLabel;
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
	JLabel licenseLabel;
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
	JTextArea licenseAreaOne;
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
	JButton workingChooserButton;
	JButton goToLoadStandardButton;
	JButton loadContractButton;
	JButton standardContractButton;
	JButton goBackToStartButton;
	JButton goToLicenseButton;
	JButton goBackToLicenseOrInstitutionButton;
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
	JButton goToPublicRestrictionButton;
	JButton goBackToPublicTempButton;
	JButton goToMigrationButton;
	JButton goBackToPublicRestrictionOrPublicButton;
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
	JButton licenseActivatedIconButton;
	JButton licenseIconButton;
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
	JButton uebertragenIconButton;
	JButton uebertragenActivatedIconButton;
	JButton historieIconButton;
	JButton historieActivatedIconButton;

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
	JRadioButton metadataLicenseRadioButton;
	JRadioButton premisLicenseRadioButton;

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
	JTextField workingPathTextField;
	JTextField licenseTextField;
	JTextField collectionNameTextField;
	JDateChooser institutionTempStartDateChooserField;
	JTextField institutionRestrictionTextPagesTextField;
	JTextField institutionRestrictionImageTextField;
	JDateChooser publicTempStartDateChooserField;
	JTextField publicRestrictionTextPagesTextField;
	JTextField publicRestrictionImageTextField;

	// Comboboxes
	JComboBox<String> kindOfSIPBuildingDropDown;
	JComboBox<CCLicense> licenseDropDown;
	JComboBox<String> institutionLawIdDropDown;
	JComboBox<String> institutionImageDropDown;
	JComboBox<String> institutionImageTextDropDown;
	JComboBox<String> institutionImageTextOpacityDropDown;
	JComboBox<String> institutionImageTextSizeDropDown;
	JComboBox<String> institutionAudioDurationDropDown;
	JComboBox<String> institutionVideoQualityDropDown;
	JComboBox<String> institutionVideoDurationDropDown;
	JComboBox<String> publicLawIdDropDown;
	JComboBox<String> publicImageDropDown;
	JComboBox<String> publicImageTextDropDown;
	JComboBox<String> publicImageTextOpacityDropDown;
	JComboBox<String> publicImageTextSizeDropDown;
	JComboBox<String> publicAudioDurationDropDown;
	JComboBox<String> publicVideoQualityDropDown;
	JComboBox<String> publicVideoDurationDropDown;
	JComboBox<String> migrationDropDown;

	// ProgressBar
	JProgressBar progressBar;

	int oldTabPublic = -1;
	int oldTabInstitution = -1;

	// Steuerung, damit keine Prüfung auf die Eingabefelder gemacht wird
	// Benötigt für Übertragung und History
	boolean uebertragenHistory = false;

	public class ScrollBarCustomUI extends BasicScrollBarUI {
		@Override
		protected void installComponents() {
			super.installComponents();
		}

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return super.createDecreaseButton(orientation);
		}

		@Override
		protected void paintThumb(Graphics graphics, JComponent c, Rectangle thumbBounds) {
			Graphics2D g2g = (Graphics2D) graphics;
			g2g.setColor(GuiTheme.BORDER_DEFAULT);
			g2g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
			super.paintThumb(g2g, c, thumbBounds);
		}
	}

	public Gui(String confFolderPath, String dataFolderPath, String scriptFolderPath) {

		GuiTheme.initialize();

		standardFont = GuiTheme.getRegularFont();
		boldFont = GuiTheme.getBoldFont();
		standardFont12 = standardFont.deriveFont(12.0f);

		Toolkit toolkit = Toolkit.getDefaultToolkit();

		UIManager.put("ScrollBarUI", ScrollBarCustomUI.class.getName());
		UIManager.put("List.font", standardFont12);

		sourcePathChooser = new JFileChooser(new File("."));
		destinationPathChooser = new JFileChooser(new File("."));
		workingPathChooser = new JFileChooser(new File("."));
		contractFileLoadPathChooser = new JFileChooser(new File("."));
		contractFileSavePathChooser = new JFileChooser(new File("."));

		this.confFolderPath = confFolderPath;
		this.dataFolderPath = dataFolderPath;
		this.scriptFolderPath = scriptFolderPath;
		if (!new File(dataFolderPath).exists())
			new File(dataFolderPath).mkdir();

		URL icon = classloader.getResource("images/sipBuilderIcon.png");
		URL logo = classloader.getResource(SIPBuilder.getProperties().getProperty("LOGO_FILE"));
		ImageIcon iconImage = icon != null ? new ImageIcon(icon) : null;
		ImageIcon logoImage = logo != null ? new ImageIcon(logo) : null;
		if (iconImage != null && iconImage.getImage() != null) {
			this.setIconImage(iconImage.getImage());
		}

		messageWriter.setGui(this);
		messageWriter.setIconImage(logoImage != null ? logoImage.getImage() : null);

		try {
			contractSettings = new ContractSettings(confFolderPath);
		} catch (Exception e) {
			logger.error("Failed to load contract settings file", e);
			messageWriter.showMessage("Die Contract Settings konnten nicht geladen werden.\n"
					+ "Die Datei \"settings.xml\" im Verzeichnis \"conf\" wurde möglicherweise\n"
					+ "verändert oder gelöscht.", JOptionPane.ERROR_MESSAGE);
			System.exit(Feedback.GUI_ERROR.toInt());
		}

		initialize();

		if (!loadStandardRights())
			System.exit(Feedback.GUI_ERROR.toInt());
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
		progressBar.setBackground(GuiTheme.PROGRESS_BG);
		progressBar.setForeground(GuiTheme.PROGRESS_BAR);

		defineElementPositions();
		addElementsToContentPane();
		oldTabPublic = publicTabbedPane.getSelectedIndex();
		oldTabInstitution = institutionTabbedPane.getSelectedIndex();
		createListeners();

		loadLastSessionSettings();

		showCard(startPanel);
	}

	/**
	 * Creates the panels ( = SIP-Builder steps)
	 */
	private void initializePanels() {

		institutionTabbedPane = new JTabbedPane();
		institutionTabbedPane.setPreferredSize(new Dimension(430, 380));

		institutionTextPanel = new JPanel();
		institutionTextPanel.setOpaque(true);
		institutionTextPanel.setBackground(GuiTheme.BG_CARD);
		institutionTabbedPane.addTab("Text", institutionTextPanel);
		institutionImagePanel = new JPanel();
		institutionImagePanel.setOpaque(true);
		institutionImagePanel.setBackground(GuiTheme.BG_CARD);
		institutionTabbedPane.addTab("Bild", institutionImagePanel);
		institutionAudioPanel = new JPanel();
		institutionAudioPanel.setOpaque(true);
		institutionAudioPanel.setBackground(GuiTheme.BG_CARD);
		institutionTabbedPane.addTab("Audio", institutionAudioPanel);
		institutionVideoPanel = new JPanel();
		institutionVideoPanel.setOpaque(true);
		institutionVideoPanel.setBackground(GuiTheme.BG_CARD);
		institutionTabbedPane.addTab("Video", institutionVideoPanel);
		institutionTabbedPane.setBackgroundAt(0, Color.WHITE);
		institutionTabbedPane.setBackgroundAt(1, Color.WHITE);
		institutionTabbedPane.setBackgroundAt(2, Color.WHITE);
		institutionTabbedPane.setBackgroundAt(3, Color.WHITE);
		institutionTabbedPane.setBackground(Color.WHITE);
		institutionTabbedPane.updateUI();

		publicTabbedPane = new JTabbedPane();
		publicTabbedPane.setPreferredSize(new Dimension(430, 380));

		publicTextPanel = new JPanel();
		publicTextPanel.setOpaque(true);
		publicTextPanel.setBackground(GuiTheme.BG_CARD);
		publicTabbedPane.addTab("Text", publicTextPanel);
		publicImagePanel = new JPanel();
		publicImagePanel.setOpaque(true);
		publicImagePanel.setBackground(GuiTheme.BG_CARD);
		publicTabbedPane.addTab("Bild", publicImagePanel);
		publicAudioPanel = new JPanel();
		publicAudioPanel.setOpaque(true);
		publicAudioPanel.setBackground(GuiTheme.BG_CARD);
		publicTabbedPane.addTab("Audio", publicAudioPanel);
		publicVideoPanel = new JPanel();
		publicVideoPanel.setOpaque(true);
		publicVideoPanel.setBackground(GuiTheme.BG_CARD);
		publicTabbedPane.addTab("Video", publicVideoPanel);
		publicTabbedPane.setBackgroundAt(0, Color.WHITE);
		publicTabbedPane.setBackgroundAt(1, Color.WHITE);
		publicTabbedPane.setBackgroundAt(2, Color.WHITE);
		publicTabbedPane.setBackgroundAt(3, Color.WHITE);

		publicTabbedPane.setBackground(Color.WHITE);
		publicTabbedPane.updateUI();

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
		publicLicensePanel = new JPanel();
		publicLicensePanel.setVisible(false);
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

		uebertragenPanel = new UebertragenPanel(this, dataFolderPath, scriptFolderPath, false, classloader,
				standardFont, boldFont);
		historiePanel = new HistoriePanel(false, classloader);

	}

	/**
	 * Creates the labels responsible for diplaying the background image
	 */
	private void initializeImageLabels() {

		URL backgroundImage = classloader.getResource(SIPBuilder.getProperties().getProperty("BACKGROUND_FILE"));

		backgroundStartImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundLoadStandardImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundLicenseImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionTempImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundInstitutionRestrictionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicTempImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundPublicRestrictionImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundMigrationImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundSaveImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundCreateImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundUebertragenImageLabel = new JLabel(new ImageIcon(backgroundImage));
		backgroundHistorieImageLabel = new JLabel(new ImageIcon(backgroundImage));
	}

	/**
	 * Creates the text labels
	 */
	private void initializeTextLabels() {
		versionInfoLabel = new JLabel(
				"SIP-Builder Build: " + buildNumber + ", LVR-InfoKom (ab 2014). HKI, Universität zu Köln 2011-2014.");
		versionInfoLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		versionInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		versionInfoLabel.setForeground(GuiTheme.TEXT_ON_PRIMARY);
		welcomeLabel = new JLabel("Herzlich Willkommen im SIP-Builder!");
		welcomeLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		welcomeLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		sourceLabel = new JLabel("Quellordner:");
		sourceLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		sourceLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		destinationLabel = new JLabel("Zielordner:");
		destinationLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		destinationLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		workingLabel = new JLabel("Arbeitsordner:");
		workingLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		workingLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		rightsLabel = new JLabel("Rechteeinstellungen");
		rightsLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		rightsLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		licenseLabel = new JLabel("Lizenzangaben");
		licenseLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		licenseLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		institutionLabel = new JLabel("eigene Institution");
		institutionLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		institutionLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		institutionStartLabel = new JLabel("Startzeitpunkt der Publikation");
		institutionStartLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		institutionStartLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		institutionTempStartDateLabel = new JLabel("Startdatum der institutionellen Publikation:");
		institutionTempStartDateLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionTempStartDateLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionTempStartDateLabel.setEnabled(false);
		institutionRestrictionHeadlineLabel = new JLabel("Vorschaurestriktionen für die eigene Institution");
		institutionRestrictionHeadlineLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		institutionRestrictionHeadlineLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		institutionRestrictionTextPagesLabel = new JLabel("Welche Seiten sollen angezeigt werden?");
		institutionRestrictionTextPagesLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionTextPagesLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionTextPagesLabel.setEnabled(false);
		institutionRestrictionImageLabel = new JLabel("Bitte geben Sie den gewünschten Text an (max. 65 Zeichen):");
		institutionRestrictionImageLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionImageLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionImageLabel.setEnabled(false);
		institutionRestrictionImageTextTypeLabel = new JLabel("Texttyp:");
		institutionRestrictionImageTextTypeLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionImageTextTypeLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionImageTextTypeLabel.setEnabled(false);
		institutionRestrictionImageTextOpacityLabel = new JLabel("Sichtbarkeit:");
		institutionRestrictionImageTextOpacityLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionImageTextOpacityLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionImageTextOpacityLabel.setEnabled(false);
		institutionRestrictionImageTextSizeLabel = new JLabel("Schriftgröße:");
		institutionRestrictionImageTextSizeLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionImageTextSizeLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionImageTextSizeLabel.setEnabled(false);
		institutionRestrictionAudioDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		institutionRestrictionAudioDurationLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionAudioDurationLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionAudioDurationLabel.setEnabled(false);
		institutionRestrictionVideoQualityLabel = new JLabel(
				"In welcher Qualität sollen Ihre Videos abgespielt werden?");
		institutionRestrictionVideoQualityLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionVideoQualityLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionVideoQualityLabel.setEnabled(false);
		institutionRestrictionVideoDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		institutionRestrictionVideoDurationLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		institutionRestrictionVideoDurationLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionVideoDurationLabel.setEnabled(false);
		publicLabel = new JLabel("Publikation für die Öffentlichkeit");
		publicLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		publicLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		publicStartLabel = new JLabel("Startzeitpunkt der Publikation");
		publicStartLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		publicStartLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		publicTempStartDateLabel = new JLabel("Startdatum der öffentlichen Publikation:");
		publicTempStartDateLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicTempStartDateLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicTempStartDateLabel.setEnabled(false);
		publicRestrictionHeadlineLabel = new JLabel("Vorschaurestriktionen für die Öffentlichkeit");
		publicRestrictionHeadlineLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		publicRestrictionHeadlineLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		publicRestrictionTextPagesLabel = new JLabel("Welche Seiten sollen angezeigt werden?");
		publicRestrictionTextPagesLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionTextPagesLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionTextPagesLabel.setEnabled(false);
		publicRestrictionImageLabel = new JLabel("Bitte geben Sie den gewünschten Text an (max. 65 Zeichen):");
		publicRestrictionImageLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionImageLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionImageLabel.setEnabled(false);
		publicRestrictionImageTextTypeLabel = new JLabel("Texttyp:");
		publicRestrictionImageTextTypeLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionImageTextTypeLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionImageTextTypeLabel.setEnabled(false);
		publicRestrictionImageTextOpacityLabel = new JLabel("Sichtbarkeit:");
		publicRestrictionImageTextOpacityLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionImageTextOpacityLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionImageTextOpacityLabel.setEnabled(false);
		publicRestrictionImageTextSizeLabel = new JLabel("Schriftgröße:");
		publicRestrictionImageTextSizeLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionImageTextSizeLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionImageTextSizeLabel.setEnabled(false);
		publicRestrictionAudioDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		publicRestrictionAudioDurationLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionAudioDurationLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionAudioDurationLabel.setEnabled(false);
		publicRestrictionVideoQualityLabel = new JLabel("In welcher Qualität sollen Ihre Videos abgespielt werden?");
		publicRestrictionVideoQualityLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionVideoQualityLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionVideoQualityLabel.setEnabled(false);
		publicRestrictionVideoDurationLabel = new JLabel("Wieviele Sekunden sollen abspielbar sein?");
		publicRestrictionVideoDurationLabel.setEnabled(false);
		publicRestrictionVideoDurationLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		publicRestrictionVideoDurationLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		migrationConversionLabel = new JLabel("Konversions- und Migrationseinstellungen");
		migrationConversionLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		migrationConversionLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		migrationConditionLabel = new JLabel("Migrationsbedingung:");
		migrationConditionLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		migrationConditionLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		saveLabel = new JLabel("Einstellungen speichern");
		saveLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		saveLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		createLabel = new JLabel("SIP-Generierungsprozess starten");
		createLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
		createLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		sipProgressDisplayLabel = new JLabel();
		sipProgressDisplayLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		sipProgressDisplayLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		sipProgressDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
		sipProgressStepLabel = new JLabel();
		sipProgressStepLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		sipProgressStepLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		sipProgressStepLabel.setHorizontalAlignment(JLabel.CENTER);
	}

	/**
	 * Creates the buttons
	 */
	private void initializeButtons() {

		helpIconButton = new ThemeButton(ThemeButton.Style.SECONDARY, "?");
		helpIconButton.setPreferredSize(GuiTheme.smallButtonSize());

		sourceChooserButton = createChooserButton();
		destinationChooserButton = createChooserButton();
		workingChooserButton = createChooserButton();

		goToLoadStandardButton = createForwardButton();
		loadContractButton = createPrimaryButton("Laden");
		standardContractButton = createSecondaryButton("Standard");
		goBackToStartButton = createBackButton();
		goToLicenseButton = createForwardButton();
		goBackToLicenseOrInstitutionButton = createBackButton();
		goToInstitutionButton = createForwardButton();
		goBackToLoadStandardButton = createBackButton();
		goToInstitutionTempButton = createForwardButton();
		goBackToInstitutionButton = createBackButton();
		goToInstitutionRestrictionOrPublicButton = createForwardButton();
		goBackToInstitutionTempButton = createBackButton();
		goToPublicButton = createForwardButton();
		goBackToInstitutionRestrictionOrTempButton = createBackButton();
		goToPublicTempButton = createForwardButton();
		goBackToPublicButton = createBackButton();
		goToPublicRestrictionButton = createForwardButton();
		goBackToPublicTempButton = createBackButton();
		goToMigrationButton = createForwardButton();
		goBackToPublicRestrictionOrPublicButton = createBackButton();
		goToSaveButton = createForwardButton();
		saveButton = createPrimaryButton("Speichern");
		goBackToMigrationButton = createBackButton();
		goToCreateButton = createForwardButton();
		createButton = createPrimaryButton("Erstellen");
		goBackToSaveButton = createBackButton();
		abortButton = createSecondaryButton("Abbrechen");
		quitButton = createSecondaryButton("Beenden");

		createPlaceholderIconButtonPair();
	}

	private JButton createPrimaryButton(String text) {
		ThemeButton button = new ThemeButton(ThemeButton.Style.PRIMARY, text);
		button.setPreferredSize(GuiTheme.buttonSize());
		return button;
	}

	private JButton createSecondaryButton(String text) {
		ThemeButton button = new ThemeButton(ThemeButton.Style.SECONDARY, text);
		button.setPreferredSize(GuiTheme.buttonSize());
		return button;
	}

	private JButton createForwardButton() {
		JButton button = createPrimaryButton("Weiter →");
		return button;
	}

	private JButton createBackButton() {
		JButton button = createSecondaryButton("← Zurück");
		return button;
	}

	private JButton createChooserButton() {
		ThemeButton button = new ThemeButton(ThemeButton.Style.SECONDARY, "...");
		button.setPreferredSize(GuiTheme.smallButtonSize());
		return button;
	}

	private void createPlaceholderIconButtonPair() {
		startActivatedIconButton = new JButton();
		startIconButton = new JButton();
		loadActivatedIconButton = new JButton();
		loadIconButton = new JButton();
		licenseActivatedIconButton = new JButton();
		licenseIconButton = new JButton();
		publicationActivatedIconButton = new JButton();
		publicationIconButton = new JButton();
		institutionActivatedIconButton = new JButton();
		institutionIconButton = new JButton();
		institutionTempActivatedIconButton = new JButton();
		institutionTempIconButton = new JButton();
		institutionRestrictionActivatedIconButton = new JButton();
		institutionRestrictionIconButton = new JButton();
		publicActivatedIconButton = new JButton();
		publicIconButton = new JButton();
		publicTempActivatedIconButton = new JButton();
		publicTempIconButton = new JButton();
		publicRestrictionActivatedIconButton = new JButton();
		publicRestrictionIconButton = new JButton();
		migrationActivatedIconButton = new JButton();
		migrationIconButton = new JButton();
		saveActivatedIconButton = new JButton();
		saveIconButton = new JButton();
		createActivatedIconButton = new JButton();
		createIconButton = new JButton();
		uebertragenActivatedIconButton = new JButton();
		uebertragenIconButton = new JButton();
		historieActivatedIconButton = new JButton();
		historieIconButton = new JButton();
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
		welcomeArea.setForeground(GuiTheme.TEXT_SECONDARY);
		welcomeArea.setBorder(new EmptyBorder(GuiTheme.PADDING_MD, GuiTheme.PADDING_MD, GuiTheme.PADDING_MD,
				GuiTheme.PADDING_MD));
		welcomeArea.setText("Wählen Sie zunächst aus, wo die Daten aufzufinden sind, die Sie "
				+ "einliefern wollen.\n\n" + "Geben Sie dann den Pfad zum Zielordner "
				+ "an, in dem die SIPs gespeichert werden sollen.\n\n"
				+ "Optional können Sie ein zur Zwischenspeicherung benutztes " + "Arbeitsverzeichnis angeben.");

		rightsAreaOne = createLeftAlignedTextPane();
		rightsAreaOne.setText("In den folgenden Schritten können Sie die Publikations- und Migrationsrechte für "
				+ "Ihre SIPs festlegen.\n\n" + "Falls Sie diese Einstellungen schon einmal vorgenommen und gespeichert "
				+ "haben, können Sie sie jetzt laden:");

		licenseAreaOne = new JTextArea();
		licenseAreaOne.setEditable(false);
		licenseAreaOne.setOpaque(false);
		licenseAreaOne.setLineWrap(true);
		licenseAreaOne.setWrapStyleWord(true);
		licenseAreaOne.setFocusable(false);
		licenseAreaOne.setFont(standardFont.deriveFont(12.0f));
		licenseAreaOne.setForeground(GuiTheme.TEXT_SECONDARY);
		licenseAreaOne.setText("In den folgenden Schritten können Sie die Lizenzrechte für "
				+ "Ihre SIPs festlegen. Die Lizenzangabe gilt für die Veröffentlichungsportale.\n\n"
				+ "Für eine Publikation ist eine Lizenz zwingend erforderlich!\n\n"
				+ "Hinterlegen Sie im SIP-Builder eine Lizenz nur wenn diese nicht "
				+ "bereits in den Metadaten hinterlegt ist!!!");

		rightsAreaTwo = createLeftAlignedTextPane();
		rightsAreaTwo.setText("Alternativ können Sie die Standard-Einstellungen wählen:");

		rightsAreaThree = createLeftAlignedTextPane();
		rightsAreaThree.setText("Sie haben im Folgenden die Möglichkeit, die Einstellungen\n"
				+ "anzupassen bzw. komplett neu zu wählen. Wenn Sie dabei\n"
				+ "getroffene Entscheidungen rückgängig machen möchten,\n"
				+ "können Sie jederzeit hierher zurückkehren und die Standard-\n" + "Einstellungen neu laden.");

		institutionArea = new JTextArea();
		institutionArea.setEditable(false);
		institutionArea.setOpaque(false);
		institutionArea.setLineWrap(true);
		institutionArea.setWrapStyleWord(true);
		institutionArea.setFocusable(false);
		institutionArea.setFont(standardFont.deriveFont(12.0f));
		institutionArea.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionArea.setText(
				"Möchten Sie - unabhängig von den Einstellungen für die Öffentlichkeit - zusätzliche Präsentationsdaten "
						+ "generieren lassen, die nur Ihrer Institution über die Schnittstellen des Presentation Repository zugänglich sind?\n"
						+ "Es können dabei Rechteeinstellungen gewählt werden, die von denen der öffentlich einsehbaren Daten abweichen.");

		institutionRestrictionArea = new JTextArea();
		institutionRestrictionArea.setEditable(false);
		institutionRestrictionArea.setOpaque(false);
		institutionRestrictionArea.setLineWrap(true);
		institutionRestrictionArea.setWrapStyleWord(true);
		institutionRestrictionArea.setFocusable(false);
		institutionRestrictionArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionArea.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionArea
				.setText("Bitte wählen Sie die Vorschaurestriktionen für die verschiedenen " + "Medientypen.");

		institutionRestrictionTextArea = new JTextArea();
		institutionRestrictionTextArea.setEditable(false);
		institutionRestrictionTextArea.setOpaque(false);
		institutionRestrictionTextArea.setLineWrap(true);
		institutionRestrictionTextArea.setWrapStyleWord(true);
		institutionRestrictionTextArea.setFocusable(false);
		institutionRestrictionTextArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionTextArea.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionTextArea
				.setText("Geben Sie die gewünschten Seitenzahlen durch Kommas voneinander getrennt an. "
						+ "Um mehrere aufeinander folgende Seiten festzulegen, genügt es, die erste und "
						+ "letzte Seitenzahl mit einem dazwischen liegenden Bindestrich anzugeben.\n\n" + "Beispiel:\n"
						+ "Die Eingabe \"1,14-17,24,30\" drückt aus, dass die Seiten 1, 14, 15, 16, 17, 24 und 30 "
						+ "angezeigt werden sollen.");
		institutionRestrictionTextArea.setEnabled(false);

		institutionRestrictionImageArea = new JTextArea();
		institutionRestrictionImageArea.setEditable(false);
		institutionRestrictionImageArea.setOpaque(false);
		institutionRestrictionImageArea.setLineWrap(true);
		institutionRestrictionImageArea.setWrapStyleWord(true);
		institutionRestrictionImageArea.setFocusable(false);
		institutionRestrictionImageArea.setFont(standardFont.deriveFont(12.0f));
		institutionRestrictionImageArea.setForeground(GuiTheme.TEXT_SECONDARY);
		institutionRestrictionImageArea
				.setText("Geben Sie die Bildqualität absolut oder im Verhältnis " + "zum Original an.");
		institutionRestrictionImageArea.setEnabled(false);

		publicArea = new JTextArea();
		publicArea.setEditable(false);
		publicArea.setOpaque(false);
		publicArea.setLineWrap(true);
		publicArea.setWrapStyleWord(true);
		publicArea.setFocusable(false);
		publicArea.setFont(standardFont.deriveFont(12.0f));
		publicArea.setForeground(GuiTheme.TEXT_SECONDARY);
		publicArea.setText("Möchten Sie den Zugriff auf Ihre Daten über öffentliche Portale ermöglichen?");

		publicDDBArea = new JTextArea();
		publicDDBArea.setEditable(false);
		publicDDBArea.setOpaque(false);
		publicDDBArea.setLineWrap(true);
		publicDDBArea.setWrapStyleWord(true);
		publicDDBArea.setFocusable(false);
		publicDDBArea.setFont(standardFont.deriveFont(12.0f));
		publicDDBArea.setForeground(GuiTheme.TEXT_SECONDARY);
		publicDDBArea.setText("Im Regelfall werden veröffentlichte Daten automatisch der Deutschen Digitalen "
				+ "Bibliothek zur Verfügung gestellt. Wünschen Sie dies nicht, können Sie diesen Prozess durch Deaktivieren "
				+ "der Option verhindern.");

		publicRestrictionArea = new JTextArea();
		publicRestrictionArea.setEditable(false);
		publicRestrictionArea.setOpaque(false);
		publicRestrictionArea.setLineWrap(true);
		publicRestrictionArea.setWrapStyleWord(true);
		publicRestrictionArea.setFocusable(false);
		publicRestrictionArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionArea.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionArea
				.setText("Bitte wählen Sie die Vorschaurestriktionen für die verschiedenen " + "Medientypen.");

		publicRestrictionTextArea = new JTextArea();
		publicRestrictionTextArea.setEditable(false);
		publicRestrictionTextArea.setOpaque(false);
		publicRestrictionTextArea.setLineWrap(true);
		publicRestrictionTextArea.setWrapStyleWord(true);
		publicRestrictionTextArea.setFocusable(false);
		publicRestrictionTextArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionTextArea.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionTextArea
				.setText("Geben Sie die gewünschten Seitenzahlen durch Kommas voneinander getrennt an. "
						+ "Um mehrere aufeinander folgende Seiten festzulegen, genügt es, die erste und "
						+ "letzte Seitenzahl mit einem dazwischen liegenden Bindestrich anzugeben.\n\n" + "Beispiel:\n"
						+ "Die Eingabe \"1,14-17,24,30\" drückt aus, dass die Seiten 1, 14, 15, 16, 17, 24 und 30 "
						+ "angezeigt werden sollen.");
		publicRestrictionTextArea.setEnabled(false);

		publicRestrictionImageArea = new JTextArea();
		publicRestrictionImageArea.setEditable(false);
		publicRestrictionImageArea.setOpaque(false);
		publicRestrictionImageArea.setLineWrap(true);
		publicRestrictionImageArea.setWrapStyleWord(true);
		publicRestrictionImageArea.setFocusable(false);
		publicRestrictionImageArea.setFont(standardFont.deriveFont(12.0f));
		publicRestrictionImageArea.setForeground(GuiTheme.TEXT_SECONDARY);
		publicRestrictionImageArea
				.setText("Geben Sie die Bildqualität absolut oder im Verhältnis " + "zum Original an.");
		publicRestrictionImageArea.setEnabled(false);

		migrationArea = new JTextArea();
		migrationArea.setEditable(false);
		migrationArea.setOpaque(false);
		migrationArea.setLineWrap(true);
		migrationArea.setWrapStyleWord(true);
		migrationArea.setFocusable(false);
		migrationArea.setFont(standardFont.deriveFont(12.0f));
		migrationArea.setForeground(GuiTheme.TEXT_SECONDARY);
		migrationArea.setText(
				"Bei der Einlieferung Ihrer Daten findet im Regelfall eine Erstkonversion in Dateiformate statt, die "
						+ "für die Langzeitarchivierung geeignet sind. Darüber hinaus können spätere Konversionen folgen, um die "
						+ "dauerhafte Lesbarkeit der Daten zu gewährleisten (Migration).\n"
						+ "Wenn Sie im Falle einer Migration benachrichtigt oder um ausdrückliche Zustimmung gefragt werden "
						+ "möchten, können Sie die entsprechenden Einstellungen hier vornehmen.");

		settingsOverviewInfoArea = new JTextArea();
		settingsOverviewInfoArea.setEditable(false);
		settingsOverviewInfoArea.setOpaque(false);
		settingsOverviewInfoArea.setLineWrap(true);
		settingsOverviewInfoArea.setWrapStyleWord(true);
		settingsOverviewInfoArea.setFocusable(false);
		settingsOverviewInfoArea.setFont(standardFont.deriveFont(12.0f));
		settingsOverviewInfoArea.setForeground(GuiTheme.TEXT_SECONDARY);
		settingsOverviewInfoArea.setText("Sie haben folgende Rechteeinstellungen gewählt:");

		settingsOverviewTextArea = new JTextArea();
		settingsOverviewTextArea.setEditable(false);
		settingsOverviewTextArea.setOpaque(false);
		settingsOverviewTextArea.setLineWrap(true);
		settingsOverviewTextArea.setWrapStyleWord(true);
		settingsOverviewTextArea.setFocusable(false);
		settingsOverviewTextArea.setFont(standardFont.deriveFont(12.0f));
		settingsOverviewTextArea.setForeground(GuiTheme.TEXT_PRIMARY);
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
		saveArea.setForeground(GuiTheme.TEXT_SECONDARY);
		saveArea.setText("Falls Sie Ihre Rechteeinstellungen später für weitere SIP-Generierungen "
				+ "wiederverwenden möchten, können Sie diese nun sichern:");

		createArea = new JTextArea();
		createArea.setEditable(false);
		createArea.setOpaque(false);
		createArea.setLineWrap(true);
		createArea.setWrapStyleWord(true);
		createArea.setFocusable(false);
		createArea.setFont(standardFont.deriveFont(12.0f));
		createArea.setForeground(GuiTheme.TEXT_SECONDARY);
		createArea.setText("Wenn Sie mit den vorgenommenen Einstellungen zufrieden sind, können Sie "
				+ "die SIP-Generierung starten, indem Sie den Button \"Erstellen\" betätigen.\n\n"
				+ "Sie können den Prozess beschleunigen, indem Sie die Kompression deaktivieren. "
				+ "Bitte beachten Sie, dass in diesem Fall größere SIP-Dateien erzeugt werden.");
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
		ButtonGroup licenseRadioButtonGroup = new ButtonGroup();
		metadataLicenseRadioButton = new JRadioButton("Lizenz aus Metadaten übernehmen.");
		metadataLicenseRadioButton.setOpaque(false);
		metadataLicenseRadioButton.setFont(standardFont.deriveFont(12.0f));

		premisLicenseRadioButton = new JRadioButton("Lizenz festlegen");
		premisLicenseRadioButton.setOpaque(false);
		premisLicenseRadioButton.setFont(standardFont.deriveFont(12.0f));

		licenseRadioButtonGroup.add(metadataLicenseRadioButton);
		licenseRadioButtonGroup.add(premisLicenseRadioButton);

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
		sourcePathTextField.setBorder(GuiTheme.createInputBorder());
		sourcePathTextField.setPreferredSize(GuiTheme.inputFieldSize());

		destinationPathTextField = new JTextField();
		destinationPathTextField.setEditable(true);
		destinationPathTextField.setBorder(GuiTheme.createInputBorder());
		destinationPathTextField.setPreferredSize(GuiTheme.inputFieldSize());

		workingPathTextField = new JTextField();
		workingPathTextField.setEditable(true);
		workingPathTextField.setBorder(GuiTheme.createInputBorder());
		workingPathTextField.setPreferredSize(GuiTheme.inputFieldSize());

		licenseTextField = new JTextField();
		licenseTextField.setEditable(true);
		licenseTextField.setOpaque(false);
		licenseTextField.setBorder(null);
		licenseTextField.setFont(standardFont.deriveFont(12.0f));

		collectionNameTextField = new JTextField();
		collectionNameTextField.setEditable(false);
		collectionNameTextField.setEnabled(false);
		collectionNameTextField.setBorder(GuiTheme.createInputBorder());

		institutionTempStartDateChooserField = new JDateChooser();
		institutionTempStartDateChooserField.setLocale(Locale.GERMANY);
		institutionTempStartDateChooserField.setEnabled(false);
		institutionTempStartDateChooserField.setPreferredSize(new Dimension(220, GuiTheme.INPUT_HEIGHT));

		institutionRestrictionTextPagesTextField = new JTextField();
		institutionRestrictionTextPagesTextField.setEditable(false);
		institutionRestrictionTextPagesTextField.setBorder(GuiTheme.createInputBorder());
		institutionRestrictionTextPagesTextField.setEnabled(false);
		institutionRestrictionImageTextField = new JTextField();
		institutionRestrictionImageTextField.setBorder(GuiTheme.createInputBorder());
		institutionRestrictionImageTextField.setEnabled(false);
		institutionRestrictionImageTextField.setEditable(false);

		publicTempStartDateChooserField = new JDateChooser();
		publicTempStartDateChooserField.setLocale(Locale.GERMANY);
		publicTempStartDateChooserField.setPreferredSize(new Dimension(220, GuiTheme.INPUT_HEIGHT));

		publicRestrictionTextPagesTextField = new JTextField();
		publicRestrictionTextPagesTextField.setEditable(false);
		publicRestrictionTextPagesTextField.setBorder(GuiTheme.createInputBorder());
		publicRestrictionTextPagesTextField.setEnabled(false);
		publicRestrictionImageTextField = new JTextField();
		publicRestrictionImageTextField.setBorder(GuiTheme.createInputBorder());
		publicRestrictionImageTextField.setEnabled(false);
		publicRestrictionImageTextField.setEditable(false);
	}

	/**
	 * Creates the combo boxes (drop down menus)
	 */
	private void initializeComboBoxes() {

		kindOfSIPBuildingDropDown = new JComboBox<String>();
		kindOfSIPBuildingDropDown.setFont(standardFont.deriveFont(12.0f));
		kindOfSIPBuildingDropDown.addItem(C.KIND_OF_SIPBUILDING_SINGLE);
		kindOfSIPBuildingDropDown.addItem(C.KIND_OF_SIPBUILDING_MULTIPLE);
		kindOfSIPBuildingDropDown.addItem(C.KIND_OF_SIPBUILDING_NESTED);

		licenseDropDown = new JComboBox<CCLicense>();
		licenseDropDown.setFont(standardFont.deriveFont(12.0f));
		for (ContractRights.CCLicense tmp : ContractRights.CCLicense.values()) {
			licenseDropDown.addItem(tmp);
		}

		institutionLawIdDropDown = new JComboBox<String>();
		institutionLawIdDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionLawIdDropDown.setEnabled(false);
		institutionLawIdDropDown.addItem("ePflicht");
		institutionLawIdDropDown.addItem("UrhG DE");

		institutionImageDropDown = new JComboBox<String>();
		institutionImageDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionImageDropDown.addItem(
				"Niedrig (" + contractSettings.getWidthImage(0) + "x" + contractSettings.getHeightImage(0) + ")");
		institutionImageDropDown.addItem(
				"Mittel (" + contractSettings.getWidthImage(1) + "x" + contractSettings.getHeightImage(1) + ")");
		institutionImageDropDown
				.addItem("Hoch (" + contractSettings.getWidthImage(2) + "x" + contractSettings.getHeightImage(2) + ")");
		institutionImageDropDown.addItem(contractSettings.getPercentImage(0));
		institutionImageDropDown.addItem(contractSettings.getPercentImage(1));
		institutionImageDropDown.addItem(contractSettings.getPercentImage(2));
		institutionImageDropDown.setEnabled(false);

		institutionImageTextDropDown = new JComboBox<String>();
		institutionImageTextDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionImageTextDropDown.addItem("Fußzeile");
		institutionImageTextDropDown.addItem("Wasserzeichen (oben)");
		institutionImageTextDropDown.addItem("Wasserzeichen (mittig)");
		institutionImageTextDropDown.addItem("Wasserzeichen (unten)");
		institutionImageTextDropDown.setEnabled(false);

		institutionImageTextOpacityDropDown = new JComboBox<String>();
		institutionImageTextOpacityDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(0) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(1) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(2) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(3) + "%");
		institutionImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(4) + "%");
		institutionImageTextOpacityDropDown.setEnabled(false);

		institutionImageTextSizeDropDown = new JComboBox<String>();
		institutionImageTextSizeDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(0));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(1));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(2));
		institutionImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(3));
		institutionImageTextSizeDropDown.setEnabled(false);

		institutionAudioDurationDropDown = new JComboBox<String>();
		institutionAudioDurationDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(0));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(1));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(2));
		institutionAudioDurationDropDown.addItem(contractSettings.getDuration(3));
		institutionAudioDurationDropDown.setEnabled(false);

		institutionVideoQualityDropDown = new JComboBox<String>();
		institutionVideoQualityDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionVideoQualityDropDown.addItem("Niedrig");
		institutionVideoQualityDropDown.addItem("Mittel");
		institutionVideoQualityDropDown.addItem("Hoch");
		institutionVideoQualityDropDown.setEnabled(false);

		institutionVideoDurationDropDown = new JComboBox<String>();
		institutionVideoDurationDropDown.setFont(standardFont.deriveFont(12.0f));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(0));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(1));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(2));
		institutionVideoDurationDropDown.addItem(contractSettings.getDuration(3));
		institutionVideoDurationDropDown.setEnabled(false);

		publicLawIdDropDown = new JComboBox<String>();
		publicLawIdDropDown.setFont(standardFont.deriveFont(12.0f));
		publicLawIdDropDown.setEnabled(false);
		publicLawIdDropDown.addItem("ePflicht");
		publicLawIdDropDown.addItem("UrhG DE");

		publicImageDropDown = new JComboBox<String>();
		publicImageDropDown.setFont(standardFont.deriveFont(12.0f));
		publicImageDropDown.addItem(
				"Niedrig (" + contractSettings.getWidthImage(0) + "x" + contractSettings.getHeightImage(0) + ")");
		publicImageDropDown.addItem(
				"Mittel (" + contractSettings.getWidthImage(1) + "x" + contractSettings.getHeightImage(1) + ")");
		publicImageDropDown
				.addItem("Hoch (" + contractSettings.getWidthImage(2) + "x" + contractSettings.getHeightImage(2) + ")");
		publicImageDropDown.addItem(contractSettings.getPercentImage(0));
		publicImageDropDown.addItem(contractSettings.getPercentImage(1));
		publicImageDropDown.addItem(contractSettings.getPercentImage(2));
		publicImageDropDown.setEnabled(false);

		publicImageTextDropDown = new JComboBox<String>();
		publicImageTextDropDown.setFont(standardFont.deriveFont(12.0f));
		publicImageTextDropDown.addItem("Fußzeile");
		publicImageTextDropDown.addItem("Wasserzeichen (oben)");
		publicImageTextDropDown.addItem("Wasserzeichen (mittig)");
		publicImageTextDropDown.addItem("Wasserzeichen (unten)");
		publicImageTextDropDown.setEnabled(false);

		publicImageTextOpacityDropDown = new JComboBox<String>();
		publicImageTextOpacityDropDown.setFont(standardFont.deriveFont(12.0f));
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(0) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(1) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(2) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(3) + "%");
		publicImageTextOpacityDropDown.addItem(contractSettings.getOpacityImage(4) + "%");
		publicImageTextOpacityDropDown.setEnabled(false);

		publicImageTextSizeDropDown = new JComboBox<String>();
		publicImageTextSizeDropDown.setFont(standardFont.deriveFont(12.0f));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(0));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(1));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(2));
		publicImageTextSizeDropDown.addItem(contractSettings.getTextSizeImage(3));
		publicImageTextSizeDropDown.setEnabled(false);

		publicAudioDurationDropDown = new JComboBox<String>();
		publicAudioDurationDropDown.setFont(standardFont.deriveFont(12.0f));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(0));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(1));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(2));
		publicAudioDurationDropDown.addItem(contractSettings.getDuration(3));
		publicAudioDurationDropDown.setEnabled(false);

		publicVideoQualityDropDown = new JComboBox<String>();
		publicVideoQualityDropDown.setFont(standardFont.deriveFont(12.0f));
		publicVideoQualityDropDown.addItem("Niedrig");
		publicVideoQualityDropDown.addItem("Mittel");
		publicVideoQualityDropDown.addItem("Hoch");
		publicVideoQualityDropDown.setEnabled(false);

		publicVideoDurationDropDown = new JComboBox<String>();
		publicVideoDurationDropDown.setFont(standardFont.deriveFont(12.0f));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(0));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(1));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(2));
		publicVideoDurationDropDown.addItem(contractSettings.getDuration(3));
		publicVideoDurationDropDown.setEnabled(false);

		migrationDropDown = new JComboBox<String>();
		migrationDropDown.setFont(standardFont.deriveFont(12.0f));
		migrationDropDown.addItem("Keine");
		migrationDropDown.addItem("Über Migration informieren");
		migrationDropDown.addItem("Zustimmung für Migration einholen");

		styleComboBox(kindOfSIPBuildingDropDown);
		styleComboBox(licenseDropDown);
		styleComboBox(institutionLawIdDropDown);
		styleComboBox(institutionImageDropDown);
		styleComboBox(institutionImageTextDropDown);
		styleComboBox(institutionImageTextOpacityDropDown);
		styleComboBox(institutionImageTextSizeDropDown);
		styleComboBox(institutionAudioDurationDropDown);
		styleComboBox(institutionVideoQualityDropDown);
		styleComboBox(institutionVideoDurationDropDown);
		styleComboBox(publicLawIdDropDown);
		styleComboBox(publicImageDropDown);
		styleComboBox(publicImageTextDropDown);
		styleComboBox(publicImageTextOpacityDropDown);
		styleComboBox(publicImageTextSizeDropDown);
		styleComboBox(publicAudioDurationDropDown);
		styleComboBox(publicVideoQualityDropDown);
		styleComboBox(publicVideoDurationDropDown);
		styleComboBox(migrationDropDown);
	}

	private <E> void styleComboBox(final JComboBox<E> comboBox) {
		comboBox.setForeground(GuiTheme.TEXT_PRIMARY);
		comboBox.setBackground(GuiTheme.BG_INPUT);
		comboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (comboBox.isEnabled()) {
					c.setForeground(GuiTheme.TEXT_PRIMARY);
				} else {
					c.setForeground(GuiTheme.TEXT_SECONDARY);
				}
				return c;
			}
		});
	}

	/**
	 * Sets the initial position of every GUI element
	 */
	private void defineElementPositions() {

		int jhOffsetY = 40;

		// restriction tab content panels (absolute positioning inside the tabs)
		positionTabContent(publicTextPanel, publicTextRestrictionCheckBox, publicRestrictionTextPagesLabel,
				publicRestrictionTextPagesTextField, publicRestrictionTextArea);
		positionTabContent(publicImagePanel, publicImageRestrictionCheckBox, publicRestrictionImageArea,
				publicImageDropDown, publicImageTextCheckBox, publicRestrictionImageLabel,
				publicRestrictionImageTextField, publicRestrictionImageTextTypeLabel, publicImageTextDropDown,
				publicRestrictionImageTextOpacityLabel, publicImageTextOpacityDropDown,
				publicRestrictionImageTextSizeLabel, publicImageTextSizeDropDown);
		positionAudioTabContent(publicAudioPanel, publicAudioRestrictionCheckBox, publicRestrictionAudioDurationLabel,
				publicAudioDurationDropDown);
		positionVideoTabContent(publicVideoPanel, publicVideoRestrictionCheckBox,
				publicRestrictionVideoQualityLabel, publicVideoQualityDropDown, publicVideoDurationCheckBox,
				publicRestrictionVideoDurationLabel, publicVideoDurationDropDown);

		positionTabContent(institutionTextPanel, institutionTextRestrictionCheckBox,
				institutionRestrictionTextPagesLabel, institutionRestrictionTextPagesTextField,
				institutionRestrictionTextArea);
		positionTabContent(institutionImagePanel, institutionImageRestrictionCheckBox,
				institutionRestrictionImageArea, institutionImageDropDown, institutionImageTextCheckBox,
				institutionRestrictionImageLabel, institutionRestrictionImageTextField,
				institutionRestrictionImageTextTypeLabel, institutionImageTextDropDown,
				institutionRestrictionImageTextOpacityLabel, institutionImageTextOpacityDropDown,
				institutionRestrictionImageTextSizeLabel, institutionImageTextSizeDropDown);
		positionAudioTabContent(institutionAudioPanel, institutionAudioRestrictionCheckBox,
				institutionRestrictionAudioDurationLabel, institutionAudioDurationDropDown);
		positionVideoTabContent(institutionVideoPanel, institutionVideoRestrictionCheckBox,
				institutionRestrictionVideoQualityLabel, institutionVideoQualityDropDown,
				institutionVideoDurationCheckBox, institutionRestrictionVideoDurationLabel,
				institutionVideoDurationDropDown);

		// uebertragenPanel
		uebertragenPanel.defineElementsPosition(startPanelWidth, startPanelHeight, backgroundUebertragenImageLabel);

		// historiePanel
		historiePanel.defineElementsPosition(startPanelWidth, startPanelHeight, jhOffsetY,
				backgroundHistorieImageLabel);
	}

	private void positionTabContent(JPanel panel, JComponent... components) {
		panel.setLayout(new BorderLayout());
		JPanel content = new JPanel(new GridBagLayout());
		content.setOpaque(false);
		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		panel.add(scroll, BorderLayout.CENTER);
		int row = 0;
		for (Component c : components) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(6, 10, 6, 10);
			content.add(c, gbc);
			row++;
		}
	}

	private void positionAudioTabContent(JPanel panel, JComponent checkBox, JComponent durationLabel,
			JComponent durationDropDown) {
		positionTabContent(panel, checkBox, durationLabel, durationDropDown);
	}

	private void positionVideoTabContent(JPanel panel, JComponent restrictionCheckBox,
			JComponent qualityLabel, JComponent qualityDropDown, JComponent durationCheckBox,
			JComponent durationLabel, JComponent durationDropDown) {
		positionTabContent(panel, restrictionCheckBox, qualityLabel, qualityDropDown, durationCheckBox,
				durationLabel, durationDropDown);
	}

	/**
	 * Adds the GUI elements to the content pane and the panels they belong to
	 */
	private void addElementsToContentPane() {

		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(GuiTheme.BG_MAIN);

		// header bar with DA-NRW logo (left) and pixel logo (right)
		getContentPane().add(createHeaderBar(), BorderLayout.NORTH);

		// content area with wizard cards (created before sidebar so addSidebarStep can register cards)
		cardLayout = new CardLayout();
		contentCardPanel = new JPanel(cardLayout);
		contentCardPanel.setBackground(GuiTheme.BG_MAIN);
		getContentPane().add(contentCardPanel, BorderLayout.CENTER);

		// sidebar
		buildSidebar();
		JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
		sidebarScrollPane.setBorder(BorderFactory.createEmptyBorder());
		sidebarScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sidebarScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sidebarScrollPane.getViewport().setBackground(GuiTheme.BG_SIDEBAR);
		sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		getContentPane().add(sidebarScrollPane, BorderLayout.WEST);

		buildStartCard();
		buildLoadStandardCard();
		buildPublicCard();
		buildPublicLicenseCard();
		buildPublicTempCard();
		buildPublicRestrictionCard();
		buildInstitutionCard();
		buildInstitutionTempCard();
		buildInstitutionRestrictionCard();
		buildMigrationCard();
		buildSaveCard();
		buildCreateCard();
		buildUebertragenCard();
		buildHistorieCard();

		// version info bar
		JPanel versionBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
		versionBar.setBackground(GuiTheme.BG_SIDEBAR);
		versionBar.add(versionInfoLabel);
		getContentPane().add(versionBar, BorderLayout.SOUTH);

		setMinimumSize(new Dimension(GuiTheme.WINDOW_WIDTH, GuiTheme.WINDOW_HEIGHT));
	}

	private void buildContentCard(JPanel panel, String title) {
		panel.setBackground(GuiTheme.BG_CARD);
		panel.setLayout(new BorderLayout());

		if (title != null) {
			JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));
			header.setOpaque(false);
			JLabel titleLabel = new JLabel(title);
			titleLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_XL));
			titleLabel.setForeground(GuiTheme.TEXT_PRIMARY);
			header.add(titleLabel);
			panel.add(header, BorderLayout.NORTH);
		}
	}

	private void buildSidebar() {
		sidebarPanel = new JPanel();
		sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
		sidebarPanel.setBackground(GuiTheme.BG_SIDEBAR);
		sidebarPanel.setPreferredSize(new Dimension(GuiTheme.SIDEBAR_WIDTH, GuiTheme.WINDOW_HEIGHT));

		JLabel brandLabel = new JLabel("<html><div style=\"font-family:'DejaVu Sans'; font-size:12pt; font-weight:bold;\">"
				+ SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + "<br/>SIP-Builder</div></html>");
		brandLabel.setForeground(GuiTheme.TEXT_ON_PRIMARY);
		brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		brandLabel.setBorder(new EmptyBorder(GuiTheme.PADDING_MD, GuiTheme.PADDING_MD, GuiTheme.PADDING_MD,
				GuiTheme.PADDING_MD));
		sidebarPanel.add(brandLabel);

		addSidebarGroupHeader("SIP - Erstellung");

		addSidebarStep(1, 1, "Start", startPanel);
		addSidebarStep(2, 2, "Laden/Standard", loadStandardPanel);
		addSidebarStep(6, 3, "Öffentlichkeit", publicPanel);
		addSidebarSubStep(7, "Lizenzangaben", publicLicensePanel);
		addSidebarSubStep(8, "zeitlich", publicTempPanel);
		addSidebarSubStep(9, "Restriktionen", publicRestrictionPanel);
		addSidebarStep(3, 4, "Eigene Institution", institutionPanel);
		addSidebarSubStep(4, "zeitlich", institutionTempPanel);
		addSidebarSubStep(5, "Restriktionen", institutionRestrictionPanel);
		addSidebarStep(10, 5, "Migration", migrationPanel);
		addSidebarStep(11, 6, "Speichern", savePanel);
		addSidebarStep(12, 7, "SIP erstellen", createPanel);

		addSidebarGroupHeader("weitere Hilfsmittel");

		addSidebarStep(13, 8, "Übertragen", uebertragenPanel);
		addSidebarStep(14, 9, "Historie", historiePanel);

		sidebarPanel.add(Box.createVerticalGlue());
	}

	/**
	 * Fuegt eine Gruppenueberschrift in der Sidebar hinzu.
	 */
	private void addSidebarGroupHeader(String title) {
		JLabel header = new JLabel(title);
		header.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_LG));
		header.setForeground(GuiTheme.TEXT_ON_PRIMARY);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setBorder(new EmptyBorder(GuiTheme.PADDING_MD, GuiTheme.PADDING_MD, GuiTheme.PADDING_SM,
				GuiTheme.PADDING_MD));
		sidebarPanel.add(header);
	}

	private void addSidebarStep(int stepNumber, int displayNumber, String label, final JPanel card) {
		SidebarButton button = new SidebarButton(displayNumber, label, false);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterSectionForCard(card);
			}
		});
		sidebarPanel.add(button);
		sidebarButtons.add(button);
		sidebarButtonsByCard.put(card, button);

		cardNames.put(card, "card" + stepNumber);
		contentCardPanel.add(card, cardNames.get(card));
	}

	private void addSidebarSubStep(int subStepNumber, String label, final JPanel card) {
		SidebarButton button = new SidebarButton(0, label, true);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterSectionForCard(card);
			}
		});
		sidebarPanel.add(button);
		sidebarButtons.add(button);
		sidebarButtonsByCard.put(card, button);

		cardNames.put(card, "card" + subStepNumber);
		contentCardPanel.add(card, cardNames.get(card));
	}

	private void enterSectionForCard(JPanel card) {
		if (card == startPanel) {
			enterStartSection();
		} else if (card == loadStandardPanel) {
			enterLoadSection();
		} else if (card == institutionPanel) {
			enterPublicationInstitutionSection();
		} else if (card == institutionTempPanel) {
			enterPublicationInstitutionTempSection();
		} else if (card == institutionRestrictionPanel) {
			enterPublicationInstitutionRestrictionsSection();
		} else if (card == publicPanel) {
			enterPublicationPublicSection();
		} else if (card == publicLicensePanel) {
			enterPublicationPublicLicenseSection();
		} else if (card == publicTempPanel) {
			enterPublicationPublicTempSection();
		} else if (card == publicRestrictionPanel) {
			enterPublicationPublicRestrictionsSection();
		} else if (card == migrationPanel) {
			enterMigrationSection();
		} else if (card == savePanel) {
			enterSaveSection();
		} else if (card == createPanel) {
			enterCreateSection();
		} else if (card == uebertragenPanel) {
			enterUebertragenSection();
		} else if (card == historiePanel) {
			enterHistorieSection();
		}
	}

	private void showCard(JPanel card) {
		cardLayout.show(contentCardPanel, cardNames.get(card));
		for (SidebarButton button : sidebarButtons) {
			button.setActive(button == sidebarButtonsByCard.get(card));
		}
		contentCardPanel.revalidate();
		contentCardPanel.repaint();
	}

	private JPanel createContentPanel() {
		JPanel content = new JPanel(new GridBagLayout());
		content.setOpaque(false);
		return content;
	}

	private JComponent centerContent(JPanel content) {
		JPanel wrapper = new JPanel(new GridBagLayout());
		wrapper.setOpaque(false);
		GridBagConstraints wc = new GridBagConstraints();
		wc.anchor = GridBagConstraints.NORTH;
		wc.weightx = 1.0;
		wc.weighty = 1.0;
		wc.insets = new Insets(GuiTheme.PADDING_SM, GuiTheme.PADDING_SM, 0, GuiTheme.PADDING_SM);
		wrapper.add(content, wc);
		JScrollPane scrollPane = new JScrollPane(wrapper);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}

	private JScrollPane wrapInScrollViewport(JComponent component) {
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		return scrollPane;
	}

	private JPanel createHeaderBar() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(GuiTheme.BG_SIDEBAR);
		header.setBorder(BorderFactory.createEmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_MD,
				GuiTheme.PADDING_SM, GuiTheme.PADDING_MD));

		JLabel logoLabel = new JLabel();
		logoLabel.setOpaque(true);
		logoLabel.setBackground(GuiTheme.BG_CARD);
		logoLabel.setBorder(BorderFactory.createEmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_MD,
				GuiTheme.PADDING_SM, GuiTheme.PADDING_MD));
		URL logoUrl = classloader.getResource(SIPBuilder.getProperties().getProperty("LOGO_FILE"));
		if (logoUrl != null) {
			ImageIcon logoIcon = new ImageIcon(logoUrl);
			Image scaledLogo = logoIcon.getImage().getScaledInstance(-1, 38, Image.SCALE_SMOOTH);
			logoLabel.setIcon(new ImageIcon(scaledLogo));
		} else {
			logoLabel.setText(SIPBuilder.getProperties().getProperty("ARCHIVE_NAME"));
			logoLabel.setFont(GuiTheme.getBoldFont(GuiTheme.FONT_SIZE_LG));
			logoLabel.setForeground(GuiTheme.TEXT_PRIMARY);
		}
		header.add(logoLabel, BorderLayout.WEST);

		JLabel pixelLogoLabel = new JLabel();
		pixelLogoLabel.setOpaque(true);
		pixelLogoLabel.setBackground(GuiTheme.BG_CARD);
		pixelLogoLabel.setBorder(BorderFactory.createEmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_MD,
				GuiTheme.PADDING_SM, GuiTheme.PADDING_MD));
		URL pixelUrl = classloader.getResource("images/sipBuilderIcon.png");
		if (pixelUrl != null) {
			ImageIcon pixelIcon = new ImageIcon(pixelUrl);
			Image scaledPixel = pixelIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			pixelLogoLabel.setIcon(new ImageIcon(scaledPixel));
		}
		header.add(pixelLogoLabel, BorderLayout.EAST);

		return header;
	}

	private void addSectionHeader(JPanel card, JLabel titleLabel) {
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, GuiTheme.PADDING_SM, GuiTheme.PADDING_SM));
		header.setOpaque(false);
		header.add(titleLabel);
		card.add(header, BorderLayout.NORTH);
	}

	private void addFooter(JPanel card, JButton backButton, JButton forwardButton) {
		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(GuiTheme.BG_FOOTER);
		footer.setBorder(new EmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_LG, GuiTheme.PADDING_SM,
				GuiTheme.PADDING_LG));

		JPanel backPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		backPane.setOpaque(false);
		if (backButton != null) {
			backPane.add(backButton);
			footer.add(backPane, BorderLayout.WEST);
		}

		JPanel forwardPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		forwardPane.setOpaque(false);
		if (forwardButton != null) {
			forwardPane.add(forwardButton);
			footer.add(forwardPane, BorderLayout.EAST);
		}

		card.add(footer, BorderLayout.SOUTH);
	}

	private void addFormRow(JPanel content, JComponent label, JComponent field, int row) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = row;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(4, 8, 4, 8);
		content.add(label, c);

		GridBagConstraints f = new GridBagConstraints();
		f.gridx = 1;
		f.gridy = row;
		f.gridwidth = 2;
		f.anchor = GridBagConstraints.WEST;
		f.fill = GridBagConstraints.HORIZONTAL;
		f.weightx = 1.0;
		f.insets = new Insets(4, 8, 4, 8);
		content.add(field, f);
	}

	private void addFormRowWithButton(JPanel content, JComponent label, JComponent field, JComponent button, int row) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = row;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(4, 8, 4, 8);
		content.add(label, c);

		GridBagConstraints f = new GridBagConstraints();
		f.gridx = 1;
		f.gridy = row;
		f.anchor = GridBagConstraints.WEST;
		f.fill = GridBagConstraints.HORIZONTAL;
		f.weightx = 1.0;
		f.insets = new Insets(4, 8, 4, 8);
		content.add(field, f);

		GridBagConstraints b = new GridBagConstraints();
		b.gridx = 2;
		b.gridy = row;
		b.anchor = GridBagConstraints.WEST;
		b.fill = GridBagConstraints.NONE;
		b.weightx = 0;
		b.insets = new Insets(4, 4, 4, 8);
		content.add(button, b);
	}

	private void addFullRow(JPanel content, JComponent component, int row) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(4, 8, 4, 8);
		content.add(component, c);
	}

	private void prepareFormLabel(JComponent label) {
		if (label instanceof JLabel) {
			JLabel l = (JLabel) label;
			FontMetrics fm = l.getFontMetrics(l.getFont());
			int textWidth = fm.stringWidth(l.getText()) + 12;
			int width = Math.max(GuiTheme.FORM_LABEL_WIDTH, textWidth);
			l.setPreferredSize(new Dimension(width, GuiTheme.INPUT_HEIGHT));
			l.setMinimumSize(new Dimension(width, GuiTheme.INPUT_HEIGHT));
			l.setMaximumSize(new Dimension(width, GuiTheme.INPUT_HEIGHT));
		}
	}

	private JTextArea createLeftAlignedTextPane() {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setOpaque(false);
		area.setFocusable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(standardFont.deriveFont(12.0f));
		area.setForeground(GuiTheme.TEXT_SECONDARY);
		area.setBorder(new EmptyBorder(0, 0, 0, 0));
		return area;
	}

	private void buildStartCard() {
		buildContentCard(startPanel, null);
		addSectionHeader(startPanel, welcomeLabel);

		JPanel content = createContentPanel();
		int row = 0;
		welcomeArea.setPreferredSize(new Dimension(560, 130));
		addFullRow(content, wrapInScrollViewport(welcomeArea), row);
		row++;

		addFormRowWithButton(content, sourceLabel, sourcePathTextField, sourceChooserButton, row);
		row++;
		addFormRowWithButton(content, destinationLabel, destinationPathTextField, destinationChooserButton, row);
		row++;
		addFormRowWithButton(content, workingLabel, workingPathTextField, workingChooserButton, row);
		row++;
		addFormRow(content, new JLabel(""), kindOfSIPBuildingDropDown, row);

		startPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(startPanel, null, goToLoadStandardButton);
	}

	private void buildLoadStandardCard() {
		buildContentCard(loadStandardPanel, null);
		addSectionHeader(loadStandardPanel, rightsLabel);

		JPanel content = createContentPanel();
		int row = 0;
		rightsAreaOne.setPreferredSize(new Dimension(560, 90));
		addCenteredRow(content, wrapInScrollViewport(rightsAreaOne), row);
		row++;
		addCenteredRow(content, loadContractButton, row);
		row++;
		addCenteredRow(content, (JComponent) Box.createVerticalStrut(GuiTheme.PADDING_MD), row);
		row++;
		rightsAreaTwo.setPreferredSize(new Dimension(560, 40));
		addCenteredRow(content, wrapInScrollViewport(rightsAreaTwo), row);
		row++;
		addCenteredRow(content, standardContractButton, row);
		row++;
		rightsAreaThree.setPreferredSize(new Dimension(560, 90));
		addCenteredRow(content, wrapInScrollViewport(rightsAreaThree), row);

		loadStandardPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(loadStandardPanel, goBackToStartButton, goToPublicButton);
	}

	private void addCenteredRow(JPanel content, JComponent component, int row) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.insets = new Insets(4, 8, 4, 8);
		content.add(component, c);
	}

	private void buildPublicCard() {
		buildContentCard(publicPanel, null);
		addSectionHeader(publicPanel, publicLabel);

		JPanel content = createContentPanel();
		int row = 0;
		publicArea.setPreferredSize(new Dimension(560, 60));
		addFullRow(content, wrapInScrollViewport(publicArea), row);
		row++;
		addFullRow(content, publicAllowRadioButton, row);
		row++;
		addFullRow(content, publicDenyRadioButton, row);
		row++;
		publicDDBArea.setPreferredSize(new Dimension(560, 60));
		addFullRow(content, wrapInScrollViewport(publicDDBArea), row);
		row++;
		addFullRow(content, publicDDBCheckBox, row);

		publicPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(publicPanel, goBackToLoadStandardButton, goToLicenseButton);
	}

	private void buildPublicLicenseCard() {
		buildContentCard(publicLicensePanel, null);
		addSectionHeader(publicLicensePanel, licenseLabel);

		JPanel content = createContentPanel();
		int row = 0;
		licenseAreaOne.setPreferredSize(new Dimension(560, 150));
		addFullRow(content, wrapInScrollViewport(licenseAreaOne), row);
		row++;
		addFullRow(content, (JComponent) Box.createVerticalStrut(GuiTheme.PADDING_SM), row);
		row++;
		addFullRow(content, metadataLicenseRadioButton, row);
		row++;
		addFullRow(content, premisLicenseRadioButton, row);
		row++;
		addFormRow(content, new JLabel("Lizenz:"), licenseDropDown, row);
		row++;
		addFormRow(content, new JLabel("Lizenz-URL:"), licenseTextField, row);

		publicLicensePanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(publicLicensePanel, goBackToPublicButton, goToPublicTempButton);
	}

	private void buildPublicTempCard() {
		buildContentCard(publicTempPanel, null);
		addSectionHeader(publicTempPanel, publicStartLabel);

		JPanel content = createContentPanel();
		int row = 0;
		addFullRow(content, publicNoTempRestrictionRadioButton, row);
		row++;
		addFullRow(content, publicTempRadioButton, row);
		row++;
		addFormRow(content, publicTempStartDateLabel, publicTempStartDateChooserField, row);
		row++;
		addFullRow(content, publicLawRadioButton, row);
		row++;
		addFormRow(content, new JLabel("Sperrgesetz:"), publicLawIdDropDown, row);

		publicTempPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(publicTempPanel, goBackToLicenseOrInstitutionButton, goToPublicRestrictionButton);
	}

	private void buildPublicRestrictionCard() {
		buildContentCard(publicRestrictionPanel, null);
		addSectionHeader(publicRestrictionPanel, publicRestrictionHeadlineLabel);

		JPanel content = createContentPanel();
		int row = 0;
		publicRestrictionArea.setPreferredSize(new Dimension(560, 50));
		addFullRow(content, wrapInScrollViewport(publicRestrictionArea), row);
		row++;
		GridBagConstraints t = new GridBagConstraints();
		t.gridx = 0;
		t.gridy = row;
		t.gridwidth = 3;
		t.fill = GridBagConstraints.BOTH;
		t.weightx = 1.0;
		t.weighty = 1.0;
		t.insets = new Insets(4, 8, 4, 8);
		content.add(publicTabbedPane, t);

		publicRestrictionPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(publicRestrictionPanel, goBackToPublicTempButton, goToInstitutionButton);
	}

	private void buildInstitutionCard() {
		buildContentCard(institutionPanel, null);
		addSectionHeader(institutionPanel, institutionLabel);

		JPanel content = createContentPanel();
		int row = 0;
		institutionArea.setPreferredSize(new Dimension(560, 80));
		addFullRow(content, wrapInScrollViewport(institutionArea), row);
		row++;
		addFullRow(content, institutionAllowRadioButton, row);
		row++;
		addFullRow(content, institutionDenyRadioButton, row);

		institutionPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(institutionPanel, goBackToPublicRestrictionOrPublicButton, goToInstitutionTempButton);
	}

	private void buildInstitutionTempCard() {
		buildContentCard(institutionTempPanel, null);
		addSectionHeader(institutionTempPanel, institutionStartLabel);

		JPanel content = createContentPanel();
		int row = 0;
		addFullRow(content, institutionNoTempRestrictionRadioButton, row);
		row++;
		addFullRow(content, institutionTempRadioButton, row);
		row++;
		addFormRow(content, institutionTempStartDateLabel, institutionTempStartDateChooserField, row);
		row++;
		addFullRow(content, institutionLawRadioButton, row);
		row++;
		addFormRow(content, new JLabel("Sperrgesetz:"), institutionLawIdDropDown, row);

		institutionTempPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(institutionTempPanel, goBackToInstitutionButton, goToInstitutionRestrictionOrPublicButton);
	}

	private void buildInstitutionRestrictionCard() {
		buildContentCard(institutionRestrictionPanel, null);
		addSectionHeader(institutionRestrictionPanel, institutionRestrictionHeadlineLabel);

		JPanel content = createContentPanel();
		int row = 0;
		institutionRestrictionArea.setPreferredSize(new Dimension(560, 50));
		addFullRow(content, wrapInScrollViewport(institutionRestrictionArea), row);
		row++;
		GridBagConstraints t = new GridBagConstraints();
		t.gridx = 0;
		t.gridy = row;
		t.gridwidth = 3;
		t.fill = GridBagConstraints.BOTH;
		t.weightx = 1.0;
		t.weighty = 1.0;
		t.insets = new Insets(4, 8, 4, 8);
		content.add(institutionTabbedPane, t);

		institutionRestrictionPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(institutionRestrictionPanel, goBackToInstitutionTempButton, goToMigrationButton);
	}

	private void buildMigrationCard() {
		buildContentCard(migrationPanel, null);
		addSectionHeader(migrationPanel, migrationConversionLabel);

		JPanel content = createContentPanel();
		int row = 0;
		migrationArea.setPreferredSize(new Dimension(560, 120));
		addFullRow(content, wrapInScrollViewport(migrationArea), row);
		row++;
		addFormRow(content, migrationConditionLabel, migrationDropDown, row);

		migrationPanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(migrationPanel, goBackToInstitutionRestrictionOrTempButton, goToSaveButton);
	}

	private void buildSaveCard() {
		buildContentCard(savePanel, null);
		addSectionHeader(savePanel, saveLabel);

		JPanel content = createContentPanel();
		int row = 0;
		settingsOverviewInfoArea.setPreferredSize(new Dimension(560, 30));
		addFullRow(content, wrapInScrollViewport(settingsOverviewInfoArea), row);
		row++;
		GridBagConstraints o = new GridBagConstraints();
		o.gridx = 0;
		o.gridy = row;
		o.gridwidth = 3;
		o.fill = GridBagConstraints.BOTH;
		o.weightx = 1.0;
		o.weighty = 1.0;
		o.insets = new Insets(4, 8, 4, 8);
		content.add(settingsOverviewArea, o);
		row++;
		saveArea.setPreferredSize(new Dimension(560, 40));
		addFullRow(content, wrapInScrollViewport(saveArea), row);
		row++;
		GridBagConstraints s = new GridBagConstraints();
		s.gridx = 1;
		s.gridy = row;
		s.anchor = GridBagConstraints.CENTER;
		s.insets = new Insets(4, 8, 4, 8);
		content.add(saveButton, s);

		savePanel.add(centerContent(content), BorderLayout.CENTER);
		addFooter(savePanel, goBackToMigrationButton, goToCreateButton);
	}

	private void buildCreateCard() {
		buildContentCard(createPanel, null);
		addSectionHeader(createPanel, createLabel);

		JPanel content = createContentPanel();
		int row = 0;
		createArea.setPreferredSize(new Dimension(560, 90));
		addFullRow(content, wrapInScrollViewport(createArea), row);
		row++;
		addFullRow(content, compressionCheckBox, row);
		row++;
		progressBar.setPreferredSize(new Dimension(500, 24));
		addFullRow(content, progressBar, row);
		row++;
		addFullRow(content, sipProgressDisplayLabel, row);
		row++;
		addFullRow(content, sipProgressStepLabel, row);

		createPanel.add(centerContent(content), BorderLayout.CENTER);

		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(GuiTheme.BG_FOOTER);
		footer.setBorder(new EmptyBorder(GuiTheme.PADDING_SM, GuiTheme.PADDING_LG, GuiTheme.PADDING_SM,
				GuiTheme.PADDING_LG));

		JPanel backPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		backPane.setOpaque(false);
		backPane.add(goBackToSaveButton);
		footer.add(backPane, BorderLayout.WEST);

		JPanel forwardPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		forwardPane.setOpaque(false);
		forwardPane.add(createButton);
		forwardPane.add(abortButton);
		forwardPane.add(quitButton);
		footer.add(forwardPane, BorderLayout.EAST);

		createPanel.add(footer, BorderLayout.SOUTH);
	}

	private void buildUebertragenCard() {
		uebertragenPanel.setBackground(GuiTheme.BG_CARD);
		contentCardPanel.add(uebertragenPanel, "card13");
		cardNames.put(uebertragenPanel, "card13");
	}

	private void buildHistorieCard() {
		historiePanel.setBackground(GuiTheme.BG_CARD);
		historiePanel.setLayout(new BorderLayout());

		JPanel content = createContentPanel();
		JLabel infoLabel = new JLabel("Hier wird in Zukunft die Historie der eingelieferten SIPs angezeigt.");
		infoLabel.setFont(GuiTheme.getFont(GuiTheme.FONT_SIZE_MD));
		infoLabel.setForeground(GuiTheme.TEXT_SECONDARY);
		addFullRow(content, infoLabel, 0);
		historiePanel.add(centerContent(content), BorderLayout.CENTER);

		if (!sidebarButtonsByCard.containsKey(historiePanel)) {
			contentCardPanel.add(historiePanel, "card14");
			cardNames.put(historiePanel, "card14");
		}
	}

	/**
	 * Creates action listeners for certain GUI elements to let them react with
	 * user input
	 */
	private void createListeners() {

		helpIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// DANRW-1524: Infotext korrigiert
				messageWriter.showMessage(
						SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder v" + buildNumber + "\n\n"
								+ "LVR-InfoKom (ab 2014). HKI, Universität zu Köln 2011-2014.\n\n" + "www.danrw.de");
			}

		});

		sourceChooserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String sourcePath = searchFolder(sourcePathChooser);
				if (sourcePath != null) {
					sourcePathTextField.setText(sourcePath);
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

		destinationChooserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String destinationPath = searchFolder(destinationPathChooser);
				if (destinationPath != null) {
					destinationPathTextField.setText(destinationPath);
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "destPath.sav"),
								destinationPath);
					} catch (Exception ex) {
						logger.warn(
								"Failed to create file "
										+ new File(dataFolderPath + File.separator + "destPath.sav").getAbsolutePath(),
								ex);
					}
				}
			}

		});

		workingChooserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String workingPath = searchFolder(workingPathChooser);
				if (workingPath != null) {
					workingPathTextField.setText(workingPath);
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "workPath.sav"), workingPath);
					} catch (Exception ex) {
						logger.warn(
								"Failed to create file "
										+ new File(dataFolderPath + File.separator + "workPath.sav").getAbsolutePath(),
								ex);
					}
				}
			}

		});

		goToLoadStandardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterLoadSection();
			}
		});

		goBackToStartButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterStartSection();
			}
		});

		loadContractButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				File contractRightsFile;
				String contractRightsFilePath = searchXmlFile();
				if (contractRightsFilePath != null) {
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "crloadPath.sav"),
								contractRightsFilePath);
					} catch (Exception ex) {
						logger.warn("Failed to create file "
								+ new File(dataFolderPath + File.separator + "crloadPath.sav").getAbsolutePath(), ex);
					}

					contractRightsFile = new File(contractRightsFilePath);

					try {
						sipFactory.getContractRights().loadContractRightsFromFile(contractRightsFile);
					} catch (Exception ex) {
						logger.error("Failed to load contract rights from file " + contractRightsFile.getAbsolutePath(),
								ex);
						messageWriter.showMessage("Beim Einlesen der Datei ist ein Fehler aufgetreten.",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					updateValues();
					messageWriter.showMessage("Die Rechteeinstellungen wurden erfolgreich geladen.",
							JOptionPane.PLAIN_MESSAGE);
				}
			}

		});

		standardContractButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (loadStandardRights())
					messageWriter.showMessage("Die Standardeinstellungen wurden erfolgreich geladen.",
							JOptionPane.PLAIN_MESSAGE);
			}
		});

		goToInstitutionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionSection();
			}
		});

		goToLicenseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationPublicLicenseSection())
					enterPublicationInstitutionSection();
			}
		});

		goBackToLicenseOrInstitutionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicLicenseSection();
			}
		});

		goBackToLoadStandardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterLoadSection();
			}
		});

		goToPublicButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		goBackToPublicButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		goToPublicTempButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicTempSection();
			}
		});

		goBackToPublicTempButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicTempSection();
			}
		});

		goToPublicRestrictionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicRestrictionsSection();
			}
		});

		goBackToPublicRestrictionOrPublicButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationPublicRestrictionsSection())
					enterPublicationPublicSection();
			}
		});

		goToInstitutionTempButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationInstitutionTempSection())
					enterMigrationSection();
			}
		});

		goBackToInstitutionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionSection();
			}
		});

		goToInstitutionRestrictionOrPublicButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionRestrictionsSection();
			}
		});

		goBackToInstitutionTempButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionTempSection();
			}
		});

		goBackToInstitutionRestrictionOrTempButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationInstitutionRestrictionsSection())
					enterPublicationInstitutionSection();
			}
		});

		goToMigrationButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterMigrationSection();
			}
		});

		goToSaveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterSaveSection();
			}
		});

		goBackToMigrationButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterMigrationSection();
			}
		});

		saveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String fileName = saveFile("contractRights.xml");

				if (fileName != null && !fileName.equals("")) {
					try {
						Utilities.writeFile(new File(dataFolderPath + File.separator + "crsavePath.sav"), fileName);
					} catch (Exception ex) {
						logger.warn("Failed to write file "
								+ new File(dataFolderPath + File.separator + "crsavePath.sav").getAbsolutePath(), ex);
					}

					try {
						premisWriter.createContractRightsFile(sipFactory.getContractRights(), new File(fileName));
						messageWriter.showMessage("Ihre Rechte-Einstellungen wurden erfolgreich gespeichert!",
								JOptionPane.PLAIN_MESSAGE);
					} catch (Exception ex) {
						logger.error("Failed to create file " + new File(fileName).getAbsolutePath(), ex);
						messageWriter.showMessage(ex.getMessage() + "\n" + ex.getStackTrace(),
								JOptionPane.ERROR_MESSAGE);
						messageWriter.showMessage("Beim Erstellen der Datei ist ein Fehler aufgetreten.",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		});

		goToCreateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterCreateSection();
			}
		});

		goBackToSaveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterSaveSection();
			}
		});

		createButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
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
				licenseActivatedIconButton.setEnabled(false);
				licenseIconButton.setEnabled(false);
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

				uebertragenActivatedIconButton.setEnabled(false);
				uebertragenIconButton.setEnabled(false);
				historieActivatedIconButton.setEnabled(false);
				historieIconButton.setEnabled(false);

				progressBar.setVisible(true);
				sipProgressDisplayLabel.setVisible(true);
				sipProgressStepLabel.setVisible(true);

				GuiProgressManager progressManager = new GuiProgressManager(progressBar, sipProgressDisplayLabel,
						sipProgressStepLabel);

				sipFactory.setCompress(compressionCheckBox.isSelected());
				sipFactory.setProgressManager(progressManager);
				sipFactory.setMessageWriter(messageWriter);
				sipFactory.startSIPBuilding();
			}
		});

		abortButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				abortButton.setEnabled(false);
				sipFactory.abort();
			}
		});

		quitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		startIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterStartSection();
			}
		});

		loadIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterLoadSection();
			}
		});

		licenseIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationPublicLicenseSection())
					enterPublicationPublicSection();
			}
		});

		publicationIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		publicationActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		institutionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionSection();
			}
		});

		institutionActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationInstitutionSection();
			}
		});

		institutionTempIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationInstitutionTempSection())
					enterPublicationInstitutionSection();
			}
		});

		institutionRestrictionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationInstitutionRestrictionsSection())
					enterPublicationInstitutionSection();
			}
		});

		publicIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		publicActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterPublicationPublicSection();
			}
		});

		publicTempIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationPublicTempSection())
					enterPublicationPublicSection();
			}
		});

		publicRestrictionIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!enterPublicationPublicRestrictionsSection())
					enterPublicationPublicSection();
			}
		});

		migrationIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterMigrationSection();
			}
		});

		saveIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterSaveSection();
			}
		});

		createIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterCreateSection();
			}
		});

		uebertragenIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterUebertragenSection();
			}
		});

		uebertragenActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterUebertragenSection();
			}
		});

		historieIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterHistorieSection();
			}
		});

		historieActivatedIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				enterHistorieSection();
			}
		});

		publicAllowRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				publicDDBArea.setEnabled(true);
				publicDDBCheckBox.setEnabled(true);
			}
		});

		publicDenyRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				publicDDBArea.setEnabled(false);
				publicDDBCheckBox.setEnabled(false);
			}
		});

		institutionNoTempRestrictionRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionNoTempRestrictionRadioButton.isSelected()) {
					institutionTempStartDateLabel.setEnabled(false);
					institutionTempStartDateChooserField.setEnabled(false);

					institutionLawIdDropDown.setEnabled(false);
				}
			}
		});

		institutionTempRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionTempRadioButton.isSelected()) {
					institutionTempStartDateLabel.setEnabled(true);

					institutionTempStartDateChooserField.setEnabled(true);
					institutionLawIdDropDown.setEnabled(false);
				} else {
					institutionTempStartDateLabel.setEnabled(false);
					institutionTempStartDateChooserField.setEnabled(false);
				}
			}
		});

		institutionLawRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionLawRadioButton.isSelected()) {
					institutionLawIdDropDown.setEnabled(true);
					institutionTempStartDateLabel.setEnabled(false);

					institutionTempStartDateChooserField.setEnabled(false);
				} else
					institutionLawIdDropDown.setEnabled(false);
			}
		});

		institutionTextRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionTextRestrictionCheckBox.isSelected()) {
					institutionRestrictionTextPagesTextField.setEnabled(true);
					institutionRestrictionTextPagesTextField.setEditable(true);
					institutionRestrictionTextPagesLabel.setEnabled(true);
					institutionRestrictionTextArea.setEnabled(true);
				} else {
					institutionRestrictionTextPagesTextField.setEnabled(false);
					institutionRestrictionTextPagesTextField.setEditable(false);
					institutionRestrictionTextPagesLabel.setEnabled(false);
					institutionRestrictionTextArea.setEnabled(false);
				}
			}
		});
		ActionListener licenseRadioButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JRadioButton btn = (JRadioButton) e.getSource();
				if (btn == premisLicenseRadioButton) {
					licenseDropDown.setEditable(true);
					licenseDropDown.setEnabled(true);
					licenseTextField.setVisible(true);
					licenseDropDown.setSelectedIndex(0);
				} else {
					licenseDropDown.setEditable(false);
					licenseDropDown.setEnabled(false);
					licenseTextField.setVisible(false);
				}
			}
		};
		metadataLicenseRadioButton.addActionListener(licenseRadioButtonListener);
		premisLicenseRadioButton.addActionListener(licenseRadioButtonListener);

		institutionImageRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionImageRestrictionCheckBox.isSelected()) {
					institutionImageDropDown.setEnabled(true);
					institutionRestrictionImageArea.setEnabled(true);
				} else {
					institutionImageDropDown.setEnabled(false);
					institutionRestrictionImageArea.setEnabled(false);
				}
			}
		});

		institutionImageTextCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionImageTextCheckBox.isSelected()) {
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
				} else {
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

		institutionAudioRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionAudioRestrictionCheckBox.isSelected()) {
					institutionAudioDurationDropDown.setEnabled(true);
					institutionRestrictionAudioDurationLabel.setEnabled(true);
				} else {
					institutionAudioDurationDropDown.setEnabled(false);
					institutionRestrictionAudioDurationLabel.setEnabled(false);
				}
			}
		});

		institutionVideoRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionVideoRestrictionCheckBox.isSelected()) {
					institutionVideoQualityDropDown.setEnabled(true);
					institutionRestrictionVideoQualityLabel.setEnabled(true);

				} else {
					institutionVideoQualityDropDown.setEnabled(false);
					institutionRestrictionVideoQualityLabel.setEnabled(false);
				}
			}
		});

		institutionVideoDurationCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (institutionVideoDurationCheckBox.isSelected()) {
					institutionVideoDurationDropDown.setEnabled(true);
					institutionRestrictionVideoDurationLabel.setEnabled(true);

				} else {
					institutionVideoDurationDropDown.setEnabled(false);
					institutionRestrictionVideoDurationLabel.setEnabled(false);
				}
			}
		});

		publicNoTempRestrictionRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicNoTempRestrictionRadioButton.isSelected()) {
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateChooserField.setEnabled(false);
					publicLawIdDropDown.setEnabled(false);
				}
			}
		});

		publicTempRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicTempRadioButton.isSelected()) {
					publicTempStartDateLabel.setEnabled(true);
					publicTempStartDateChooserField.setEnabled(true);
					publicLawIdDropDown.setEnabled(false);
				} else {
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateChooserField.setEnabled(false);
				}
			}
		});

		publicLawRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicLawRadioButton.isSelected()) {
					publicLawIdDropDown.setEnabled(true);
					publicTempStartDateLabel.setEnabled(false);
					publicTempStartDateChooserField.setEnabled(false);
				} else
					publicLawIdDropDown.setEnabled(false);
			}
		});

		publicTextRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicTextRestrictionCheckBox.isSelected()) {
					publicRestrictionTextPagesTextField.setEnabled(true);
					publicRestrictionTextPagesTextField.setEditable(true);
					publicRestrictionTextPagesLabel.setEnabled(true);
					publicRestrictionTextArea.setEnabled(true);
				} else {
					publicRestrictionTextPagesTextField.setEnabled(false);
					publicRestrictionTextPagesTextField.setEditable(false);
					publicRestrictionTextPagesLabel.setEnabled(false);
					publicRestrictionTextArea.setEnabled(false);
				}
			}
		});

		publicImageRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicImageRestrictionCheckBox.isSelected()) {
					publicImageDropDown.setEnabled(true);
					publicRestrictionImageArea.setEnabled(true);
				} else {
					publicImageDropDown.setEnabled(false);
					publicRestrictionImageArea.setEnabled(false);
				}
			}
		});

		publicImageTextCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicImageTextCheckBox.isSelected()) {
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
				} else {
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

		publicAudioRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicAudioRestrictionCheckBox.isSelected()) {
					publicAudioDurationDropDown.setEnabled(true);
					publicRestrictionAudioDurationLabel.setEnabled(true);
				} else {
					publicAudioDurationDropDown.setEnabled(false);
					publicRestrictionAudioDurationLabel.setEnabled(false);
				}
			}
		});

		publicVideoRestrictionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicVideoRestrictionCheckBox.isSelected()) {
					publicVideoQualityDropDown.setEnabled(true);
					publicRestrictionVideoQualityLabel.setEnabled(true);
				} else {
					publicVideoQualityDropDown.setEnabled(false);
					publicRestrictionVideoQualityLabel.setEnabled(false);
				}
			}
		});

		publicVideoDurationCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (publicVideoDurationCheckBox.isSelected()) {
					publicVideoDurationDropDown.setEnabled(true);
					publicRestrictionVideoDurationLabel.setEnabled(true);
				} else {
					publicVideoDurationDropDown.setEnabled(false);
					publicRestrictionVideoDurationLabel.setEnabled(false);
				}
			}
		});

		compressionCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				quitButton.setEnabled(false);
				quitButton.setVisible(false);
				createButton.setEnabled(true);
				createButton.setVisible(true);

				String compressionSetting = String.valueOf(compressionCheckBox.isSelected());
				try {
					Utilities.writeFile(new File(dataFolderPath + File.separator + "compSetting.sav"),
							compressionSetting);
				} catch (Exception ex) {
					logger.warn(
							"Failed to create file "
									+ new File(dataFolderPath + File.separator + "compSetting.sav").getAbsolutePath(),
							ex);
				}
			}
		});

		kindOfSIPBuildingDropDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (Utilities.translateKindOfSIPBuilding((String) kindOfSIPBuildingDropDown
						.getSelectedItem()) == SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS) {
					;
				} else {
					;
				}
			}

		});

		licenseDropDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				licenseTextField.setText(((ContractRights.CCLicense) licenseDropDown.getSelectedItem()).getHref());
			}

		});

		licenseTextField.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(licenseTextField.getText()));
						} catch (Throwable t) {
							System.out.println("Default System Browser not startab: " + t.getMessage());
						}
					}
				} else if (e.getClickCount() == 1) {
					licenseTextField.selectAll();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		institutionImageTextDropDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String text = institutionRestrictionImageTextField.getText();
				if (((String) institutionImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
					institutionRestrictionImageTextField.setDocument(new SetMaxText(65));
					institutionRestrictionImageTextField.setText(text);
					if (institutionImageTextCheckBox.isSelected()) {
						institutionRestrictionImageLabel
								.setText("Bitte geben Sie den gewünschten Text an (max. 65 Zeichen)");
					}

					institutionRestrictionImageTextOpacityLabel.setEnabled(false);
					institutionImageTextOpacityDropDown.setEnabled(false);
					institutionRestrictionImageTextSizeLabel.setEnabled(false);
					institutionImageTextSizeDropDown.setEnabled(false);
				} else {
					institutionRestrictionImageTextField.setDocument(new SetMaxText(20));
					if (text.length() > 20) {
						text = text.substring(0, 20);
						messageWriter
								.showMessage("Der Text enthält mehr als 20 Zeichen und wird entsprechend gekürzt.");
					}
					institutionRestrictionImageTextField.setText(text);
					if (institutionImageTextCheckBox.isSelected()) {
						institutionRestrictionImageLabel
								.setText("Bitte geben Sie den gewünschten Text an: (max. 20 Zeichen)");
					}

					institutionRestrictionImageTextOpacityLabel.setEnabled(true);
					institutionImageTextOpacityDropDown.setEnabled(true);
					institutionRestrictionImageTextSizeLabel.setEnabled(true);
					institutionImageTextSizeDropDown.setEnabled(true);
				}
			}

		});

		publicImageTextDropDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String text = publicRestrictionImageTextField.getText();
				if (((String) publicImageTextDropDown.getSelectedItem()).equals("Fußzeile")) {
					publicRestrictionImageTextField.setDocument(new SetMaxText(65));
					publicRestrictionImageTextField.setText(text);
					if (publicImageTextCheckBox.isSelected()) {
						publicRestrictionImageLabel
								.setText("Bitte geben Sie den gewünschten Text an (max. 65 Zeichen)");
					}
					publicRestrictionImageTextOpacityLabel.setEnabled(false);
					publicImageTextOpacityDropDown.setEnabled(false);
					publicRestrictionImageTextSizeLabel.setEnabled(false);
					publicImageTextSizeDropDown.setEnabled(false);
				} else {
					publicRestrictionImageTextField.setDocument(new SetMaxText(20));
					if (text.length() > 20) {
						text = text.substring(0, 20);
						messageWriter
								.showMessage("Der Text enthält mehr als 20 Zeichen und wird entsprechend gekürzt.");
					}
					publicRestrictionImageTextField.setText(text);
					if (publicImageTextCheckBox.isSelected()) {
						publicRestrictionImageLabel
								.setText("Bitte geben Sie den gewünschten Text an: (max. 20 Zeichen)");
					}
					publicRestrictionImageTextOpacityLabel.setEnabled(true);
					publicImageTextOpacityDropDown.setEnabled(true);
					publicRestrictionImageTextSizeLabel.setEnabled(true);
					publicImageTextSizeDropDown.setEnabled(true);
				}
			}

		});

		progressBar.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				if (progressBar.getValue() == 100 || !progressBar.isEnabled()) {

					compressionCheckBox.setEnabled(true);
					abortButton.setEnabled(false);
					abortButton.setVisible(false);

					if (progressBar.getValue() == 100) {
						quitButton.setEnabled(true);
						quitButton.setVisible(true);
					} else if (!progressBar.isEnabled()) {
						createButton.setEnabled(true);
						createButton.setVisible(true);
					}
					goBackToSaveButton.setEnabled(true);
					startActivatedIconButton.setEnabled(true);
					startIconButton.setEnabled(true);
					loadActivatedIconButton.setEnabled(true);
					loadIconButton.setEnabled(true);
					licenseActivatedIconButton.setEnabled(true);
					licenseIconButton.setEnabled(true);
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

					uebertragenActivatedIconButton.setEnabled(true);
					uebertragenIconButton.setEnabled(true);
					historieActivatedIconButton.setEnabled(true);
					historieIconButton.setEnabled(true);
				}
			}

		});

		publicTabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (oldTabPublic != publicTabbedPane.getSelectedIndex()) {
					/*
					 * Reiter Text
					 */
					if (oldTabPublic == 0) {

						if (publicTextRestrictionCheckBox.isSelected()) {
							if (publicRestrictionTextPagesTextField == null
									|| publicRestrictionTextPagesTextField.getText().equals("")) {
								publicTabbedPane.setSelectedIndex(oldTabPublic);
								messageWriter.showMessage(
										"Bitte geben Sie die Seitennummern der zu\n" + "berücksichtigenden Seiten an.",
										JOptionPane.ERROR_MESSAGE);

								oldTabPublic = publicTabbedPane.getSelectedIndex();
								return;
							}

							sipFactory.getContractRights().getPublicRights()
									.setPages(publicRestrictionTextPagesTextField.getText());
							if (sipFactory.getContractRights().getPublicRights().parsePages().equals("")) {
								publicTabbedPane.setSelectedIndex(oldTabPublic);
								messageWriter.showMessage(
										"Die Angabe der Seitenzahlen ist nicht \n" + "korrekt formuliert.",
										JOptionPane.ERROR_MESSAGE);

								oldTabPublic = publicTabbedPane.getSelectedIndex();
								return;
							}
						}
					} // Ende Reiter Text
					/*
					 * Reiter Bild
					 */
					if (oldTabPublic == 1) {
						if (publicImageTextCheckBox.isSelected()) {
							if (publicRestrictionImageTextField.getText() == null
									|| publicRestrictionImageTextField.getText().equals("")) {
								if (Utilities.translateTextType((String) publicImageTextDropDown
										.getSelectedItem()) == PublicationRights.TextType.footer) {
									publicTabbedPane.setSelectedIndex(oldTabPublic);

									messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.",
											JOptionPane.ERROR_MESSAGE);

									oldTabPublic = publicTabbedPane.getSelectedIndex();
									return;
								} else {
									publicTabbedPane.setSelectedIndex(oldTabPublic);

									messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.",
											JOptionPane.ERROR_MESSAGE);

									oldTabPublic = publicTabbedPane.getSelectedIndex();
									return;
								}
							}

							int imageTextSize = publicRestrictionImageTextField.getText().length();
							if (Utilities
									.translateTextType((String) publicImageTextDropDown
											.getSelectedItem()) == PublicationRights.TextType.footer
									&& imageTextSize > 65) {

								// publicTabbedPane.setSelectedIndex(oldTab);
								messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n"
										+ "Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");

								oldTabPublic = publicTabbedPane.getSelectedIndex();
								return;
							}

							if (Utilities
									.translateTextType((String) publicImageTextDropDown
											.getSelectedItem()) != PublicationRights.TextType.footer
									&& imageTextSize > 20) {

								// publicTabbedPane.setSelectedIndex(oldTab);
								messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n"
										+ "Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");

								oldTabPublic = publicTabbedPane.getSelectedIndex();
								return;
							}
						}
					} // Ende Reiter Bild

					oldTabPublic = publicTabbedPane.getSelectedIndex();

				}
			}
		});

		institutionTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				if (oldTabInstitution != institutionTabbedPane.getSelectedIndex()) {
					/*
					 * Reiter Text
					 */
					if (oldTabInstitution == 0) {

						if (institutionTextRestrictionCheckBox.isSelected()) {
							if (institutionRestrictionTextPagesTextField == null
									|| institutionRestrictionTextPagesTextField.getText().equals("")) {
								institutionTabbedPane.setSelectedIndex(oldTabInstitution);
								messageWriter.showMessage(
										"Bitte geben Sie die Seitennummern der zu\n" + "berücksichtigenden Seiten an.",
										JOptionPane.ERROR_MESSAGE);

								oldTabInstitution = institutionTabbedPane.getSelectedIndex();
								return;
							}

							sipFactory.getContractRights().getPublicRights()
									.setPages(institutionRestrictionTextPagesTextField.getText());
							if (sipFactory.getContractRights().getPublicRights().parsePages().equals("")) {
								institutionTabbedPane.setSelectedIndex(oldTabInstitution);
								messageWriter.showMessage(
										"Die Angabe der Seitenzahlen ist nicht \n" + "korrekt formuliert.",
										JOptionPane.ERROR_MESSAGE);

								oldTabInstitution = institutionTabbedPane.getSelectedIndex();
								return;
							}
						}
					} // Ende Reiter Text
					/*
					 * Reiter Bild
					 */
					if (oldTabInstitution == 1) {
						if (institutionImageTextCheckBox.isSelected()) {
							if (institutionRestrictionImageTextField.getText() == null
									|| institutionRestrictionImageTextField.getText().equals("")) {
								if (Utilities.translateTextType((String) institutionImageTextDropDown
										.getSelectedItem()) == PublicationRights.TextType.footer) {
									institutionTabbedPane.setSelectedIndex(oldTabInstitution);

									messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.",
											JOptionPane.ERROR_MESSAGE);

									oldTabInstitution = institutionTabbedPane.getSelectedIndex();
									return;
								} else {
									institutionTabbedPane.setSelectedIndex(oldTabInstitution);

									messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.",
											JOptionPane.ERROR_MESSAGE);

									oldTabInstitution = institutionTabbedPane.getSelectedIndex();
									return;
								}
							}

							int imageTextSize = institutionRestrictionImageTextField.getText().length();
							if (Utilities
									.translateTextType((String) institutionImageTextDropDown
											.getSelectedItem()) == PublicationRights.TextType.footer
									&& imageTextSize > 65) {

								// institutionTabbedPane.setSelectedIndex(oldTab);
								messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n"
										+ "Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");

								oldTabInstitution = institutionTabbedPane.getSelectedIndex();
								return;
							}

							if (Utilities
									.translateTextType((String) institutionImageTextDropDown
											.getSelectedItem()) != PublicationRights.TextType.footer
									&& imageTextSize > 20) {

								// institutionTabbedPane.setSelectedIndex(oldTab);
								messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n"
										+ "Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");

								oldTabInstitution = institutionTabbedPane.getSelectedIndex();
								return;
							}
						}
					} // Ende Reiter Bild

					oldTabInstitution = institutionTabbedPane.getSelectedIndex();

				}
			}
		});

	}

	/**
	 * Restores the file paths and compression setting chosen by the user in the
	 * last session
	 */
	private void loadLastSessionSettings() {
		File sourceFolderFile = new File(dataFolderPath + File.separator + "srcPath.sav");
		File destinationFolderFile = new File(dataFolderPath + File.separator + "destPath.sav");
		File workingFolderFile = new File(dataFolderPath + File.separator + "workPath.sav");
		File contractRightsLoadFolderFile = new File(dataFolderPath + File.separator + "crloadPath.sav");
		File contractRightsSaveFolderFile = new File(dataFolderPath + File.separator + "crsavePath.sav");
		File compressionSettingFile = new File(dataFolderPath + File.separator + "compSetting.sav");

		if (sourceFolderFile.exists()) {
			try {
				String sourceFolderPath = StringUtilities.readFile(sourceFolderFile);
				sourcePathChooser.setCurrentDirectory(new File(sourceFolderPath));
			} catch (Exception e) {
				logger.warn("Failed to read file " + sourceFolderFile.getAbsolutePath(), e);
			}
		}

		if (destinationFolderFile.exists()) {
			try {
				String destinationFolderPath = StringUtilities.readFile(destinationFolderFile);
				destinationPathChooser.setCurrentDirectory(new File(destinationFolderPath));
			} catch (Exception e) {
				logger.warn("Failed to read file " + destinationFolderFile.getAbsolutePath(), e);
			}
		}

		if (workingFolderFile.exists()) {
			try {
				String workingFolderPath = StringUtilities.readFile(workingFolderFile);
				workingPathChooser.setCurrentDirectory(new File(workingFolderPath));
			} catch (Exception e) {
				logger.warn("Failed to read file " + workingFolderFile.getAbsolutePath(), e);
			}
		}

		if (contractRightsLoadFolderFile.exists()) {
			try {
				String contractFileLoadPath = StringUtilities.readFile(contractRightsLoadFolderFile);
				contractFileLoadPathChooser.setSelectedFile(new File(contractFileLoadPath));
			} catch (Exception e) {
				logger.warn("Failed to read file " + contractRightsLoadFolderFile.getAbsolutePath(), e);
			}
		}

		if (contractRightsSaveFolderFile.exists()) {
			try {
				String contractFileSavePath = StringUtilities.readFile(contractRightsSaveFolderFile);
				contractFileSavePathChooser.setSelectedFile(new File(contractFileSavePath));
			} catch (Exception e) {
				logger.warn("Failed to read file " + contractRightsSaveFolderFile.getAbsolutePath(), e);
			}
		}

		if (compressionSettingFile.exists()) {
			try {
				String compressionSetting = StringUtilities.readFile(compressionSettingFile);
				compressionCheckBox.setSelected(Boolean.valueOf(compressionSetting));
			} catch (Exception e) {
				logger.warn("Failed to read file " + compressionSettingFile.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Checks if the current section (= panel) contains invalid settings/user
	 * input and makes the current panel invisible if no problems exist
	 * 
	 * @return true if the section could be left, otherwise false
	 */
	private boolean leaveSection() {

		if (startPanel.isVisible() && !uebertragenHistory) {

			UserInputValidator.Feedback feedback = UserInputValidator.checkPaths(sourcePathTextField.getText(),
					destinationPathTextField.getText(),
					Utilities.translateKindOfSIPBuilding((String) kindOfSIPBuildingDropDown.getSelectedItem()));

			switch (feedback) {
			case NO_SOURCE_PATH:
				messageWriter.showMessage("Bitte geben Sie einen Quellordner an.", JOptionPane.ERROR_MESSAGE);
				return false;

			case SOURCE_PATH_DOES_NOT_EXIST:
				messageWriter.showMessage("Der von Ihnen angegebene Quellordner existiert nicht.",
						JOptionPane.ERROR_MESSAGE);
				return false;

			case NO_DESTINATION_PATH:
				messageWriter.showMessage("Bitte geben Sie einen Zielordner an.", JOptionPane.ERROR_MESSAGE);
				return false;

			case FOLDER_EQUALITY:
				messageWriter.showMessage("Bitte stellen Sie sicher, dass Quell- und Zielordner nicht identisch sind.",
						JOptionPane.ERROR_MESSAGE);
				return false;

			case SUBFOLDER:
				messageWriter.showMessage(
						"Bitte stellen Sie sicher, dass der Zielordner kein Unterordner des Quellordners ist. ",
						JOptionPane.ERROR_MESSAGE);
				return false;

			case NON_DIRECTORY_FILES_EXIST:
				messageWriter.showMessage(
						"Das von Ihnen angegebene Quellverzeichnis enthält Dateien, die keine Ordner sind.\n"
								+ "Bitte überprüfen Sie, ob Sie die richtige Einstellung zur SIP-Generierung gewählt haben.",
						JOptionPane.ERROR_MESSAGE);
				return false;

			default:
				break;
			}

			if (collectionCheckBox.isSelected()) {
				feedback = UserInputValidator.checkCollectionName(collectionNameTextField.getText(),
						destinationPathTextField.getText());

				switch (feedback) {
				case NO_COLLECTION_NAME:
					messageWriter.showMessage("Bitte geben Sie den gewünschten Namen der Lieferung an.",
							JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_COLLECTION_NAME:
					messageWriter
							.showMessage(
									"Der von Ihnen gewählte Lieferungsname enthält Zeichen, die auf manchen Betriebssystemen für\n"
											+ "die Benennung von Dateien nicht erlaubt sind.",
									JOptionPane.ERROR_MESSAGE);
					return false;

				case COLLECTION_ALREADY_EXISTS:
					messageWriter.showMessage(
							"Im Zielverzeichnis existiert bereits eine Lieferung namens \""
									+ collectionNameTextField.getText() + "\".\n"
									+ "Bitte löschen Sie die bestehende Lieferung oder wählen Sie einen anderen Namen.",
							JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}
		}

		if (institutionTempPanel.isVisible()) {

			if (institutionTempRadioButton.isSelected()) {
				JComponent jComp = institutionTempStartDateChooserField.getDateEditor().getUiComponent();
				JTextField textField = (JTextField) jComp;
				String probablyDateStr = textField.getText();
				UserInputValidator.Feedback feedback = UserInputValidator.checkDate(probablyDateStr);

				switch (feedback) {

				case NO_DATE:
					messageWriter.showMessage("Bitte geben Sie ein Startdatum für die Publikation an.",
							JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_DATE:
					messageWriter.showMessage("Das von Ihnen angegebene Startdatum für die Publikation\n"
							+ "existiert nicht oder ist nicht korrekt formuliert.\n\n"
							+ "Bitte geben Sie das Datum in der Form [Tag].[Monat].[Jahr] an.\n"
							+ "(Beispiel: 14.07.2020)", JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}
		}

		if (publicTempPanel.isVisible()) {
			if (publicTempRadioButton.isSelected()) {
				JComponent jComp = publicTempStartDateChooserField.getDateEditor().getUiComponent();
				JTextField textField = (JTextField) jComp;
				String probablyDateStr = textField.getText();
				UserInputValidator.Feedback feedback = UserInputValidator.checkDate(probablyDateStr);

				switch (feedback) {

				case NO_DATE:
					messageWriter.showMessage("Bitte geben Sie ein Startdatum für die Publikation an.",
							JOptionPane.ERROR_MESSAGE);
					return false;

				case INVALID_DATE:
					messageWriter.showMessage("Das von Ihnen angegebene Startdatum für die Publikation\n"
							+ "existiert nicht oder ist nicht korrekt formuliert.\n\n"
							+ "Bitte geben Sie das Datum in der Form [Tag].[Monat].[Jahr] an.\n"
							+ "(Beispiel: 14.07.2020)", JOptionPane.ERROR_MESSAGE);
					return false;

				default:
					break;
				}
			}
		}

		if (institutionRestrictionPanel.isVisible()) {

			if (institutionImageTextCheckBox.isSelected()) {
				if (institutionRestrictionImageTextField.getText() == null
						|| institutionRestrictionImageTextField.getText().equals("")) {

					if (Utilities.translateTextType((String) institutionImageTextDropDown
							.getSelectedItem()) == PublicationRights.TextType.footer)
						messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.",
								JOptionPane.ERROR_MESSAGE);
					else
						messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.",
								JOptionPane.ERROR_MESSAGE);

					return false;
				}

				int imageTextSize = institutionRestrictionImageTextField.getText().length();
				if (Utilities.translateTextType(
						(String) institutionImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer
						&& imageTextSize > 65) {
					messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n"
							+ "Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");
					return false;
				}

				if (Utilities.translateTextType(
						(String) institutionImageTextDropDown.getSelectedItem()) != PublicationRights.TextType.footer
						&& imageTextSize > 20) {
					messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n"
							+ "Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");
					return false;
				}
			}

			if (institutionTextRestrictionCheckBox.isSelected()) {

				String pages = institutionRestrictionTextPagesTextField.getText();

				if (pages.equals("")) {
					messageWriter.showMessage(
							"Bitte geben Sie die Seitennummern der zu\n" + "berücksichtigenden Seiten an.",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

				sipFactory.getContractRights().getInstitutionRights().setPages(pages);
				if (sipFactory.getContractRights().getInstitutionRights().parsePages().equals("")) {
					messageWriter.showMessage("Die Angabe der Seitenzahlen ist nicht \n" + "korrekt formuliert.",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}

		if (publicRestrictionPanel.isVisible()) {

			if (publicImageTextCheckBox.isSelected()) {
				if (publicRestrictionImageTextField.getText() == null
						|| publicRestrictionImageTextField.getText().equals("")) {

					if (Utilities.translateTextType(
							(String) publicImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer)
						messageWriter.showMessage("Bitte geben Sie den Text der Fußzeile an.",
								JOptionPane.ERROR_MESSAGE);
					else
						messageWriter.showMessage("Bitte geben Sie den Text des Wasserzeichens an.",
								JOptionPane.ERROR_MESSAGE);

					return false;
				}

				int imageTextSize = publicRestrictionImageTextField.getText().length();
				if (Utilities.translateTextType(
						(String) publicImageTextDropDown.getSelectedItem()) == PublicationRights.TextType.footer
						&& imageTextSize > 65) {
					messageWriter.showMessage("Fußzeilen dürfen nicht mehr als 65 Zeichen lang sein.\n"
							+ "Ihr Text ist " + (imageTextSize - 65) + " Zeichen zu lang.");
					return false;
				}

				if (Utilities.translateTextType(
						(String) publicImageTextDropDown.getSelectedItem()) != PublicationRights.TextType.footer
						&& imageTextSize > 20) {
					messageWriter.showMessage("Wasserzeichen dürfen nicht mehr als 20 Zeichen lang sein.\n"
							+ "Ihr Text ist " + (imageTextSize - 20) + " Zeichen zu lang.");
					return false;
				}
			}

			if (publicTextRestrictionCheckBox.isSelected()) {

				String pages = publicRestrictionTextPagesTextField.getText();

				if (pages.equals("")) {
					messageWriter.showMessage(
							"Bitte geben Sie die Seitennummern der zu\n" + "berücksichtigenden Seiten an.",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

				sipFactory.getContractRights().getPublicRights().setPages(pages);
				if (sipFactory.getContractRights().getPublicRights().parsePages().equals("")) {
					messageWriter.showMessage("Die Angabe der Seitenzahlen ist nicht \n" + "korrekt formuliert.",
							JOptionPane.ERROR_MESSAGE);
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

		if (uebertragenPanel.isVisible()) {
			uebertragenHistory = false;
		}

		if (historiePanel.isVisible()) {
			uebertragenHistory = false;
		}

		return true;
	}

	/**
	 * Shows the start panel
	 */
	private void enterStartSection() {

		if (!startPanel.isVisible() && leaveSection()) {
			showCard(startPanel);
		}
	}

	/**
	 * Shows the load panel
	 */
	private void enterLoadSection() {
		if (!loadStandardPanel.isVisible() && leaveSection()) {
			showCard(loadStandardPanel);
		}
	}

	/**
	 * Shows the publication for institution panel
	 */
	private void enterPublicationInstitutionSection() {

		if (!institutionPanel.isVisible() && leaveSection()) {
			showCard(institutionPanel);
		}
	}

	/**
	 * Shows the publication for institution temp restriction panel
	 * 
	 * @return false if publication for institution is deactivated and the
	 *         section can't be entered, otherwise true
	 */
	private boolean enterPublicationInstitutionTempSection() {

		if (institutionDenyRadioButton.isSelected())
			return false;
		else if (!institutionTempPanel.isVisible() && leaveSection()) {
			showCard(institutionTempPanel);
		}

		return true;
	}

	/**
	 * Shows the publication for institution restrictions panel
	 * 
	 * @return false if publication for institution is deactivated and the
	 *         section can't be entered, otherwise true
	 */
	private boolean enterPublicationInstitutionRestrictionsSection() {

		if (institutionDenyRadioButton.isSelected())
			return false;
		else if (!institutionRestrictionPanel.isVisible() && leaveSection()) {
			showCard(institutionRestrictionPanel);
		}

		return true;
	}

	/**
	 * Shows the publication for public panel
	 */
	private void enterPublicationPublicSection() {

		if (!publicPanel.isVisible() && leaveSection()) {
			showCard(publicPanel);
		}
	}

	/**
	 * Shows the publication for public license restriction panel
	 * 
	 * @return false if publication for public is deactivated and the section
	 *         can't be entered, otherwise true
	 */
	private boolean enterPublicationPublicLicenseSection() {

		if (publicDenyRadioButton.isSelected())
			return false;
		else if (!publicLicensePanel.isVisible() && leaveSection()) {
			showCard(publicLicensePanel);
		}

		return true;
	}

	/**
	 * Shows the publication for public temp restriction panel
	 * 
	 * @return false if publication for public is deactivated and the section
	 *         can't be entered, otherwise true
	 */
	private boolean enterPublicationPublicTempSection() {

		if (publicDenyRadioButton.isSelected())
			return false;
		else if (!publicTempPanel.isVisible() && leaveSection()) {
			showCard(publicTempPanel);
		}

		return true;
	}

	/**
	 * Shows the publication for public restrictions panel
	 * 
	 * @return false if publication for public is deactivated and the section
	 *         can't be entered, otherwise true
	 */
	private boolean enterPublicationPublicRestrictionsSection() {

		if (publicDenyRadioButton.isSelected())
			return false;
		else if (!publicRestrictionPanel.isVisible() && leaveSection()) {
			showCard(publicRestrictionPanel);
		}

		return true;
	}

	/**
	 * Shows the migration panel
	 */
	private void enterMigrationSection() {

		if (!migrationPanel.isVisible() && leaveSection()) {
			showCard(migrationPanel);
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

			showCard(savePanel);
		}
	}

	private void enterUebertragenSection() {

		uebertragenHistory = true;

		if (!uebertragenPanel.isVisible() && leaveSection()) {
			showCard(uebertragenPanel);
		}

	}

	private void enterHistorieSection() {
		// TODO
		uebertragenHistory = true;

		if (!historiePanel.isVisible() && leaveSection()) {
			showCard(historiePanel);
		}
	}

	/**
	 * Shows the create panel
	 */
	private void enterCreateSection() {

		if (!createPanel.isVisible() && leaveSection()) {

			updateContractRights();

			showCard(createPanel);
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
			messageWriter.showMessage("Die Datei \"" + confFolderPath + File.separator
					+ "standardRights.xml\", in der die Standardrechte\n"
					+ "hinterlegt sind, konnte nicht gefunden werden.", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			sipFactory.getContractRights().loadContractRightsFromFile(standardRightsFile);
		} catch (Exception ex) {
			logger.error("Failed to load standard rights from file " + standardRightsFile.getAbsolutePath(), ex);
			messageWriter.showMessage("Beim Einlesen der Standardrechte ist ein Fehler aufgetreten.",
					JOptionPane.ERROR_MESSAGE);
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
		String workDir = workingPathTextField.getText();
		if (workDir == null || workDir.isEmpty()) {
			workDir = destinationPathTextField.getText();
		}

		sipFactory.setWorkingPath(workDir);
		sipFactory.setCreateCollection(collectionCheckBox.isSelected());
		sipFactory.setCollectionName(collectionNameTextField.getText());
		institutionRights.setAllowPublication(institutionAllowRadioButton.isSelected());
		institutionRights.setTempPublication(institutionTempRadioButton.isSelected());
		institutionRights.setLawPublication(institutionLawRadioButton.isSelected());

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date instDate = institutionTempStartDateChooserField.getDate();
		if (instDate != null) {
			String dateStr = dateFormat.format(instDate);
			institutionRights.setStartDate(dateStr);
		} else {
			institutionRights.setStartDate("");
		}

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
		institutionRights
				.setImageTextType(Utilities.translateTextType((String) institutionImageTextDropDown.getSelectedItem()));
		institutionRights.setWatermarkSize(
				contractSettings.getTextSizeImage(institutionImageTextSizeDropDown.getSelectedIndex()));
		institutionRights.setWatermarkOpacity(
				contractSettings.getOpacityImage(institutionImageTextOpacityDropDown.getSelectedIndex()));
		institutionRights
				.setAudioDuration(contractSettings.getDuration(institutionAudioDurationDropDown.getSelectedIndex()));
		institutionRights
				.setVideoSize(contractSettings.getHeightVideo(institutionVideoQualityDropDown.getSelectedIndex()));
		institutionRights
				.setVideoDuration(contractSettings.getDuration(institutionVideoDurationDropDown.getSelectedIndex()));
		publicRights.setAllowPublication(publicAllowRadioButton.isSelected());
		publicRights.setTempPublication(publicTempRadioButton.isSelected());
		publicRights.setLawPublication(publicLawRadioButton.isSelected());

		Date publDate = publicTempStartDateChooserField.getDate();
		if (publDate != null) {
			String dateStr = dateFormat.format(publDate);
			publicRights.setStartDate(dateStr);
		} else {
			publicRights.setStartDate("");
		}

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
		publicRights
				.setWatermarkSize(contractSettings.getTextSizeImage(publicImageTextSizeDropDown.getSelectedIndex()));
		publicRights.setWatermarkOpacity(
				contractSettings.getOpacityImage(publicImageTextOpacityDropDown.getSelectedIndex()));
		publicRights.setAudioDuration(contractSettings.getDuration(publicAudioDurationDropDown.getSelectedIndex()));
		publicRights.setVideoSize(contractSettings.getHeightVideo(publicVideoQualityDropDown.getSelectedIndex()));
		publicRights.setVideoDuration(contractSettings.getDuration(publicVideoDurationDropDown.getSelectedIndex()));
		contractRights.setConversionCondition((String) migrationDropDown.getSelectedItem());
		contractRights.setDdbExclusion(!publicDDBCheckBox.isSelected());

		if (premisLicenseRadioButton.isSelected()) {
			contractRights.setCclincense((CCLicense) licenseDropDown.getSelectedItem());
		} else if (metadataLicenseRadioButton.isSelected()) {
			contractRights.setCclincense(null);
		}
	}

	/**
	 * Copies the contract rights settings to the respective GUI elements
	 */
	private void updateValues() {

		ContractRights contractRights = sipFactory.getContractRights();
		PublicationRights institutionRights = contractRights.getInstitutionRights();
		PublicationRights publicRights = contractRights.getPublicRights();

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

			institutionTempStartDateChooserField.setEnabled(true);
		} else {
			institutionTempStartDateLabel.setEnabled(false);

			institutionTempStartDateChooserField.setEnabled(false);
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
		} else {
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

		institutionTempStartDateChooserField.setDate(institutionRights.getStartDate());
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
			publicTempStartDateChooserField.setEnabled(true);
		} else {
			publicTempStartDateLabel.setEnabled(false);
			publicTempStartDateChooserField.setEnabled(false);
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
		} else {
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
		} else {
			publicAudioDurationDropDown.setEnabled(false);
			publicRestrictionAudioDurationLabel.setEnabled(false);
		}

		if (publicRights.getVideoRestriction()) {
			publicVideoQualityDropDown.setEnabled(true);
			publicRestrictionVideoQualityLabel.setEnabled(true);
		} else {
			publicVideoQualityDropDown.setEnabled(false);
			publicRestrictionVideoQualityLabel.setEnabled(false);
		}

		if (publicRights.getVideoDurationRestriction()) {
			publicVideoDurationDropDown.setEnabled(true);
			publicRestrictionVideoDurationLabel.setEnabled(true);
		} else {
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

		publicTempStartDateChooserField.setDate(publicRights.getStartDate());
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

		migrationDropDown
				.setSelectedItem(Utilities.translateConversionCondition(contractRights.getConversionCondition()));
		publicDDBCheckBox.setSelected(!contractRights.getDdbExclusion());

		if (contractRights.getCclincense() != null) {
			licenseDropDown.setEditable(true);
			licenseDropDown.setEnabled(true);
			licenseTextField.setVisible(true);
			licenseDropDown.setSelectedItem(contractRights.getCclincense());
			premisLicenseRadioButton.setSelected(true);
		} else {
			licenseDropDown.setEditable(false);
			licenseDropDown.setEnabled(false);
			licenseTextField.setVisible(false);
			licenseDropDown.setSelectedItem(0);
			metadataLicenseRadioButton.setSelected(true);
		}
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
					settingsOverview += "          Position: "
							+ Utilities.translateTextTypePosition(publicRights.getImageTextType()) + "\n";
					settingsOverview += "          Sichtbarkeit: " + publicRights.getWatermarkOpacity() + "%\n";
					settingsOverview += "          Schriftgröße: " + publicRights.getWatermarkSize() + "\n";
				}
			}
			if (publicRights.getAudioRestriction())
				settingsOverview += "     Maximale Abspieldauer von Audiodateien: " + publicRights.getAudioDuration()
						+ " Sekunden\n";
			if (publicRights.getVideoRestriction())
				settingsOverview += "     Bildqualität von Videodateien: "
						+ (String) publicVideoQualityDropDown.getSelectedItem() + "\n";
			if (publicRights.getVideoDurationRestriction())
				settingsOverview += "     Maximale Abspieldauer von Videodateien: " + publicRights.getVideoDuration()
						+ " Sekunden\n";

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
					settingsOverview += "          Position: "
							+ Utilities.translateTextTypePosition(institutionRights.getImageTextType()) + "\n";
					settingsOverview += "          Sichtbarkeit: " + institutionRights.getWatermarkOpacity() + "%\n";
					settingsOverview += "          Schriftgröße: " + institutionRights.getWatermarkSize() + "\n";
				}
			}
			if (institutionRights.getAudioRestriction())
				settingsOverview += "     Maximale Abspieldauer von Audiodateien: "
						+ institutionRights.getAudioDuration() + " Sekunden\n";
			if (institutionRights.getVideoRestriction())
				settingsOverview += "     Bildqualität von Videodateien: "
						+ (String) institutionVideoQualityDropDown.getSelectedItem() + "\n";
			if (institutionRights.getVideoDurationRestriction())
				settingsOverview += "     Maximale Abspieldauer von Videodateien: "
						+ institutionRights.getVideoDuration() + " Sekunden\n";
		} else
			settingsOverview += "Nein\n";

		settingsOverview += "\nMigrationsbedingung: " + (String) migrationDropDown.getSelectedItem();
		if (publicRights.getAllowPublication()) {
			if (premisLicenseRadioButton.isSelected()) {
				settingsOverview += "\nLizenzangaben: Im SIP-Builder festgelegt : "
						+ licenseDropDown.getSelectedItem().toString();
			} else if (metadataLicenseRadioButton.isSelected()) {
				settingsOverview += "\nLizenzangaben: Aus den Metadaten übernehmen ";
			}
		}
		return settingsOverview;
	}

	/**
	 * Shows a file dialog that lets the user choose a certain directory
	 * 
	 * @param fileChooser
	 *            The file chooser object used to display the file dialog
	 * @return The path to the folder chosen by the user
	 */
	public String searchFolder(JFileChooser fileChooser) {

		File selectedFile = null;

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			selectedFile = fileChooser.getSelectedFile();

			if (selectedFile != null)
				return selectedFile.getPath() + File.separator;
			else
				return null;
		} else
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
	 * @param defaultFileName
	 *            The default name of the target file
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

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
}