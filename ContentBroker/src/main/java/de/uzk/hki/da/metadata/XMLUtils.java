package de.uzk.hki.da.metadata;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

public class XMLUtils {
	
	/**
	 * Instantiates a new SAXBuilder of SAXParser with every a feature set
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
	
	public static SAXParser createNonvalidatingSaxParser() throws ParserConfigurationException, SAXException {
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		SAXParser saxParser = factory.newSAXParser();
		return saxParser;
	}

}
