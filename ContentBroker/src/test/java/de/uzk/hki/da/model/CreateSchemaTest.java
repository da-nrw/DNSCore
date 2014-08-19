package de.uzk.hki.da.model;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.uzk.hki.da.utils.PasswordUtils;



public class CreateSchemaTest {

	
	@Test
	public void test(){
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.configure(createDecryptedConfigFile("src/main/xml/hibernateCentralDB.cfg.xml.postgres2"));
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		configuration.buildSessionFactory();
	}		
		
		
	
	/**
	 * Creates the decrypted config file.
	 *
	 * @param configFilePath the config file path
	 * @return the document
	 * @author: Christian Weitz
	 * @author: Thomas Kleinke
	 */
	private static Document createDecryptedConfigFile(String configFilePath) {
		if (!new File(configFilePath).exists()) throw new IllegalStateException(configFilePath+" does not exist");
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilderFactory.setValidating(false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(err);
			doc = docBuilder.parse(new File(configFilePath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in createDecryptedConfigFile - Maybe schema from hibernate"
					+ " config couldn't validate against an external resource.");
		}
		err.toString();
		NodeList properties = doc.getElementsByTagName("property");
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			NamedNodeMap attributes = property.getAttributes();
			
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attribute = attributes.item(j);
				if (attribute.getNodeValue().equals("connection.password"))
				{
					Node encryptedPasswordNode = property.getFirstChild();
					if (encryptedPasswordNode == null)
						return doc;
					String encryptedPassword = property.getFirstChild().getNodeValue();
					if (encryptedPassword == null || encryptedPassword.equals(""))
						return doc;
	
					String decryptedPassword = PasswordUtils.decryptPassword(encryptedPassword);
					property.getFirstChild().setNodeValue(decryptedPassword);
					return doc;
				}				
			}
		}
		
		return null;
	}
	/** The err. */
	private static ErrorHandler err = new ErrorHandler(){

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw new RuntimeException("---sax error");
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw new RuntimeException("---fatal error");
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw new RuntimeException("---saxparseexception");
		}
	};
	
}
