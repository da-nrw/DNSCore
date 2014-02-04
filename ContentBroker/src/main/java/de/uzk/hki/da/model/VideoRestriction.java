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


/**
 * The Class VideoRestriction.
 */
public class VideoRestriction {
	
	/** The width. */
	private String width;
	
	/** The height. */
	private String height;
	
	/** The compression rate. */
	private String compressionRate;
	
	/** The duration. */
	private Integer duration;
	
	/**
	 * Instantiates a new video restriction.
	 */
	public VideoRestriction() {
		width = null;
		height = null;
		duration = null;
		compressionRate = null;
	}
	
	/**
	 * Instantiates a new video restriction.
	 *
	 * @param width the width
	 * @param height the height
	 * @param compressionRate the compression rate
	 * @param duration the duration
	 */
	public VideoRestriction(String width, String height, String compressionRate, Integer duration) {
		this.width = width;
		this.height = height;
		this.compressionRate = compressionRate;
		this.duration = duration;
	}
	
	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}
	
	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(String width) {
		this.width = width;
	}
	
	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}
	
	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(String height) {
		this.height = height;
	}
	
	/**
	 * Gets the compression rate.
	 *
	 * @return the compression rate
	 */
	public String getCompressionRate() {
		return compressionRate;
	}

	/**
	 * Sets the compression rate.
	 *
	 * @param compressionRate the new compression rate
	 */
	public void setCompressionRate(String compressionRate) {
		this.compressionRate = compressionRate;
	}

	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}
	
	/**
	 * Sets the duration.
	 *
	 * @param duration the new duration
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

}
