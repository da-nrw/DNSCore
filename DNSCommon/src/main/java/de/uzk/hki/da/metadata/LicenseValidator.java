package de.uzk.hki.da.metadata;

/**
 * Bewertet, ob ein Lizenz-Bezug ({@code href}) eine Veroeffentlichung
 * erlaubt.
 *
 * <p>Als veroeffentlichungsfaehig gelten die Creative-Commons-Lizenzen
 * (z.B. CC BY, CC BY-SA, CC BY-NC, CC BY-ND, CC BY-NC-SA, CC BY-NC-ND)
 * sowie die CC0 Public Domain Dedication. Andere Rechtshinweise (z.B.
 * RightsStatements wie "In Copyright") gelten als nicht
 * veroeffentlichungsfaehig.</p>
 */
public final class LicenseValidator {

	/** Bekannte, veroeffentlichungsfaehige Lizenzpfade unter creativecommons.org. */
	private static final String[] VALID_PATHS = {
		"/publicdomain/zero/1.0",
		"/licenses/by/4.0",
		"/licenses/by/3.0",
		"/licenses/by/2.5",
		"/licenses/by/2.0",
		"/licenses/by/1.0",
		"/licenses/by-sa/4.0",
		"/licenses/by-sa/3.0",
		"/licenses/by-nd/4.0",
		"/licenses/by-nd/3.0",
		"/licenses/by-nc/4.0",
		"/licenses/by-nc/3.0",
		"/licenses/by-nc-nd/4.0",
		"/licenses/by-nc-nd/3.0",
		"/licenses/by-nc-sa/4.0",
		"/licenses/by-nc-sa/3.0"
	};

	private LicenseValidator() {
		// reine Hilfsklasse
	}

	/**
	 * Prueft, ob ein Lizenz-Bezug zur Veroeffentlichung gueltig ist.
	 *
	 * @param href der Lizenz-Bezug, kann {@code null} sein
	 * @return {@code true}, wenn die Lizenz veroeffentlichungsfaehig ist
	 */
	public static boolean isValidForPublication(String href) {
		if (href == null || href.trim().isEmpty()) {
			return false;
		}
		String normalized = normalize(href.trim());
		for (String path : VALID_PATHS) {
			if (normalized.contains(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Normalisiert eine Lizenz-URL fuer den Vergleich: ignoriert das
	 * Protokoll (http/https) und entfernt einen abschliessenden Slash.
	 *
	 * @param url die URL
	 * @return die normalisierte URL
	 */
	private static String normalize(String url) {
		String result = url;
		int scheme = result.indexOf("://");
		if (scheme >= 0) {
			result = result.substring(scheme + 3);
		}
		while (result.length() > 1 && result.endsWith("/")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
