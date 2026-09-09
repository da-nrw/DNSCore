package de.uzk.hki.da.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.C;

/**
 * Parser fuer EAD(EAD(DDB))-Dokumente.
 *
 * <p>Neben der Extraktion von Referenzen und Metadatenfeldern stellt diese
 * Klasse Methoden zur Ermittlung der <em>Lizenzen</em> bereit, die fuer
 * eine Veroeffentlichung relevant sind:</p>
 * <ul>
 *   <li>Metadaten-Lizenz: {@code userestrict[@type='ead']/p/extref/@xlink:href}</li>
 *   <li>Digitalisat-Lizenz: {@code userestrict[@type='dao']/p/extref/@xlink:href}</li>
 * </ul>
 *
 * <p>Lokale Rechteangaben einer Verzeichnungseinheit ({@code c}) ueberschreiben
 * die globalen Angaben des Bestands ({@code archdesc}); andernfalls wird geerbt.</p>
 */
public class EadParser {

	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(EadParser.class);

	private static final Namespace XLINK_NS = Namespace.getNamespace(
			"xlink", "http://www.w3.org/1999/xlink");

	private Document eadDoc = new Document();

	private Namespace EAD_NS;

	public EadParser(Document doc) throws JDOMException {
		this.eadDoc = doc;
		EAD_NS = eadDoc.getRootElement().getNamespace();
		logger.debug("Setting namespace " + EAD_NS);
	}

//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@SuppressWarnings("unchecked")
	public List<String> getReferences() throws JDOMException, IOException {
		List<String> metsReferences = new ArrayList<String>();

		String namespaceUri = eadDoc.getRootElement().getNamespace().getURI();
		XPath xPath = XPath.newInstance(C.EAD_XPATH_EXPRESSION);

		// Case of new DDB EAD with namespace xmlns="urn:isbn:1-931666-22-9"
		if (!namespaceUri.equals("")) {
			xPath = XPath.newInstance("//isbn:daoloc/@href");
			xPath.addNamespace("isbn", eadDoc.getRootElement().getNamespace().getURI());
		}

		List allNodes = xPath.selectNodes(eadDoc);

		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}

	List<String> getTitle(Element element) {
		List<String> title = new ArrayList<String>();
		String t = "";
		try {
			t = element.getChild("unittitle", EAD_NS).getValue();
		} catch (Exception e) {
			logger.error("No unittitle element found");
		}
		title.add(t);
		return title;
	}

	List<String> getDate(Element element) {
		List<String> date = new ArrayList<String>();
		String d = "";
		try {
			d = element.getChild("unitdate", EAD_NS).getAttribute("normal").getValue();
			if (d.equals("")) {
				d = element.getChild("unitdate", EAD_NS).getValue();
			}
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		date.add(d);
		return date;
	}

	List<String> getUnitIDs(Element did) {
		List<String> unitIDs = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> children = did.getChildren("unitid", EAD_NS);

		for (Element child : children) {
			String unitID = "";
			try {
				unitID = child.getValue();
				if (!unitID.equals("")) {
					unitIDs.add(unitID);
				}
			} catch (Exception e) {
				logger.debug("No unitid element found");
			}
		}
		return unitIDs;
	}

	List<String> getHref(Element daogrp) {
		List<String> hrefs = new ArrayList<String>();
		String href = "";
		try {
			href = daogrp.getChild("daoloc", EAD_NS).getAttributeValue("href");
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		hrefs.add(href);
		return hrefs;
	}

//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  LIZENZEN  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * Ermittelt fuer jede Verzeichnungseinheit mit Digitalisat die
	 * <em>effektiven</em> Lizenzen (Metadaten und Digitalisat) unter
	 * Beruecksichtigung der Vorrangregel: Lokale Rechteangaben der Einheit
	 * ueberschreiben die globalen Angaben des Bestands ({@code archdesc});
	 * andernfalls wird geerbt.
	 *
	 * @return Liste der Lizenz-Pruefungen pro Verzeichnungseinheit
	 * @throws JDOMException bei Fehlern in der XPath-Auswertung
	 */
	@SuppressWarnings("unchecked")
	public List<EadLicenseCheck> getLicensesForWholeEAD() throws JDOMException {
		List<EadLicenseCheck> checks = new ArrayList<EadLicenseCheck>();

		EadLicense globalMetadata = getLicenseFromElement(
				getRootArchdesc(), "ead", "Recht der Metadaten (global)");
		EadLicense globalDao = getLicenseFromElement(
				getRootArchdesc(), "dao", "Recht der Digitalisate (global)");

		String nsPrefix = eadPrefix();
		XPath cXPath = XPath.newInstance("//" + name(nsPrefix, "c"));
		if (!nsPrefix.isEmpty()) {
			cXPath.addNamespace(nsPrefix,
					eadDoc.getRootElement().getNamespace().getURI());
		}

		List allNodes = cXPath.selectNodes(eadDoc);
		for (Object node : allNodes) {
			Element c = (Element) node;
			if (!hasDirectDigitalObject(c)) {
				continue;
			}

			EadLicenseCheck check = new EadLicenseCheck(
					getUnitId(c), getUnitTitle(c));

			EadLicense localMetadata = getLicenseFromElement(c, "ead",
					"Recht der Metadaten (lokal)");
			EadLicense localDao = getLicenseFromElement(c, "dao",
					"Recht der Digitalisate (lokal)");

			EadLicense metadata = (localMetadata != null)
					? localMetadata : globalMetadata;
			EadLicense dao = (localDao != null) ? localDao : globalDao;

			check.setMetadataLicense(metadata);
			check.setDaoLicense(dao);

			if (metadata == null) {
				check.addWarning("Keine Metadaten-Lizenz fuer '"
						+ check.getUnitId() + "' definiert.");
			}
			if (dao == null) {
				check.addWarning("Keine Digitalisat-Lizenz fuer '"
						+ check.getUnitId() + "' definiert.");
			}

			checks.add(check);
		}

		return checks;
	}

	/**
	 * Prueft, ob das gesamte EAD-Dokument eine zur Veroeffentlichung
	 * gueltige Lizenz besitzt.
	 *
	 * @return {@code true}, wenn alle Verzeichnungseinheiten
	 *         veroeffentlichungsfaehig sind
	 * @throws JDOMException bei Fehlern in der XPath-Auswertung
	 */
	public boolean isPublishable() throws JDOMException {
		List<EadLicenseCheck> checks = getLicensesForWholeEAD();
		if (checks.isEmpty()) {
			return false;
		}
		for (EadLicenseCheck check : checks) {
			if (!EadLicenseCheck.isValidForPublication(check)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prueft, ob in den EAD-Metadaten eine ausdruecklich <em>vorhandene</em>,
	 * aber zur Veroeffentlichung ungueltige Rechteangabe vorkommt (z.B.
	 * "In Copyright").
	 *
	 * <p>Im Gegensatz zu {@link #isPublishable()} wird hier zwischen
	 * "gar keine Lizenz angegeben" und "eine ungueltige Lizenz angegeben"
	 * unterschieden. Nur ein vorhandener, aber ungueltiger Rechtshinweis
	 * liefert {@code true}.</p>
	 *
	 * @return {@code true}, wenn mindestens eine vorhandene Rechteangabe
	 *         nicht veroeffentlichungsfaehig ist
	 * @throws JDOMException bei Fehlern in der XPath-Auswertung
	 */
	public boolean hasInvalidLicenseForPublication() throws JDOMException {
		List<EadLicenseCheck> checks = getLicensesForWholeEAD();
		if (checks.isEmpty()) {
			return false;
		}
		for (EadLicenseCheck check : checks) {
			EadLicense meta = check.getMetadataLicense();
			EadLicense dao = check.getDaoLicense();
			if ((meta != null
					&& !LicenseValidator.isValidForPublication(meta.getHref()))
					|| (dao != null
							&& !LicenseValidator.isValidForPublication(dao.getHref()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Liefert das {@code archdesc}-Element des Dokuments.
	 *
	 * @return das {@code archdesc}-Element oder {@code null}.
	 */
	@SuppressWarnings("unchecked")
	private Element getRootArchdesc() throws JDOMException {
		String nsPrefix = eadPrefix();
		XPath xPath = XPath.newInstance("/" + name(nsPrefix, "ead") + "/"
				+ name(nsPrefix, "archdesc"));
		if (!nsPrefix.isEmpty()) {
			xPath.addNamespace(nsPrefix,
					eadDoc.getRootElement().getNamespace().getURI());
		}
		List nodes = xPath.selectNodes(eadDoc);
		if (nodes.isEmpty()) {
			return null;
		}
		return (Element) nodes.get(0);
	}

	/**
	 * Liefert den im XPath zu verwendenden Praefix fuer das EAD-Namespace
	 * (ohne Doppelpunkt). Ist das Dokument ohne Namespace, wird ein leerer
	 * Praefix zurueckgegeben.
	 *
	 * @return den Praefix (ohne Doppelpunkt) oder leer, wenn kein Namespace
	 */
	private String eadPrefix() {
		String uri = eadDoc.getRootElement().getNamespace().getURI();
		return uri.isEmpty() ? "" : "isbn";
	}

	/**
	 * Baut aus einem Praefix und einem Elementnamen ein XPath-Pfadsegment.
	 * Bei leerem Praefix (Dokument ohne Namespace) wird nur der Name
	 * verwendet.
	 *
	 * @param prefix der Praefix (ohne Doppelpunkt) oder leer
	 * @param name   der lokale Elementname
	 * @return das Pfadsegment
	 */
	private String name(String prefix, String name) {
		return prefix.isEmpty() ? name : prefix + ":" + name;
	}

	/**
	 * Ermittelt die Lizenz direkt aus einem Element (globaler Bestand oder
	 * einzelne Verzeichnungseinheit).
	 *
	 * @param element das Element, z.B. {@code archdesc} oder {@code c}
	 * @param type    der Rechte-Typ ({@code ead} oder {@code dao})
	 * @param fallbackText Anzeigetext, falls kein extref-Text vorhanden ist
	 * @return die Lizenz oder {@code null}
	 * @throws JDOMException bei XPath-Fehlern
	 */
	@SuppressWarnings("unchecked")
	private EadLicense getLicenseFromElement(Element element, String type,
			String fallbackText) throws JDOMException {
		if (element == null) {
			return null;
		}
		String nsPrefix = eadPrefix();
		XPath xPath = XPath.newInstance(name(nsPrefix, "userestrict")
				+ "[@type='" + type + "']/" + name(nsPrefix, "p") + "/"
				+ name(nsPrefix, "extref"));
		if (!nsPrefix.isEmpty()) {
			xPath.addNamespace(nsPrefix,
					eadDoc.getRootElement().getNamespace().getURI());
		}

		List nodes = xPath.selectNodes(element);
		if (nodes.isEmpty()) {
			return null;
		}
		Element extref = (Element) nodes.get(0);
		String href = extref.getAttributeValue("href", XLINK_NS);
		if (href == null || href.trim().isEmpty()) {
			return null;
		}
		String text = extref.getValue();
		if (text == null || text.trim().isEmpty()) {
			text = fallbackText;
		}
		return new EadLicense(type, href, text);
	}

	/**
	 * Prueft, ob eine Verzeichnungseinheit ein Digitalisat
	 * ({@code daogrp}/{@code daoloc}) als direktes Kind besitzt.
	 *
	 * @param c die Verzeichnungseinheit
	 * @return {@code true}, wenn direkt ein Digitalisat angehaengt ist
	 */
	private boolean hasDirectDigitalObject(Element c) {
		List children = c.getChildren();
		for (Object child : children) {
			String name = ((Element) child).getName();
			if ("daogrp".equals(name) || "daoloc".equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Liefert die Signatur ({@code unitid}) einer Verzeichnungseinheit.
	 *
	 * @param c die Verzeichnungseinheit
	 * @return die Signatur oder ein leerer String
	 */
	@SuppressWarnings("unchecked")
	private String getUnitId(Element c) {
		if (c == null) {
			return "";
		}
		try {
			Element did = c.getChild("did", EAD_NS);
			Element unitid = (did == null)
					? null : did.getChild("unitid", EAD_NS);
			return (unitid == null) ? "" : unitid.getTextTrim();
		} catch (Exception e) {
			logger.debug("No unitid element found", e);
			return "";
		}
	}

	/**
	 * Liefert den Titel ({@code unittitle}) einer Verzeichnungseinheit.
	 *
	 * @param c die Verzeichnungseinheit
	 * @return den Titel oder ein leerer String
	 */
	@SuppressWarnings("unchecked")
	private String getUnitTitle(Element c) {
		if (c == null) {
			return "";
		}
		try {
			Element did = c.getChild("did", EAD_NS);
			Element unittitle = (did == null)
					? null : did.getChild("unittitle", EAD_NS);
			return (unittitle == null) ? "" : unittitle.getTextTrim();
		} catch (Exception e) {
			logger.debug("No unittitle element found", e);
			return "";
		}
	}
}
