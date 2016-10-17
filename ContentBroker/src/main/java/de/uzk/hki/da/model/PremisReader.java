package de.uzk.hki.da.model;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.utils.C;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class PremisReader extends ObjectPremisXmlReader {

	public PremisObject buildPremisObject (File file)throws IOException {

		readFile(file);

		PremisObject object = null;
		
		try {
			Document doc = parser.build(reader);
			Element root = doc.getRootElement();

			// Object & packages
			List<PremisPackage> packages = new ArrayList<PremisPackage>();
			Elements objectElements = root.getChildElements("object", PREMIS_NS);
			for (int i = 0; i < objectElements.size(); i++) {
				String objectIdType = objectElements.get(i).getFirstChildElement("objectIdentifier", PREMIS_NS)
						.getFirstChildElement("objectIdentifierType", PREMIS_NS).getValue();
								
				if (objectIdType.toUpperCase().equals("URN")
						|| objectIdType.toUpperCase().equals("OBJECT_BUSINESS_KEY"))
					object = buildObject(objectElements.get(i));

				else if (objectElements.get(i).getFirstChildElement("objectIdentifier", PREMIS_NS)
						.getFirstChildElement("objectIdentifierType", PREMIS_NS).getValue().toUpperCase().equals("PACKAGE_NAME"))
					packages.add(buildPackage(objectElements.get(i)));
			}

			if (object == null)
				object = new PremisObject();
			if (packages.size() == 0) {
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

		} catch (Exception e) {

		} finally {
			reader.close();
		}
		return object;
	}
	
	
	private PremisObject buildObject(Element objectEl) throws NullPointerException {
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
		
		String origName = objectEl.getFirstChildElement("originalName", PREMIS_NS).getValue();
		
		PremisObject object = new PremisObject();
		object.setIdentifier(objectIdentifier);
		object.setUrn(urn);
		object.setOrig_name(origName);
				
		logger.debug("object identifier: " + objectIdentifier);
		
		return object;
	}
	
	private PremisPackage buildPackage(Element objectEl) throws NullPointerException {
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
		
		PremisPackage pkg = new PremisPackage();
		pkg.setName(packageName);
		pkg.setPkgName(packageId);
		
		Element originalNameEl = objectEl.getFirstChildElement("originalName", PREMIS_NS);
		if (originalNameEl != null)
			pkg.setContainerName(originalNameEl.getValue());
		
		logger.debug("Deserialized package: " + pkg);
		
		return pkg;
	}
	
	private PremisDAFile createFile(List<PremisPackage> packages, Element objectEl) throws ParseException, NullPointerException {
		
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
		
		PremisPackage pkg = null;
		for (PremisPackage p : packages) {
			if (p.getName().equals(packageName))
				pkg = p;
		}
		if (pkg==null){
			throw new ParseException("incosistent PREMIS file: referenced package "+ packageName + " does not exist ", 0);
		}
			
		
		PremisDAFile f = new PremisDAFile("", "");
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
	
	private PremisEvent buildEvent(Element el, PremisObject object) throws NullPointerException {
		if (object.getPackages().isEmpty()) 
			throw new InvalidParameterException("Error: Object is not consistent. Has no package.");
		
		
		PremisEvent event = new PremisEvent();
		
		event.setIdType(enumValue(el.getFirstChildElement("eventIdentifier", PREMIS_NS)
				.getFirstChildElement("eventIdentifierType", PREMIS_NS), PremisEvent.IdType.class));
		
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
			for (PremisPackage pkg : object.getPackages()) {
				for (PremisDAFile f : pkg.getFiles()) {
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
			for (PremisPackage pkg : object.getPackages()) {
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
	
	
	private void buildAgent(Element agentEl, PremisObject object) throws NullPointerException {
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
		
		//object.getAgents().add(agent);
	}
	
	
	
	private String stringValue(Node node) {
		if (node != null && !node.getValue().isEmpty())
			return node.getValue();
		return null;
	}
	
	private PremisEvent buildEvent(Element el) {
		PremisEvent pevent = new PremisEvent();

		//pevent.setObjectIdentifier(objectIdentifier);

		pevent.setIdType(enumValue(
				el.getFirstChildElement("eventIdentifier", PREMIS_NS)
						.getFirstChildElement("eventIdentifierType", PREMIS_NS),
				PremisEvent.IdType.class));

		pevent.setIdentifier(el
				.getFirstChildElement("eventIdentifier", PREMIS_NS)
				.getFirstChildElement("eventIdentifierValue", PREMIS_NS)
				.getValue());

		String eventType = el.getFirstChildElement("eventType", PREMIS_NS)
				.getValue();
		pevent.setType(eventType);

		pevent.setDate(readDate(el.getFirstChildElement("eventDateTime",
				PREMIS_NS).getValue()));

		if (eventType.equals("CONVERT"))
			pevent.setDetail(el.getFirstChildElement("eventDetail", PREMIS_NS)
					.getValue());

		pevent.setAgent_type(Agent.getTypeForIdType(el
				.getFirstChildElement("linkingAgentIdentifier", PREMIS_NS)
				.getFirstChildElement("linkingAgentIdentifierType", PREMIS_NS)
				.getValue()));

		pevent.setAgent_name(el
				.getFirstChildElement("linkingAgentIdentifier", PREMIS_NS)
				.getFirstChildElement("linkingAgentIdentifierValue", PREMIS_NS)
				.getValue());

		Elements linkingObjectIdentifiers = el.getChildElements(
				"linkingObjectIdentifier", PREMIS_NS);
		String sourceFile = "";
		String outcomeFile = "";
		String nonConvertEventIdentifier = "";

		for (int i = 0; i < linkingObjectIdentifiers.size(); i++) {
			Element linkingObjectIdentifier = linkingObjectIdentifiers.get(i);

			String linkingObjectIdentifierValue = linkingObjectIdentifier
					.getFirstChildElement("linkingObjectIdentifierValue",
							PREMIS_NS).getValue();

			Element linkingObjectRole = linkingObjectIdentifier
					.getFirstChildElement("linkingObjectRole", PREMIS_NS);
			if (linkingObjectRole != null) {
				if (linkingObjectRole.getValue().toLowerCase().equals("source")) {
					sourceFile = linkingObjectIdentifierValue;
				} else if (linkingObjectRole.getValue().toLowerCase()
						.equals("outcome"))
					outcomeFile = linkingObjectIdentifierValue;
				else
					nonConvertEventIdentifier = linkingObjectIdentifierValue;
			} else
				nonConvertEventIdentifier = linkingObjectIdentifierValue;
		}

		String[] s = sourceFile.split("/");
		PremisDAFile source = null;
		if (s.length >= 2)
			source = new PremisDAFile(s[0], s[1]);
		String[] t = outcomeFile.split("/");
		PremisDAFile target = null;
		if (t.length >= 2)
			target = new PremisDAFile(t[0], t[1]);
		if (source != null)
			pevent.setSource_file(source);
		if (target != null)
			pevent.setTarget_file(target);

		return pevent;
	}

}
