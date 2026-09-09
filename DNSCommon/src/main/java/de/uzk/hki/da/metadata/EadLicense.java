package de.uzk.hki.da.metadata;

/**
 * Eine in einer EAD-Datei referenzierte Lizenz bzw. ein Rechtshinweis.
 *
 * <p>Analog zu {@link MetsLicense} und {@link LidoLicense} kapselt diese
 * Klasse den Bezug ({@code href}) und eine menschenlesbare Bezeichnung
 * (hier: {@code text}) einer Rechteangabe aus einem EAD-Dokument.</p>
 */
public class EadLicense implements Comparable<EadLicense> {

	/** Typ der Rechteangabe fuer die Metadaten. */
	public static final String TYPE_EAD = "ead";

	/** Typ der Rechteangabe fuer die Digitalisate. */
	public static final String TYPE_DAO = "dao";

	private String href;
	private String text;
	private String type;

	public EadLicense() {
		super();
	}

	/**
	 * Erzeugt eine EAD-Lizenz.
	 *
	 * @param type Typ der Rechteangabe ({@link #TYPE_EAD} oder
	 *             {@link #TYPE_DAO})
	 * @param href  der Bezug (URL) der Lizenz
	 * @param text  eine menschenlesbare Bezeichnung
	 */
	public EadLicense(String type, String href, String text) {
		super();
		this.type = type;
		this.href = href;
		this.text = text;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "EadLicense [type=" + type + ", href=" + href
				+ ", text=" + text + "]";
	}

	@Override
	public int compareTo(EadLicense o) {
		return toString().compareTo(o.toString());
	}
}
