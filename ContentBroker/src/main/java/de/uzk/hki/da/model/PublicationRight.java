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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;



/**
 * The Class PublicationRight.
 */
public class PublicationRight {
	
	/**
	 * The Enum Audience.
	 */
	public static enum Audience {
		
		/** The public. */
		PUBLIC, 
 /** The institution. */
 INSTITUTION
	}
	
	/** The audience. */
	private Audience audience;
	
	/** The start date. */
	private Date startDate;
	
	/** The law id. */
	private String lawID;
	
	/** The image restriction. */
	private ImageRestriction imageRestriction;
	
	/** The video restriction. */
	private VideoRestriction videoRestriction;
	
	/** The audio restriction. */
	private AudioRestriction audioRestriction;
	
	/** The text restriction. */
	private TextRestriction textRestriction;
	
	/**
	 * Gets the audience.
	 *
	 * @return the audience
	 */
	public Audience getAudience() {
		return audience;
	}
	
	/**
	 * Sets the audience.
	 *
	 * @param audience the new audience
	 */
	public void setAudience(Audience audience) {
		this.audience = audience;
	}


	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	@XmlSchemaType(name="date")
	@XmlJavaTypeAdapter(CustomDateAdapter.class)
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the image restriction.
	 *
	 * @return the image restriction
	 */
	@XmlElement(name="restrictImage")
	public ImageRestriction getImageRestriction() {
		return imageRestriction;
	}
	
	/**
	 * Sets the image restriction.
	 *
	 * @param imageRestriction the new image restriction
	 */
	public void setImageRestriction(ImageRestriction imageRestriction) {
		this.imageRestriction = imageRestriction;
	}
	
	/**
	 * Gets the video restriction.
	 *
	 * @return the video restriction
	 */
	@XmlElement(name="restrictVideo")
	public VideoRestriction getVideoRestriction() {
		return videoRestriction;
	}
	
	/**
	 * Sets the video restriction.
	 *
	 * @param videoRestriction the new video restriction
	 */
	public void setVideoRestriction(VideoRestriction videoRestriction) {
		this.videoRestriction = videoRestriction;
	}
	
	/**
	 * Gets the audio restriction.
	 *
	 * @return the audio restriction
	 */
	@XmlElement(name="restrictAudio")
	public AudioRestriction getAudioRestriction() {
		return audioRestriction;
	}
	
	/**
	 * Sets the audio restriction.
	 *
	 * @param audioRestriction the new audio restriction
	 */
	public void setAudioRestriction(AudioRestriction audioRestriction) {
		this.audioRestriction = audioRestriction;
	}
	
	/**
	 * Gets the text restriction.
	 *
	 * @return the text restriction
	 */
	@XmlElement(name="restrictText")
	public TextRestriction getTextRestriction() {
		return textRestriction;
	}
		
	/**
	 * Gets the law id.
	 *
	 * @return the law id
	 */
	public String getLawID() {
		return lawID;
	}

	/**
	 * Sets the law id.
	 *
	 * @param lawID the new law id
	 */
	public void setLawID(String lawID) {
		this.lawID = lawID;
	}
	
	/**
	 * Sets the text restriction.
	 *
	 * @param textRestriction the new text restriction
	 */
	public void setTextRestriction(TextRestriction textRestriction) {
		this.textRestriction = textRestriction;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "PublicationRight[imageRestriction: "+imageRestriction.getWidth()+" "+imageRestriction.getHeight()+"]";
	}



	/**
	 * The Class CustomDateAdapter.
	 */
	public static class CustomDateAdapter extends XmlAdapter<String, Date> {

		/** The format. */
		public static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
		
		/* (non-Javadoc)
		 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
		 */
		@Override
		public String marshal(Date v) throws Exception {			
			return FORMAT.format(v);
		}

		/* (non-Javadoc)
		 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
		 */
		@Override
		public Date unmarshal(String v) throws Exception {
			return FORMAT.parse(v);
		}
		
	}

}
