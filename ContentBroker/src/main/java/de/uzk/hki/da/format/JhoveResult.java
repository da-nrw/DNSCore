package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.WellformednessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import de.uzk.hki.da.metadata.PremisXmlReaderNodeFactory;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.IOTimeoutException;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.StringUtilities;

public class JhoveResult {

	private static final Logger logger = LoggerFactory.getLogger(JhoveResult.class);
	public static final String StatusWellFormedAndValid = ("Well-Formed and valid");
	public static final String StatusWellFormedAndNotValid = ("Well-Formed, but not valid");
	public static final String StatusNotWellFormed = ("Not well-formed");

	String status;
	String format;
	String message;

	public static JhoveResult parseJHoveXML(String outFile) throws IOException,
			SAXException {
		Reader reader = new FileReader(outFile);
		final JhoveResult ret = new JhoveResult();

		NodeFactory nodeFactory = new PremisXmlReaderNodeFactory();

		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			reader.close();
			throw new IOException("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(new DefaultHandler());

		Builder parser = new Builder(xmlReader, false, nodeFactory);
		logger.trace("Successfully built builder and XML reader");

		try {
			Document doc = parser.build(reader);
			Element root = doc.getRootElement();
			Elements repInfo = root.getChildElements("repInfo", "http://hul.harvard.edu/ois/xml/ns/jhove");
			String statusString = repInfo.get(0).getChildElements("status", "http://hul.harvard.edu/ois/xml/ns/jhove").get(0).getChild(0).getValue();
			ret.setStatus(statusString);
			Elements formatElems=repInfo.get(0).getChildElements("format", "http://hul.harvard.edu/ois/xml/ns/jhove");
			String formatString ="";
			if(formatElems.size()>=1){
				formatString = formatElems.get(0).getChild(0).getValue();
			}
			ret.setFormat(formatString);
			StringBuilder messagesStringBuilder = new StringBuilder();

			Elements messagesElement = repInfo.get(0).getChildElements("messages", "http://hul.harvard.edu/ois/xml/ns/jhove");
			if (messagesElement.size() == 0)
				ret.setMessage(null);
			else {
				Elements messages = messagesElement.get(0).getChildElements("message", "http://hul.harvard.edu/ois/xml/ns/jhove");

				for (int messageNum = 0; messageNum < messages.size(); messageNum++) {
					Element iterMessage = messages.get(messageNum);

					// if(iterMessage.getAttribute("severity").getValue().equals("error")){
					messagesStringBuilder.append("[ ");
					messagesStringBuilder.append(iterMessage.getAttribute("severity").getValue());
					messagesStringBuilder.append(": ");
					messagesStringBuilder.append(iterMessage.getChild(0).getValue());
					messagesStringBuilder.append(" ]");
					// }
				}

				ret.setMessage(messagesStringBuilder.toString());
			}
		} catch (ValidityException ve) {
			throw new IOException(ve);
		} catch (ParsingException pe) {
			throw new IOException(pe);
		} catch (IOException ie) {
			throw new IOException(ie);
		}

		finally {
			reader.close();
		}

		return ret;
	}

	public boolean isValid() {
		//return status.equalsIgnoreCase(StatusWellFormedAndValid) || format.equalsIgnoreCase("XML");
		return status.equalsIgnoreCase(StatusWellFormedAndValid);
	}

	@Override
	public String toString() {
		return "JHoveResult [status=" + status + ", format=" + format + (message == null ? ""
				: ", message=" + message) + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
