package de.uzk.hki.da.service;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.core.RegisterObjectService;
import de.uzk.hki.da.core.UserException.UserExceptionId;

/**
 * 
 * @author Thomas Kleinke
 */
public class UserExceptionManager {

	static final Logger logger = LoggerFactory.getLogger(RegisterObjectService.class);
	
	private HashMap<UserExceptionId, String> messageMap = new HashMap<UserExceptionId, String>();
	
	
	public void readConfigFile() throws IOException {
		
		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			throw new IOException("Error creating SAX parser", e);
		}
		
		xmlReader.setErrorHandler(new ErrorHandler() {

			@Override
			public void error(SAXParseException e) throws SAXException {
				throw new SAXException("Error while parsing user exception messages config file", e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw new SAXException("Fatal error while parsing user exception messages config file", e);
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				logger.warn("Warning while parsing user exception messages config file", e);
			}
		});
		
		Builder parser = new Builder(xmlReader, false);
			
		try {
			Document doc = parser.build(this.getClass().getClassLoader().getResourceAsStream("META-INF/userExceptionMessages.xml"));
			Element root = doc.getRootElement();
			
			Elements elements = root.getChildElements("userExceptionMessage");
			for (int i = 0; i < elements.size(); i++) {
				UserExceptionId id = UserExceptionId.valueOf(elements.get(i).getAttribute("id").getValue());
				String messageText = elements.get(i).getValue().trim().replace("\\n", "\n");
				messageMap.put(id, messageText);
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	public String getMessage(UserExceptionId userExceptionId) {		
		return messageMap.get(userExceptionId);
	}
}
