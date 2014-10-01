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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Contains the publication rights for one audience
 * 
 * @author Thomas Kleinke
 */
public class PublicationRights {

	// General rights
	private boolean allowPublication, tempPublication, lawPublication;
	private boolean textRestriction, imageRestriction, audioRestriction, videoRestriction, videoDurationRestriction;
	private boolean imageRestrictionText;
	private Date startDate;
	private Law law;
	
	// Restrictions
	private String pages;
	private String imageWidth, imageHeight;
	private String footerText, watermarkOpacity, watermarkSize;
	private TextType imageTextType;
	private String audioDuration;
	private String videoSize, videoDuration;
	
	public enum Law { EPFLICHT, URHG_DE, PUBLICDOMAIN_DE};
	public enum TextType { footer, north, south, center };
		
	public boolean getAllowPublication() {
		return allowPublication;
	}

	public void setAllowPublication(boolean allowPublication) {
		this.allowPublication = allowPublication;
	}

	public boolean getTempPublication() {
		return tempPublication;
	}

	public void setTempPublication(boolean tempPublication) {
		this.tempPublication = tempPublication;
	}
	
	public boolean getLawPublication() {
		return lawPublication;
	}

	public void setLawPublication(boolean lawPublication) {
		this.lawPublication = lawPublication;
	}

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Takes a start date as a string and saves it as a date object
	 * 
	 * @param startDate The start date as a text string
	 * @return true if the date could be successfully parsed, false otherwise
	 */
	public boolean setStartDate(String startDate) {
		
		Date date = checkDate(startDate);
		
		if (date != null)
		{
			this.startDate = date;
			return true;
		}
		else
			return false;
	}

	public Law getLaw() {
		return law;
	}

	public void setLaw(Law law) {
		this.law = law;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getFooterText() {
		return footerText;
	}

	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	public String getAudioDuration() {
		return audioDuration;
	}

	public void setAudioDuration(String audioDuration) {
		this.audioDuration = audioDuration;
	}

	public String getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}

	public String getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(String videoDuration) {
		this.videoDuration = videoDuration;
	}

	public String getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}
	
	public boolean getTextRestriction() {
		return textRestriction;
	}

	public void setTextRestriction(boolean textRestriction) {
		this.textRestriction = textRestriction;
	}

	public boolean getImageRestriction() {
		return imageRestriction;
	}

	public void setImageRestriction(boolean imageRestriction) {
		this.imageRestriction = imageRestriction;
	}

	public boolean getAudioRestriction() {
		return audioRestriction;
	}

	public void setAudioRestriction(boolean audioRestriction) {
		this.audioRestriction = audioRestriction;
	}

	public boolean getVideoRestriction() {
		return videoRestriction;
	}

	public void setVideoRestriction(boolean videoRestriction) {
		this.videoRestriction = videoRestriction;
	}

	public boolean getVideoDurationRestriction() {
		return videoDurationRestriction;
	}

	public void setVideoDurationRestriction(boolean videoDurationRestriction) {
		this.videoDurationRestriction = videoDurationRestriction;
	}

	public String getWatermarkOpacity() {
		return watermarkOpacity;
	}

	public void setWatermarkOpacity(String watermarkOpacity) {
		this.watermarkOpacity = watermarkOpacity;
	}

	public String getWatermarkSize() {
		return watermarkSize;
	}

	public void setWatermarkSize(String watermarkSize) {
		this.watermarkSize = watermarkSize;
	}

	public TextType getImageTextType() {
		return imageTextType;
	}

	public void setImageTextType(TextType imageTextType) {
		this.imageTextType = imageTextType;
	}

	public boolean getImageRestrictionText() {
		return imageRestrictionText;
	}

	public void setImageRestrictionText(boolean imageRestrictionText) {
		this.imageRestrictionText = imageRestrictionText;
	}
	
	/**
	 * Turns the pages settings chosen by the user into a list of pages (as a single string)
	 * 
	 * Example:
	 * pages settings: "1, 6-10, 200"
	 * result: "1 6 7 8 9 10 200"
	 * 
	 * @return List of pages as a single string
	 */
	public String parsePages() {
		
		if (pages.contains("$"))
			return "";
		
		String tempPages = pages + "$";
		
		int number1 = -1;
		int number2 = -1;
		String numberText = "";
		boolean range = false;
		boolean space = false;
		
		Set<Integer> pagesSet = new HashSet<Integer>();
		
		char[] charPages = tempPages.toCharArray();
		
		for(char c : charPages) {
			switch (c) {
			case ',':
			case ';':
			case '$':
				if (numberText.equals(""))
					break;
				
				try {
				if (!range)
					number1 = Integer.parseInt(numberText);
				else
					number2 = Integer.parseInt(numberText);
				} catch (NumberFormatException e) {
					return "";
				}
				numberText = "";
				
				range = false;
				space = false;
				
				if (number1 != -1) {
					if (number2 != -1) {
						if (!addPagesToList(number1, number2, pagesSet))
							return "";
						number1 = -1;
						number2 = -1;
						break;
					}
					 
					if (!pagesSet.contains(number1))
						pagesSet.add(number1);
					number1 = -1;
				}
				break;
				
			case '-':
				if (range || numberText.equals(""))
					return "";
					
				try {
				number1 = Integer.parseInt(numberText);
				} catch (NumberFormatException e) {
					return "";
				}
				numberText = "";
				
				range = true;
				space = false;
				break;
				
			case ' ':
				if (!numberText.equals(""))
					space = true;
				break;
				
			default:
				if (space)
					return "";
				
				if (c >= '0' && c <= '9') {
					numberText += c;
				}
				else
					return "";
				break;
			}
		}
		
		List<Integer> pagesList = new ArrayList<Integer>(pagesSet);
		Collections.sort(pagesList);
		
		StringBuilder builder = new StringBuilder();
		
		Iterator<Integer> iterator = pagesList.iterator();
		while(iterator.hasNext())
		{
			if (builder.length() != 0)
				builder.append(" ");
				
			builder.append(iterator.next());			
		}
		
		return builder.toString();
	}
	
	/**
	 * Adds the pages between startPage and endPage to the given list of pages
	 * 
	 * @param startPage The first page to add
	 * @param endPage The last page to add
	 * @param pagesList The page list
	 * @return true if the pages could be added, otherwise false
	 */
	private boolean addPagesToList(int startPage, int endPage, Set<Integer> pagesList) {
		
		if (startPage > endPage || (endPage - startPage) > 1000000)
			return false;
		
		for (int i = startPage; i <= endPage; i++) {			
			if (!pagesList.contains(i))
				pagesList.add(i);
		}
		
		return true;
	}
	
	/**
	 * Checks if the given date string is a valid date
	 * 
	 * @param dateString The date as a text string
	 * @return The date as a date object if the date string is a valid date, otherwise null
	 */
	private Date checkDate(String dateString) {
		
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd.MM.yy");
	
		Date date;
		try {
			date = dateFormat1.parse(dateString);
		} catch (ParseException e1) {
			try {
				date = dateFormat2.parse(dateString);
			} catch (ParseException e2) {
				try {
					date = dateFormat3.parse(dateString);
				} catch (ParseException e3) {
					return null;
				}
			}
		}
		
		return date;
	}
}
