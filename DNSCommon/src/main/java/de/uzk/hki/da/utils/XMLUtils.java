package de.uzk.hki.da.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
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
	
	public static File getCanonicalFileFromReference(String ref, File metadataFile) throws IOException {
		
		String parentFilePath = "";
		if (metadataFile.getParentFile() != null)
			parentFilePath=metadataFile.getParentFile().getPath();
		
		String tmpFilePath = new RelativePath(parentFilePath, ref).toString();
		
		File file = new File(new File(tmpFilePath).getCanonicalFile().toString().replace(new File("").getCanonicalFile().toString(), ""));
		
		return file;
	}
	
	public static String identifyMetadataType(File f) throws IOException{
		String beginningOfFile = convertFirst10LinesOfFileToString(f);
		if (beginningOfFile.matches(C.EAD_PATTERN))  {
			return C.SUBFORMAT_IDENTIFIER_EAD;
		}
		if (beginningOfFile.matches(C.METS_PATTERN)) {
			return C.SUBFORMAT_IDENTIFIER_METS;
		}
		if (beginningOfFile.matches(C.LIDO_PATTARN)) {
			return C.SUBFORMAT_IDENTIFIER_LIDO;
		} 
		return "";
	}
	
	private static String convertFirst10LinesOfFileToString(File f) throws IOException {
		
		String result = "";
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		int lineCount=0;
		while ((line = br.readLine()) != null) {
		   // process the line.
			result+=line;
			lineCount++;
			if (lineCount==10) break;
		}
		br.close();
		return result;
	}
	
	public static Document getDocumentFromXMLFile(File file) throws IOException, JDOMException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(file);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
		return metsDoc;
	}

}
