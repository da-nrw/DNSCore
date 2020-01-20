/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.utils.Utilities;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;



/**
 * The contract rights settings chosen by the user
 * 
 * @author Thomas Kleinke
 */
public class ContractRights {
	
	private Logger logger = LogManager.getLogger();
	
	private PublicationRights institutionRights = new PublicationRights();
	private PublicationRights publicRights = new PublicationRights();
	
	private ConversionCondition conversionCondition;
	boolean ddbExclusion;
	private CCLicense cclincense;
	private int minimalIngestQuality;


	public enum ConversionCondition { NONE, NOTIFY, CONFIRM };
/*
	public enum LicenseCondition {
		//NOLICENSE("NOLICENSE"), 
		METADALICENSE("METADALICENSE"), 
		PREMISLICENSE("PREMISLICENSE");

		private LicenseCondition(String text) {	this.text = text;}
		private String text;
		public String getText() {return text;}
		@Override public String toString() {	return text;}
	};*/

	
	public enum CCLicense {

		DlDeBy2("http://www.govdata.de/dl-de/by-2-0","DL-DE-BY-Lizenz (v2.0)","Datenlizenz Deutschland – Namensnennung (v2.0)"),
		DlDeZero("http://www.govdata.de/dl-de/zero-2-0","DL-DE-Zero-Lizenz (v2.0)","Datenlizenz Deutschland – Zero (v2.0)"),
		DlDeBy1("http://www.govdata.de/dl-de/by-1-0","DL-DE-BY-Lizenz (v1.0)","Datenlizenz Deutschland – Namensnennung (v1.0)"),
		DlDeByNc("http://www.govdata.de/dl-de/by-1-0","DL-DE-BY-NC-Lizenz (v1.0)","Datenlizenz Deutschland – Namensnennung – nicht kommerziell (v1.0)"),
		
		PDM("https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","Public Domain Mark 1.0"),
		Cc0("https://creativecommons.org/publicdomain/zero/1.0/","CC0-Lizenz (v1.0)","CC0 1.0 Public Domain Dedication"),
		/*
		By3("https://creativecommons.org/licenses/by/3.0/","CC-BY-Lizenz (v3.0)","cc-by_3.0"),
		ByNd3("https://creativecommons.org/licenses/by-nd/3.0/","CC-BY-ND-Lizenz (v3.0)","cc-by-nd_3.0"),
		ByNc3("https://creativecommons.org/licenses/by-nc/3.0/","CC-BY-NC-Lizenz (v3.0)","cc-by-nc_3.0"),
		ByNcNd3("https://creativecommons.org/licenses/by-nc-nd/3.0/","CC-BY-NC-ND-Lizenz (v3.0)","cc-by-nc-nd_3.0"),
		ByNcSa3("https://creativecommons.org/licenses/by-nc-sa/3.0/","CC-BY-NC-SA-Lizenz (v3.0)","cc-by-nc-sa_3.0"),
		BySa3("https://creativecommons.org/licenses/by-sa/3.0/","CC-BY-SA-Lizenz (v3.0)","cc-by-sa_3.0"),
		
		By4("https://creativecommons.org/licenses/by/4.0/","CC-BY-Lizenz (v4.0)","cc-by_4.0"),
		ByNd4("https://creativecommons.org/licenses/by-nd/4.0/","CC-BY-ND-Lizenz (v4.0)","cc-by-nd_4.0"),
		ByNc4("https://creativecommons.org/licenses/by-nc/4.0/","CC-BY-NC-Lizenz (v4.0)","cc-by-nc_4.0"),
		ByNcNd4("https://creativecommons.org/licenses/by-nc-nd/4.0/","CC-BY-NC-ND-Lizenz (v4.0)","cc-by-nc-nd_4.0"),
		ByNcSa4("https://creativecommons.org/licenses/by-nc-sa/4.0/","CC-BY-NC-SA-Lizenz (v4.0)","cc-by-nc-sa_4.0"),
		BySa4("https://creativecommons.org/licenses/by-sa/4.0/","CC-BY-SA-Lizenz (v4.0)","cc-by-sa_4.0"),
		*/
		
		By3("https://creativecommons.org/licenses/by/3.0/","CC-BY-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung"),
		ByNd3("https://creativecommons.org/licenses/by-nd/3.0/","CC-BY-ND-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung - Keine Bearbeitungen"),
		ByNc3("https://creativecommons.org/licenses/by-nc/3.0/","CC-BY-NC-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung - Nicht kommerziell"),
		ByNcNd3("https://creativecommons.org/licenses/by-nc-nd/3.0/","CC-BY-NC-ND-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung - Nicht kommerziell - Keine Bearbeitungen"),
		ByNcSa3("https://creativecommons.org/licenses/by-nc-sa/3.0/","CC-BY-NC-SA-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung - Nicht-kommerziell - Weitergabe unter gleichen Bedingungen"),
		BySa3("https://creativecommons.org/licenses/by-sa/3.0/","CC-BY-SA-Lizenz (v3.0)","CC v3.0 International Lizenz: Namensnennung - Weitergabe unter gleichen Bedingungen"),
		
		
		By4("https://creativecommons.org/licenses/by/4.0/","CC-BY-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung"),
		ByNd4("https://creativecommons.org/licenses/by-nd/4.0/","CC-BY-ND-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung - Keine Bearbeitungen"),
		ByNc4("https://creativecommons.org/licenses/by-nc/4.0/","CC-BY-NC-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung - Nicht kommerziell"),
		ByNcNd4("https://creativecommons.org/licenses/by-nc-nd/4.0/","CC-BY-NC-ND-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung - Nicht kommerziell - Keine Bearbeitungen"),
		ByNcSa4("https://creativecommons.org/licenses/by-nc-sa/4.0/","CC-BY-NC-SA-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung - Nicht-kommerziell - Weitergabe unter gleichen Bedingungen"),
		BySa4("https://creativecommons.org/licenses/by-sa/4.0/","CC-BY-SA-Lizenz (v4.0)","CC v4.0 International Lizenz: Namensnennung - Weitergabe unter gleichen Bedingungen"),
		;
		

		private CCLicense(String href, String displayLabel, String defaultText) {
			this.displayLabel = displayLabel;
			this.href = href;
			this.text = defaultText;
		}

		private String displayLabel;
		private String href;
		private String text;
		public String getDisplayLabel() {return displayLabel;	}
		public String getHref() {return href;	}
		public String getText() {return text;	}
		@Override
		public String toString() {
			return displayLabel;
		}
	};
	

	/**
	 * Restores the contract rights settings from a previously created XML file
	 * 
	 * @param contractRightsFile The contract rights XML file
	 * @throws Exception
	 */
	public void loadContractRightsFromFile(File contractRightsFile) throws Exception {
		
		institutionRights = new PublicationRights();
		publicRights = new PublicationRights();

		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			throw new IOException("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(new ErrorHandler(){

			@Override
			public void error(SAXParseException e) throws SAXException {
				throw new SAXException("Error while parsing contract rights file", e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw new SAXException("Fatal error while parsing contract rights file", e);
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				logger.warn("Warning while parsing contract rights file:\n" + e.getMessage());
			}
		});

		InputStream inputStream = new FileInputStream(contractRightsFile);
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		Builder parser = new Builder(xmlReader);
		Document doc = parser.build(reader);
		reader.close();
		
		Element root = doc.getRootElement();
		
		Element institutionRightsEl = root.getFirstChildElement("institutionRights");
		readPublicationRights(institutionRights, institutionRightsEl);
		
		Element publicRightsEl = root.getFirstChildElement("publicRights");
		readPublicationRights(publicRights, publicRightsEl);
		
		Element conversionRightsEl = root.getFirstChildElement("conversionRights");
		conversionCondition = ConversionCondition.valueOf(conversionRightsEl.getFirstChildElement("condition").getValue());
		
		Element ddbExclusionEl = root.getFirstChildElement("ddbExclusion");
		if (ddbExclusionEl != null)
			ddbExclusion = Boolean.parseBoolean(ddbExclusionEl.getValue());
		else
			ddbExclusion = false;
		
		Element minimalIngestQualityEl = root.getFirstChildElement("minimalIngestQualityLevel");
		if (minimalIngestQualityEl != null)
			minimalIngestQuality = Integer.parseInt(minimalIngestQualityEl.getValue());
		else
			minimalIngestQuality = 0;
		
		Element publicationLicenseEl = root.getFirstChildElement("publicationLicense");
		if(publicationLicenseEl!=null){
			String hrefAttribute=publicationLicenseEl.getAttribute("href").getValue();
			//publicationLicenseEl.getAttribute("displayLabel");
			//publicationLicenseEl.getValue();
			for(CCLicense cl:CCLicense.values())
				if(cl.getHref().equals(hrefAttribute)){
					cclincense = cl;
					break;
				}
			if(cclincense==null){
				throw new RuntimeException("Reading ContractRights from file failed, unknown license href: "+hrefAttribute);
			}
		}else{
			cclincense=null;
		}
		
		
	}
	
	private void readPublicationRights(PublicationRights pubRights, Element pubRightEl) throws Exception {
		
		pubRights.setAllowPublication(
				Boolean.parseBoolean(pubRightEl.getFirstChildElement("allowed").getValue()));
		pubRights.setTempPublication(
				Boolean.parseBoolean(pubRightEl.getFirstChildElement("tempPublication").getValue()));
		pubRights.setLawPublication(
				Boolean.parseBoolean(pubRightEl.getFirstChildElement("lawPublication").getValue()));
		
		Element startDateEl = pubRightEl.getFirstChildElement("startDate");
		if (startDateEl != null)
			pubRights.setStartDate(startDateEl.getValue());
		
		Element restrictionLawEl = pubRightEl.getFirstChildElement("law");
		if (restrictionLawEl != null)
			pubRights.setLaw(PublicationRights.Law.valueOf(restrictionLawEl.getValue()));
		
		Element restrictionsEl = pubRightEl.getFirstChildElement("restrictions");
		
		if (restrictionsEl != null) {
			
			pubRights.setTextRestriction(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("textRestriction").getValue()));
			pubRights.setImageRestriction(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("imageRestriction").getValue()));
			pubRights.setImageRestrictionText(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("imageRestrictionText").getValue()));
			pubRights.setAudioRestriction(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("audioRestriction").getValue()));
			pubRights.setVideoRestriction(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("videoRestriction").getValue()));
			pubRights.setVideoDurationRestriction(Boolean.parseBoolean(restrictionsEl.getFirstChildElement("videoDurationRestriction").getValue()));

			Element numPagesEl = restrictionsEl.getFirstChildElement("pages");
			if (numPagesEl != null)
				pubRights.setPages(numPagesEl.getValue());
			
			Element imageWidthEl = restrictionsEl.getFirstChildElement("imageWidth");
			if (imageWidthEl != null)
				pubRights.setImageWidth(imageWidthEl.getValue());

			Element imageHeightEl = restrictionsEl.getFirstChildElement("imageHeight");
			if (imageHeightEl != null)
				pubRights.setImageHeight(imageHeightEl.getValue());

			Element footerTextEl = restrictionsEl.getFirstChildElement("footerText");
			if (footerTextEl != null)
				pubRights.setFooterText(footerTextEl.getValue());
			
			Element imageTextTypeEl = restrictionsEl.getFirstChildElement("imageTextType");
			if (imageTextTypeEl != null)
				pubRights.setImageTextType(PublicationRights.TextType.valueOf(imageTextTypeEl.getValue()));
			
			Element watermarkOpacityEl = restrictionsEl.getFirstChildElement("watermarkOpacity");
			if (watermarkOpacityEl != null)
				pubRights.setWatermarkOpacity(watermarkOpacityEl.getValue());
			
			Element watermarkSizeEl = restrictionsEl.getFirstChildElement("watermarkSize");
			if (watermarkSizeEl != null)
				pubRights.setWatermarkSize(watermarkSizeEl.getValue());
			
			Element audioDurationEl = restrictionsEl.getFirstChildElement("audioDuration");
			if (audioDurationEl != null)
				pubRights.setAudioDuration(audioDurationEl.getValue());

			Element videoSizeEl = restrictionsEl.getFirstChildElement("videoSize");
			if (videoSizeEl != null)
				pubRights.setVideoSize(videoSizeEl.getValue());

			Element videoDurationEl = restrictionsEl.getFirstChildElement("videoDuration");
			if (videoDurationEl != null)
				pubRights.setVideoDuration(videoDurationEl.getValue());
		}
	}
	
	public PublicationRights getInstitutionRights() {
		return institutionRights;
	}

	public PublicationRights getPublicRights() {
		return publicRights;
	}

	public ConversionCondition getConversionCondition() {
		return conversionCondition;
	}

	public void setConversionCondition(String conversionCondition) {
		this.conversionCondition = Utilities.translateConversionCondition(conversionCondition);
	}

	public boolean getDdbExclusion() {
		return ddbExclusion;
	}

	public void setDdbExclusion(boolean ddbExclusion) {
		this.ddbExclusion = ddbExclusion;
	}
	
	public CCLicense getCclincense() {
		return cclincense;
	}

	public void setCclincense(CCLicense cclincense) {
		this.cclincense = cclincense;
	}

	public int getMinimalIngestQuality() {
		return minimalIngestQuality;
	}

	public void setMinimalIngestQuality(int minimalIngestQuality) {
		this.minimalIngestQuality = minimalIngestQuality;
	}
	
	
	
}
