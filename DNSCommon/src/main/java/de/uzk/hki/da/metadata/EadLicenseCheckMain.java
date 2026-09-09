package de.uzk.hki.da.metadata;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Kommandozeilen-Treiber, der eine EAD-Datei auf eine zur
 * Veroeffentlichung gueltige Lizenz prueft.
 *
 * <p>Verwendung:</p>
 * <pre>
 *   java de.uzk.hki.da.metadata.EadLicenseCheckMain &lt;pfad-zur-ead-datei&gt;
 * </pre>
 *
 * <p>Exit-Code 0, wenn alle Verzeichnungseinheiten veroeffentlichungsfaehig
 * sind, andernfalls Exit-Code 1.</p>
 */
public final class EadLicenseCheckMain {

	private EadLicenseCheckMain() {
		// Main-Klasse
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Aufruf: EadLicenseCheckMain <pfad-zur-ead-datei>");
			System.exit(2);
		}

		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(args[0]));
			EadParser parser = new EadParser(doc);

			List<EadLicenseCheck> checks = parser.getLicensesForWholeEAD();

			if (checks.isEmpty()) {
				System.out.println("Keine Verzeichnungseinheiten mit Digitalisat "
						+ "gefunden - keine Lizenzpruefung moeglich.");
				System.exit(1);
			}

			boolean allValid = true;
			for (EadLicenseCheck check : checks) {
				boolean valid = EadLicenseCheck.isValidForPublication(check);
				allValid &= valid;

				System.out.println("VZE: " + check.getUnitId()
						+ " - " + check.getTitle());
				System.out.println("   Metadaten : "
						+ describe(check.getMetadataLicense())
						+ " -> " + gueltig(validIfMetaValid(check)));
				System.out.println("   Digitalisat: "
						+ describe(check.getDaoLicense())
						+ " -> " + gueltig(validIfDaoValid(check)));
				for (String w : check.getWarnings()) {
					System.out.println("   Warnung: " + w);
				}
				System.out.println("   Ergebnis: "
						+ (valid ? "gultig" : "NICHT gueltig"));
				System.out.println();
			}

			System.out.println("Gesamtstatus: "
					+ (allValid ? "GUELTIG (veroeffentlichungsfaehig)"
							: "UNGUELTIG (mind. eine Einheit ist nicht "
									+ "veroeffentlichungsfaehig)"));
			System.exit(allValid ? 0 : 1);

		} catch (Exception e) {
			System.err.println("Fehler beim Parsen: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	private static boolean validIfMetaValid(EadLicenseCheck check) {
		return check.getMetadataLicense() != null
				&& LicenseValidator.isValidForPublication(
						check.getMetadataLicense().getHref());
	}

	private static boolean validIfDaoValid(EadLicenseCheck check) {
		return check.getDaoLicense() != null
				&& LicenseValidator.isValidForPublication(
						check.getDaoLicense().getHref());
	}

	private static String describe(EadLicense license) {
		if (license == null) {
			return "keine Angabe";
		}
		return license.getHref() + " (" + license.getText() + ")";
	}

	private static String gueltig(boolean valid) {
		return valid ? "gueltig" : "NICHT gueltig";
	}
}
