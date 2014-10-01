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
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Contains the selectable values for certain restriction settings shown in the drop down boxes in GUI mode
 * 
 * @author Thomas Kleinke
 */
public class ContractSettings {

	private String[] duration = new String[4];
	private String[] widthImage = new String[3];
	private String[] heightImage = new String[3];
	private String[] percentImage = new String[3];
	private String[] opacityImage = new String[5];
	private String[] textSizeImage = new String[4];
	private String[] heightVideo = new String[3];
	
	public ContractSettings(String confFolderPath) throws Exception {
		
		File settingsFile = new File(confFolderPath + File.separator + "settings.xml");
		
		if (settingsFile.exists())
			loadContractSettingsFromFile(settingsFile);
		else
			setStandardSettings();
	}
	
	/**
	 * Loads the settings from the default settings XML file
	 * 
	 * @param settingsFile The XML file
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws NullPointerException
	 * @throws SAXException
	 */
	private void loadContractSettingsFromFile(File settingsFile) throws IOException, ValidityException,
		ParsingException, NullPointerException, SAXException {
		
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
				throw new SAXException("Error while parsing settings file", e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw new SAXException("Fatal error while parsing settings file", e);
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				System.out.println("Warning while parsing settings file:\n" + e.getMessage());
			}
		});

		FileReader reader = new FileReader(settingsFile);
		Builder parser = new Builder(xmlReader);
		Document doc = parser.build(reader);
		reader.close();
		Element root = doc.getRootElement();
		
		Element durationEl = root.getFirstChildElement("duration");
		duration[0] = durationEl.getFirstChildElement("firstOption").getValue();
		duration[1] = durationEl.getFirstChildElement("secondOption").getValue();
		duration[2] = durationEl.getFirstChildElement("thirdOption").getValue();
		duration[3] = durationEl.getFirstChildElement("fourthOption").getValue();
		
		Element imageEl = root.getFirstChildElement("image");
		
		Element widthImageEl = imageEl.getFirstChildElement("width");
		widthImage[0] = widthImageEl.getFirstChildElement("firstOption").getValue();
		widthImage[1] = widthImageEl.getFirstChildElement("secondOption").getValue();
		widthImage[2] = widthImageEl.getFirstChildElement("thirdOption").getValue();
		
		Element heightImageEl = imageEl.getFirstChildElement("height");
		heightImage[0] = heightImageEl.getFirstChildElement("firstOption").getValue();
		heightImage[1] = heightImageEl.getFirstChildElement("secondOption").getValue();
		heightImage[2] = heightImageEl.getFirstChildElement("thirdOption").getValue();
		
		Element percentImageEl = imageEl.getFirstChildElement("percent");
		percentImage[0] = percentImageEl.getFirstChildElement("firstOption").getValue();
		percentImage[1] = percentImageEl.getFirstChildElement("secondOption").getValue();
		percentImage[2] = percentImageEl.getFirstChildElement("thirdOption").getValue();
		
		Element opacityImageEl = imageEl.getFirstChildElement("opacity");
		opacityImage[0] = opacityImageEl.getFirstChildElement("firstOption").getValue();
		opacityImage[1] = opacityImageEl.getFirstChildElement("secondOption").getValue();
		opacityImage[2] = opacityImageEl.getFirstChildElement("thirdOption").getValue();
		opacityImage[3] = opacityImageEl.getFirstChildElement("fourthOption").getValue();
		opacityImage[4] = opacityImageEl.getFirstChildElement("fifthOption").getValue();
		
		Element textSizeImageEl = imageEl.getFirstChildElement("textSize");
		textSizeImage[0] = textSizeImageEl.getFirstChildElement("firstOption").getValue();
		textSizeImage[1] = textSizeImageEl.getFirstChildElement("secondOption").getValue();
		textSizeImage[2] = textSizeImageEl.getFirstChildElement("thirdOption").getValue();
		textSizeImage[3] = textSizeImageEl.getFirstChildElement("fourthOption").getValue();
		
		Element videoEl = root.getFirstChildElement("video");
		
		Element heightVideoEl = videoEl.getFirstChildElement("height");
		heightVideo[0] = heightVideoEl.getFirstChildElement("firstOption").getValue();
		heightVideo[1] = heightVideoEl.getFirstChildElement("secondOption").getValue();
		heightVideo[2] = heightVideoEl.getFirstChildElement("thirdOption").getValue();
	}
	
	/**
	 * Sets some default values (just needed if the settings.xml is not found)
	 */
	private void setStandardSettings() {
	
		duration[0] = "5";
		duration[1] = "15";
		duration[2] = "60";
		duration[3] = "120";
		
		widthImage[0] = "480";
		widthImage[1] = "800";
		widthImage[2] = "1280";
		
		heightImage[0] = "360";
		heightImage[1] = "600";
		heightImage[2] = "960";
		
		percentImage[0] = "12.5%";
		percentImage[1] = "25%";
		percentImage[2] = "50%";
		
		opacityImage[0] = "5";
		opacityImage[1] = "10";
		opacityImage[2] = "25";
		opacityImage[3] = "50";
		opacityImage[4] = "100";
		
		textSizeImage[0] = "10";
		textSizeImage[1] = "20";
		textSizeImage[2] = "40";
		textSizeImage[3] = "60";
		
		heightVideo[0] = "360";
		heightVideo[1] = "720";
		heightVideo[2] = "1080";
	}

	/**
	 * Returns the duration value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The duration value as a text string
	 */
	public String getDuration(int index) {
		return duration[index];
	}

	/**
	 * Returns the image width value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The image width value as a text string
	 */
	public String getWidthImage(int index) {
		
		if (index <= 2)		
			return widthImage[index];
		else
			return percentImage[index - 3];
	}

	/**
	 * Returns the image height value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The image height value as a text string
	 */
	public String getHeightImage(int index) {
		
		if (index <= 2)		
			return heightImage[index];
		else
			return percentImage[index - 3];		
	}

	/**
	 * Returns the image percent value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The image percent value as a text string
	 */
	public String getPercentImage(int index) {
		return percentImage[index];
	}
	
	/**
	 * Returns the watermark opacity value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The watermark opacity value as a text string
	 */
	public String getOpacityImage(int index) {
		return opacityImage[index];
	}
	
	/**
	 * Returns the watermark text size value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The watermark text size value as a text string
	 */
	public String getTextSizeImage(int index) {
		return textSizeImage[index];
	}

	/**
	 * Returns the video height value for a certain index
	 * 
	 * @param index The drop down menu index
	 * @return The video height value as a text string
	 */
	public String getHeightVideo(int index) {
		return heightVideo[index];
	}
	
	/**
	 * Returns the drop down menu index for a certain duration value
	 * 
	 * @param value The duration value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getDurationIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < duration.length; i++)
		{
			if (value.equals(duration[i]))
				return i;
		}
		
		return -1;		
	}
	
	/**
	 * Returns the drop down menu index for a certain image width value
	 * 
	 * @param value The image width value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getWidthImageIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < widthImage.length; i++)
		{
			if (value.equals(widthImage[i]))
				return i;
		}
		
		for (int i = 0; i < percentImage.length; i++)
		{
			if (value.equals(percentImage[i]))
				return i + widthImage.length;
		}
		
		return -1;		
	}
	
	/**
	 * Returns the drop down menu index for a certain image height value
	 * 
	 * @param value The image height value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getHeightImageIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < heightImage.length; i++)
		{
			if (value.equals(heightImage[i]))
				return i;
		}
		
		for (int i = 0; i < percentImage.length; i++)
		{
			if (value.equals(percentImage[i]))
				return i + heightImage.length;
		}
		
		return -1;		
	}
	
	/**
	 * Returns the drop down menu index for a certain watermark opacity value
	 * 
	 * @param value The watermark opacity value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getOpacityImageIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < opacityImage.length; i++)
		{
			if (value.equals(opacityImage[i]))
				return i;
		}
		
		return -1;		
	}
	
	/**
	 * Returns the drop down menu index for a certain watermark text string value
	 * 
	 * @param value The watermark text string value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getTextSizeImageIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < textSizeImage.length; i++)
		{
			if (value.equals(textSizeImage[i]))
				return i;
		}
		
		return -1;		
	}
	
	/**
	 * Returns the drop down menu index for a certain video height value
	 * 
	 * @param value The video height value as a text string
	 * @return The drop down menu index corresponding to the given text string
	 */
	public int getHeightVideoIndex(String value) {
		if (value == null)
			return -1;
		
		for (int i = 0; i < heightVideo.length; i++)
		{
			if (value.equals(heightVideo[i]))
				return i;
		}
		
		return -1;		
	}
}
