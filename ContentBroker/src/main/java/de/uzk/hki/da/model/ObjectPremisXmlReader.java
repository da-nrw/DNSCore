/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.PremisXmlReaderNodeFactory;


/**
 * Translates contents of a PREMIS file to the native object model used by the ContentBroker.
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 * @author Thomas Kleinke
 */
public class ObjectPremisXmlReader{

	/** The Constant DATE_FORMAT_WITH_TIMEZONE. */
	private static final SimpleDateFormat DATE_FORMAT_WITH_TIMEZONE = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	/** The Constant DATE_FORMAT_WITH_TIMEZONE. */
	private static final SimpleDateFormat DATE_FORMAT_WITH_TIMEZONE_WITHOUT_MILLISECONDS = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/** The Constant DATE_FORMAT_WITHOUT_TIMEZONE. */
	private static final SimpleDateFormat DATE_FORMAT_WITHOUT_TIMEZONE = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	/** The Constant DATE_FORMAT_WITHOUT_DATE_AND_TIMEZONE. */
	private static final SimpleDateFormat DATE_FORMAT_WITHOUT_DATE_AND_TIMEZONE = 
			new SimpleDateFormat("yyyy-MM-dd");
	
	/** The Constant XSI_NS. */
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	
	/** The Constant PREMIS_NS. */
	private static final String PREMIS_NS = "info:lc/xmlns/premis-v2";
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ObjectPremisXmlReader.class);
	
	/** The err. */
	private static ErrorHandler err = new ErrorHandler(){

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw new SAXException("Error while parsing premis file", e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw new SAXException("Fatal error while parsing premis file", e);
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			logger.warn("Warning while parsing premis file", e);
		}
	};
	
	/**
	 * Deserialize.
	 *
	 * @param reader the reader
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 * @author Daniel M. de Oliveira
	 */
	public Object deserialize(File file) throws IOException, ParseException, NullPointerException {

		Reader reader = new FileReader(file);
		
		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			reader.close();
			throw new IOException("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(err);
		NodeFactory nodeFactory = new PremisXmlReaderNodeFactory();
		Builder parser = new Builder(xmlReader, false, nodeFactory);
		logger.trace("Successfully built builder and XML reader");
		
		Object object = null;
		
		try {
			Document doc = parser.build(reader);
			Element root = doc.getRootElement();
			
			logger.trace("Read root element");
			
			// Object & packages
			List<Package> packages = new ArrayList<Package>();
			Elements objectElements = root.getChildElements("object", PREMIS_NS);
			for (int i = 0; i < objectElements.size(); i++) {
				String objectIdType = objectElements.get(i).getFirstChildElement("objectIdentifier", PREMIS_NS)
						 .getFirstChildElement("objectIdentifierType", PREMIS_NS).getValue();				
				
				if (objectIdType.toUpperCase().equals("URN") || objectIdType.toUpperCase().equals("OBJECT_BUSINESS_KEY"))
					object = buildObject(objectElements.get(i));
				
				else if (objectElements.get(i).getFirstChildElement("objectIdentifier", PREMIS_NS)
						 .getFirstChildElement("objectIdentifierType", PREMIS_NS).getValue()
						 .toUpperCase().equals("PACKAGE_NAME"))
					packages.add(buildPackage(objectElements.get(i)));					
			}
			
			if (object == null)
				object = new Object();
			if (packages.size() == 0){
				logger.warn("Incomplete PREMIS - no package element found");
			}
			
			object.setPackages(packages);
			
			// DAFiles			
			for (int i = 0; i < objectElements.size(); i++) {
				if (objectElements.get(i).getAttribute("type", XSI_NS).getValue().equals("file"))
					createFile(packages, objectElements.get(i));
			}			
			
			// Events
			Elements eventElements = root.getChildElements("event", PREMIS_NS);
			for (int i = 0; i < eventElements.size(); i++)
				buildEvent(eventElements.get(i), object);
			
			// Agents
			Elements agentElements = root.getChildElements("agent", PREMIS_NS);
			for (int i = 0; i < agentElements.size(); i++)
				buildAgent(agentElements.get(i), object);
			
			logger.trace("Build rights");
			Element rightsElement = root.getFirstChildElement("rights", PREMIS_NS);
			
			if (rightsElement == null) throw new RuntimeException("Rights element is null");
			
			RightsStatement right = buildRight(object,rightsElement);
			if (right != null) object.setRights(right);
			
			
		}
		catch (ValidityException ve) {throw new IOException(ve);}
		catch (ParsingException pe) {throw new IOException(pe);}
		catch (IOException ie) {throw new IOException(ie);}
			
		finally {
			reader.close();
		}
		
		return object;
	}
	
	/**
	 * Builds the event.
	 *
	 * @param el the el
	 * @param object the object
	 * @return the event
	 * @author Daniel M. de Oliveira
	 * @author Sebastian Cuy
	 * @author Thomas Kleinke
	 * Maps an event from premis to native data model.
	 */
	private Event buildEvent(Element el, Object object) throws NullPointerException {
		if (object.getPackages().isEmpty()) 
			throw new InvalidParameterException("Error: Object is not consistent. Has no package.");
		
		
		Event event = new Event();
		
		event.setIdType(enumValue(el.getFirstChildElement("eventIdentifier", PREMIS_NS)
				.getFirstChildElement("eventIdentifierType", PREMIS_NS), Event.IdType.class));
		
		event.setIdentifier(el.getFirstChildElement("eventIdentifier", PREMIS_NS)
				.getFirstChildElement("eventIdentifierValue", PREMIS_NS).getValue());
		
		String eventType = el.getFirstChildElement("eventType", PREMIS_NS).getValue();
		event.setType(eventType);
		
		event.setDate(readDate(el.getFirstChildElement("eventDateTime", PREMIS_NS).getValue()));
		
		if (eventType.equals("CONVERT"))
			event.setDetail(el.getFirstChildElement("eventDetail", PREMIS_NS).getValue());
		
		event.setAgent_type(Agent.getTypeForIdType(el.getFirstChildElement("linkingAgentIdentifier", PREMIS_NS)
				.getFirstChildElement("linkingAgentIdentifierType", PREMIS_NS).getValue()));
		
		event.setAgent_name(el.getFirstChildElement("linkingAgentIdentifier", PREMIS_NS)
				.getFirstChildElement("linkingAgentIdentifierValue", PREMIS_NS).getValue());
		
		Elements linkingObjectIdentifiers = el.getChildElements("linkingObjectIdentifier", PREMIS_NS);
		String sourceFile = "";
		String outcomeFile = "";
		String nonConvertEventIdentifier = "";
		
		for (int i = 0; i < linkingObjectIdentifiers.size(); i++) {
			Element linkingObjectIdentifier = linkingObjectIdentifiers.get(i);
			
			String linkingObjectIdentifierValue = linkingObjectIdentifier
					.getFirstChildElement("linkingObjectIdentifierValue", PREMIS_NS).getValue();
			
			Element linkingObjectRole = linkingObjectIdentifier.getFirstChildElement("linkingObjectRole", PREMIS_NS);
			if (linkingObjectRole != null) {
				if (linkingObjectRole.getValue().toLowerCase().equals("source"))
					sourceFile = linkingObjectIdentifierValue;
				else if (linkingObjectRole.getValue().toLowerCase().equals("outcome"))
					outcomeFile = linkingObjectIdentifierValue;		
				else
					nonConvertEventIdentifier = linkingObjectIdentifierValue;
			}
			else
				nonConvertEventIdentifier = linkingObjectIdentifierValue;
		}		
		
		logger.debug("non convert event identifier: " + nonConvertEventIdentifier);
		
		boolean eventAdded = false;		
		if (eventType.toUpperCase().equals(C.EVENT_TYPE_CONVERT)
				|| eventType.toUpperCase().equals(C.EVENT_TYPE_COPY)
				|| eventType.toUpperCase().equals(C.EVENT_TYPE_CREATE)) {
			for (Package pkg : object.getPackages()) {
				for (DAFile f : pkg.getFiles()) {
					if (sourceFile.equals("") && outcomeFile.equals("")
							&& nonConvertEventIdentifier.equals(f.getRep_name() + "/" + f.getRelative_path())) {
						event.setSource_file(f);
						pkg.getEvents().add(event);
						eventAdded = true;
						break;
					}
					
					if (sourceFile.equals(f.getRep_name() + "/" + f.getRelative_path())) {
						event.setSource_file(f);
						if (event.getTarget_file() != null) {
							pkg.getEvents().add(event);
							eventAdded = true;
							break;
						}
					}
					
					if (outcomeFile.equals(f.getRep_name() + "/" + f.getRelative_path())) {
						event.setTarget_file(f);
						if (eventType.toUpperCase().equals("CREATE") || event.getSource_file() != null) {
							pkg.getEvents().add(event);
							eventAdded = true;
							break;
						}
					}
				}
			}
		}
		else {
			for (Package pkg : object.getPackages()) {
				if (object.getIdentifier() != null)
					logger.debug("package name: " + object.getIdentifier() + ".pack_" + pkg.getName() + ".tar");
				if (nonConvertEventIdentifier.equals(object.getIdentifier() + ".pack_" + pkg.getName() + ".tar")) {
					pkg.getEvents().add(event);
					eventAdded = true;
					break;
				}
			}			
		}
		
		if (!eventAdded) {
			if (eventType.toUpperCase().equals("SIP_CREATION"))
				object.getPackages().get(0).getEvents().add(event);
			else
				throw new RuntimeException("Premis file is not consistent: couldn't find object(s) referenced by event "
											+ event.getIdentifier());						
		}
		
		logger.debug("Deserialized event: " + event);
		
		return event;
	}
	
	/**
	 * Builds the right.
	 *
	 * @param rightsEl the rights el
	 * @return the rights statement
	 * @author Sebastian Cuy
	 * @author Thomas Kleinke
	 */
	private RightsStatement buildRight(Object object, Element rightsEl) throws NullPointerException {
		logger.debug("Start reading rights section");
		
		if (rightsEl != null) {
			
			RightsStatement right = new RightsStatement();
			MigrationRight migrationRight = new MigrationRight();
			
			Elements rightsGrantedElements = rightsEl.getFirstChildElement("rightsStatement", PREMIS_NS)
					.getChildElements("rightsGranted", PREMIS_NS);
			for (int i = 0; i < rightsGrantedElements.size(); i++) {
				Element actEl = rightsGrantedElements.get(i).getFirstChildElement("act", PREMIS_NS);
				if (stringValue(actEl).toUpperCase().equals("MIGRATION"))
				{
					Element migrationRightsTermOfGrantEl = rightsGrantedElements.get(i)
							.getFirstChildElement("termOfGrant", PREMIS_NS);
					if (migrationRightsTermOfGrantEl != null) {
						Element migrationRightStartDateEl =	migrationRightsTermOfGrantEl
							.getFirstChildElement("startDate", PREMIS_NS);
					
						if (migrationRightStartDateEl != null)
							migrationRight.setStartDate(readDate(migrationRightStartDateEl.getValue()));
					}
				}
			}
			
			Element rightsExtEl = rightsEl.getFirstChildElement("rightsExtension", PREMIS_NS)
					.getFirstChildElement("rightsGranted", C.CONTRACT_NS);
			
			Element migrationRightEl = rightsExtEl
					.getFirstChildElement("migrationRight", C.CONTRACT_NS);
			if (migrationRightEl != null) {
				migrationRight.setCondition(enumValue(migrationRightEl
						.getFirstChildElement("condition",C.CONTRACT_NS),
						MigrationRight.Condition.class));
				right.setMigrationRight(migrationRight);
			}
			
			Element ddbExclusionEl = rightsEl.getFirstChildElement("DDBexclusion", C.CONTRACT_NS);
			object.setDdbExclusion(ddbExclusionEl != null);
			
			Elements publicationRightEls = rightsExtEl
					.getChildElements("publicationRight", C.CONTRACT_NS);
			
			for (int i = 0; i < publicationRightEls.size(); i++) {
				
				Element publicationRightEl = publicationRightEls.get(i);
				
				PublicationRight publicationRight = new PublicationRight();
				publicationRight.setAudience(enumValue(publicationRightEl
						.getFirstChildElement("audience", C.CONTRACT_NS),
						PublicationRight.Audience.class));

				Element startDateEl = publicationRightEl
						.getFirstChildElement("startDate", C.CONTRACT_NS);
				if (startDateEl != null)
					publicationRight.setStartDate(
							(readDate(publicationRightEl.getFirstChildElement("startDate", C.CONTRACT_NS).getValue())));
				
				publicationRight.setLawID(stringValue(publicationRightEl
						.getFirstChildElement("lawID", C.CONTRACT_NS)));
				
				Element restrictionsEl = publicationRightEl
						.getFirstChildElement("restrictions", C.CONTRACT_NS);
				
				if (restrictionsEl != null) {					
					Element restrictImageEl = restrictionsEl
							.getFirstChildElement("restrictImage", C.CONTRACT_NS);
					if (restrictImageEl != null) {
						ImageRestriction imageRestriction = new ImageRestriction();
						imageRestriction.setWidth(stringValue(restrictImageEl
								.getFirstChildElement("width", C.CONTRACT_NS)));
						imageRestriction.setHeight(stringValue(restrictImageEl
								.getFirstChildElement("height", C.CONTRACT_NS)));
						Element footerTextEl = restrictImageEl
								.getFirstChildElement("footerText", C.CONTRACT_NS);
						if (footerTextEl != null) {
							logger.debug("FooterText: " + stringValue(footerTextEl));
							imageRestriction.setFooterText(stringValue(footerTextEl));
						}
						Element watermarkEl = restrictImageEl
								.getFirstChildElement("watermark", C.CONTRACT_NS);
						if (watermarkEl != null) {
							logger.debug("WatermarkString: " + stringValue(watermarkEl
									.getFirstChildElement("watermarkString", C.CONTRACT_NS)));
						imageRestriction.setWatermarkString(stringValue(watermarkEl
								.getFirstChildElement("watermarkString", C.CONTRACT_NS)));
						imageRestriction.setWatermarkPointSize(stringValue(watermarkEl
								.getFirstChildElement("pointSize", C.CONTRACT_NS)));
						imageRestriction.setWatermarkPosition(stringValue(watermarkEl
								.getFirstChildElement("position", C.CONTRACT_NS)));
						imageRestriction.setWatermarkOpacity(stringValue(watermarkEl
								.getFirstChildElement("opacity", C.CONTRACT_NS)));
						}
						publicationRight.setImageRestriction(imageRestriction);
					}
					
					Element restrictVideoEl = restrictionsEl
							.getFirstChildElement("restrictVideo", C.CONTRACT_NS);
					if (restrictVideoEl != null) {
						VideoRestriction videoRestriction = new VideoRestriction();
						videoRestriction.setWidth(stringValue(restrictVideoEl
								.getFirstChildElement("width", C.CONTRACT_NS)));
						videoRestriction.setHeight(stringValue(restrictVideoEl
								.getFirstChildElement("height", C.CONTRACT_NS)));
						videoRestriction.setDuration(intValue(restrictVideoEl
								.getFirstChildElement("duration", C.CONTRACT_NS)));
						publicationRight.setVideoRestriction(videoRestriction);
					}
					
					Element restrictAudioEl = restrictionsEl
							.getFirstChildElement("restrictAudio", C.CONTRACT_NS);
					if (restrictAudioEl != null) {
						AudioRestriction AudioRestriction = new AudioRestriction();
						AudioRestriction.setDuration(intValue(restrictAudioEl
								.getFirstChildElement("duration", C.CONTRACT_NS)));
						publicationRight.setAudioRestriction(AudioRestriction);
					}
					
					Element restrictTextEl = restrictionsEl
							.getFirstChildElement("restrictText", C.CONTRACT_NS);
					if (restrictTextEl != null) {
						TextRestriction textRestriction = new TextRestriction();
						textRestriction.setPages(intValue(restrictTextEl
								.getFirstChildElement("numberOfPages", C.CONTRACT_NS)));
						Element certainPagesEl = restrictTextEl
								.getFirstChildElement("certainPages", C.CONTRACT_NS);
						if (stringValue(certainPagesEl) != null) {
							String[] certainPagesValues = certainPagesEl.getValue().split("\\s");
							int[] certainPages = new int[certainPagesValues.length];
							for (int j = 0; j < certainPagesValues.length; j++) {
								try {
									certainPages[j] = Integer.parseInt(certainPagesValues[j]);
								} catch (NumberFormatException e) {
									logger.warn("Could not parse integer value in certainPages" +
											" element: {}", certainPagesValues[j], e);
								}
							}
							textRestriction.setCertainPages(certainPages);
						}
						publicationRight.setTextRestriction(textRestriction);
					}
				}
				
				if (right.getPublicationRights() == null)
					right.setPublicationRights(new ArrayList<PublicationRight>());
				right.getPublicationRights().add(publicationRight);
			}
			
			logger.debug("Read rights section");
			
			return right;			
		}
		
		return null;
	}
	
	/**
	 * Builds the agent.
	 *
	 * @param agentEl the agent el
	 * @param object the object
	 */
	private void buildAgent(Element agentEl, Object object) throws NullPointerException {
		Agent agent = new Agent();
		
		String agentId = agentEl.getFirstChildElement("agentIdentifier", PREMIS_NS)
				.getFirstChildElement("agentIdentifierValue", PREMIS_NS)
				.getValue();
		agent.setName(agentId);
		
		String agentType = agentEl.getFirstChildElement("agentType", PREMIS_NS)
				.getValue();
		agent.setType(agentType);
		
		String agentName = null;
		Element agentNameElement = agentEl.getFirstChildElement("agentName", PREMIS_NS);
		if (agentNameElement != null)
			agentName = agentNameElement.getValue();
		agent.setLongName(agentName);
		
		object.getAgents().add(agent);
	}
	
	/**
	 * Builds the object.
	 *
	 * @param objectEl the object el
	 * @return the object
	 */
	private Object buildObject(Element objectEl) throws NullPointerException {
		String objectIdentifier = null;
		String urn = null;		
		
		Elements objectIdentifierEls = objectEl.getChildElements("objectIdentifier", PREMIS_NS);
		for (int i = 0; i < objectIdentifierEls.size(); i++) {
			Element objectIdentifierEl = objectIdentifierEls.get(i);
			
			if (objectIdentifierEl.getFirstChildElement("objectIdentifierType", PREMIS_NS)
					.getValue().equals("OBJECT_BUSINESS_KEY")) {
				objectIdentifier = objectIdentifierEl.getFirstChildElement("objectIdentifierValue", PREMIS_NS)
						.getValue();
			}
			
			else if (objectIdentifierEl.getFirstChildElement("objectIdentifierType", PREMIS_NS)
					.getValue().equals("URN")) {
				urn = objectIdentifierEl.getFirstChildElement("objectIdentifierValue", PREMIS_NS)
						.getValue();
			}			
		}
		
		if (objectIdentifier == null && urn != null)
			objectIdentifier = urn.replace("urn+nbn+de+danrw-", "");
		
		Object object = new Object();
		object.setIdentifier(objectIdentifier);
		object.setUrn(urn);
		
		logger.debug("object identifier: " + objectIdentifier);
		
		return object;
	}
	
	/**
	 * Builds the package.
	 *
	 * @param objectEl the object el
	 * @return the package
	 */
	private Package buildPackage(Element objectEl) throws NullPointerException {
		String packageId = objectEl.getFirstChildElement("objectIdentifier", PREMIS_NS)
									 .getFirstChildElement("objectIdentifierValue", PREMIS_NS)
									 .getValue();
		int index1 = packageId.indexOf('_');
		int index2 = packageId.indexOf(".tar");
		String packageName;
		if (index1 < 0 || index2 < 0)
			packageName = packageId;
		else
			packageName = packageId.substring(index1 + 1, index2);
		
		Package pkg = new Package();
		pkg.setName(packageName);
		
		Element originalNameEl = objectEl.getFirstChildElement("originalName", PREMIS_NS);
		if (originalNameEl != null)
			pkg.setContainerName(originalNameEl.getValue());
		
		logger.debug("Deserialized package: " + pkg);
		
		return pkg;
	}
	
	
	
	/**
	 * Creates the file.
	 *
	 * @param packages the packages
	 * @param objectEl the object el
	 * @return the dA file
	 * @throws ParseException the parse exception
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 */
	private DAFile createFile(List<Package> packages, Element objectEl) throws ParseException, NullPointerException {
		
		String packageId;
		try{
			packageId = objectEl.getFirstChildElement("relationship", PREMIS_NS)
					.getFirstChildElement("relatedObjectIdentification", PREMIS_NS)
					.getFirstChildElement("relatedObjectIdentifierValue", PREMIS_NS).getValue();
		}catch(NullPointerException e){
			throw new ParseException("PREMIS-file seems to be old since there is no relationship information for file "
					+ objectEl.getValue(),0);
		}
		
		int index1 = packageId.indexOf('_');
		int index2 = packageId.indexOf(".tar");
		String packageName = packageId.substring(index1 + 1, index2);
		
		Package pkg = null;
		for (Package p : packages) {
			if (p.getName().equals(packageName))
				pkg = p;
		}
		if (pkg==null){
			throw new ParseException("incosistent PREMIS file: referenced package "+ packageName + " does not exist ", 0);
		}
		
		
		
		
		DAFile f = new DAFile(pkg, "", "");
		String fullPath = objectEl.getFirstChildElement("objectIdentifier", PREMIS_NS)
				.getFirstChildElement("objectIdentifierValue", PREMIS_NS).getValue();
		
		f.setRep_name(fullPath.substring(0, fullPath.indexOf("/")));
		f.setRelative_path(fullPath.substring(fullPath.indexOf("/")+1,fullPath.length()));
		
		
		Element charEl = objectEl.getFirstChildElement("objectCharacteristics", PREMIS_NS);
		f.setChksum(stringValue(charEl.getFirstChildElement("fixity", PREMIS_NS).getFirstChildElement("messageDigest", PREMIS_NS)));
		
		Element sizeEl = charEl.getFirstChildElement("size", PREMIS_NS);
		f.setSize(sizeEl.getValue());
		
		Element formatEl = charEl.getFirstChildElement("format", PREMIS_NS)
									.getFirstChildElement("formatRegistry", PREMIS_NS)
									.getFirstChildElement("formatRegistryKey", PREMIS_NS);
		f.setFormatPUID(formatEl.getValue());
		
//		f.setPathToJhoveOutput(jhoveTempFolder + "/premis_output/" + fullPath.replace('/', '_').replace('.', '_') + ".xml");
		
		pkg.getFiles().add(f);
		
		return f;
	}
	
	private Date readDate(String dateElementValue) throws NullPointerException {
		
		try {
			String dateString = dateElementValue;
			if (dateString.endsWith(":00"))
				dateString = dateString.substring(0, dateString.length() - 3) + "00";
			DATE_FORMAT_WITH_TIMEZONE.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
			return DATE_FORMAT_WITH_TIMEZONE.parse(dateString.replaceAll("Z$", "+0000"));
		} catch (ParseException e1) {
			try {
				String dateString = dateElementValue;
				if (dateString.endsWith(":00"))
					dateString = dateString.substring(0, dateString.length() - 3) + "00";
				DATE_FORMAT_WITH_TIMEZONE_WITHOUT_MILLISECONDS.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
				return DATE_FORMAT_WITH_TIMEZONE_WITHOUT_MILLISECONDS.parse(dateString.replaceAll("Z$", "+0000"));
			} catch (ParseException e2) {
				try {
					DATE_FORMAT_WITHOUT_TIMEZONE.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
					return DATE_FORMAT_WITHOUT_TIMEZONE.parse(dateElementValue);
				} catch (ParseException e3) {
					try {
						DATE_FORMAT_WITHOUT_DATE_AND_TIMEZONE.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
						return DATE_FORMAT_WITHOUT_DATE_AND_TIMEZONE.parse(dateElementValue);
					} catch (ParseException e4) {
						throw new RuntimeException("Could not read date " + dateElementValue, e4);
					}
				}
			}
		}
	}

	/**
	 * Enum value.
	 *
	 * @param <T> the generic type
	 * @param node the node
	 * @param enumType the enum type
	 * @return the t
	 */
	private <T extends Enum<T>> T enumValue(Node node, Class<T> enumType) {
		try {
			if (node != null && !node.getValue().isEmpty())
				return Enum.valueOf(enumType, node.getValue().toUpperCase());
		} catch (IllegalArgumentException e) {
			logger.warn("Not a valid value for enum: {}", node.getValue(), e);
		}
		return null;
	}
	
	/**
	 * Int value.
	 *
	 * @param node the node
	 * @return the integer
	 */
	private Integer intValue(Node node) {
		try {
			if (node != null && !node.getValue().isEmpty())
				return Integer.parseInt(node.getValue());
		} catch (NumberFormatException e) {
			logger.warn("Could not parse integer value: {}", node.getValue(), e);
		}
		return null;
	}
	
	/**
	 * String value.
	 *
	 * @param node the node
	 * @return the string
	 */
	private String stringValue(Node node) {
		if (node != null && !node.getValue().isEmpty())
			return node.getValue();
		return null;
	}
	
}
