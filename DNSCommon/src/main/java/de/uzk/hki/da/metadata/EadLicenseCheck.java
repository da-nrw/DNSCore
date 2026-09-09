package de.uzk.hki.da.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Ergebnis der Lizenzermittlung fuer eine Verzeichnungseinheit einer
 * EAD-Datei.
 *
 * <p>Enthaelt die <em>effektiven</em> Lizenzen fuer Metadaten und
 * Digitalisat einer Verzeichnungseinheit - d.h. nach Anwendung der
 * Vorrangregel: Lokale Rechteangaben der Einheit ueberschreiben die
 * globalen Angaben des Bestands; andernfalls wird geerbt.</p>
 */
public class EadLicenseCheck {

	private final String unitId;
	private final String title;

	private EadLicense metadataLicense;
	private EadLicense daoLicense;

	private final List<String> warnings = new ArrayList<String>();

	public EadLicenseCheck(String unitId, String title) {
		this.unitId = (unitId == null) ? "" : unitId;
		this.title = (title == null) ? "" : title;
	}

	public String getUnitId() {
		return unitId;
	}

	public String getTitle() {
		return title;
	}

	public EadLicense getMetadataLicense() {
		return metadataLicense;
	}

	public void setMetadataLicense(EadLicense metadataLicense) {
		this.metadataLicense = metadataLicense;
	}

	public EadLicense getDaoLicense() {
		return daoLicense;
	}

	public void setDaoLicense(EadLicense daoLicense) {
		this.daoLicense = daoLicense;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void addWarning(String warning) {
		warnings.add(warning);
	}

	/**
	 * Prueft, ob die Einheit fuer eine Veroeffentlichung gueltige Lizenzen
	 * besitzt (Metadaten- und Digitalisat-Lizenz muessen beide gueltig sein).
	 *
	 * @return {@code true}, wenn beide Lizenzen veroeffentlichungsfaehig sind
	 */
	public boolean isValidForPublication() {
		return isValidForPublication(this);
	}

	/**
	 * Prueft, ob eine gegebene Lizenz-Prüfung fuer die Veroeffentlichung
	 * gueltig ist.
	 *
	 * @param check die Pruefung
	 * @return {@code true}, wenn beide Lizenzen gueltig sind
	 */
	public static boolean isValidForPublication(EadLicenseCheck check) {
		EadLicense meta = check.getMetadataLicense();
		EadLicense dao = check.getDaoLicense();
		return meta != null && LicenseValidator.isValidForPublication(meta.getHref())
				&& dao != null && LicenseValidator.isValidForPublication(dao.getHref());
	}

	@Override
	public String toString() {
		return "EadLicenseCheck [unitId=" + unitId + ", metadata="
				+ metadataLicense + ", dao=" + daoLicense + "]";
	}
}
