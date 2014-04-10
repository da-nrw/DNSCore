package de.uzk.hki.da.utils;

import org.jdom.input.SAXBuilder;

public class XMLUtils {
	
	/**
	 * Instantiates a new SAXBuilder with every a feature set
	 * that prevents it from trying to access the web
	 * @author Sebastian Cuy
	 * @return the SAXBuilder
	 */
	public static SAXBuilder createNonvalidatingSaxBuilder() {
		SAXBuilder builder = new SAXBuilder(false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
		builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		return builder;
	}

}
