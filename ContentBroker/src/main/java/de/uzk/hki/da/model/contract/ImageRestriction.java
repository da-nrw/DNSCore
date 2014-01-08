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

package de.uzk.hki.da.model.contract;


/**
 * The Class ImageRestriction.
 */
public class ImageRestriction {

	/** The width. */
	private String width;
	
	/** The height. */
	private String height;
	
	/** The footer text. */
	private String footerText;
	
	/** The watermark string. */
	private String watermarkString;
	
	/** The watermark point size. */
	private String watermarkPointSize;
	
	/** The watermark position. */
	private String watermarkPosition;
	
	/** The watermark opacity. */
	private String watermarkOpacity;
	
	/**
	 * Instantiates a new image restriction.
	 */
	public ImageRestriction() {
		width = null;
		height = null;
		footerText = null;
		watermarkString = null;
		watermarkPointSize = null;
		watermarkPosition = null;
		watermarkOpacity = null;
	}
	
	/**
	 * Instantiates a new image restriction.
	 *
	 * @param width the width
	 * @param height the height
	 * @param footerText the footer text
	 */
	public ImageRestriction(String width, String height, String footerText) {
		this.width = width;
		this.height = height;
		this.footerText = footerText;
		watermarkString = null;
		watermarkPointSize = null;
		watermarkPosition = null;
		watermarkOpacity = null;
	}
	
	/**
	 * Instantiates a new image restriction.
	 *
	 * @param width the width
	 * @param height the height
	 * @param footerText the footer text
	 * @param watermarkString the watermark string
	 * @param watermarkPointSize the watermark point size
	 * @param watermarkPosition the watermark position
	 * @param watermarkOpacity the watermark opacity
	 */
	public ImageRestriction(String width, String height, String footerText,
							String watermarkString, String watermarkPointSize,
							String watermarkPosition, String watermarkOpacity) {
		this.width = width;
		this.height = height;
		this.footerText = footerText;
		this.watermarkString = watermarkString;
		this.watermarkPointSize = watermarkPointSize;
		this.watermarkPosition = watermarkPosition;
		this.watermarkOpacity = watermarkOpacity;
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
	 * Gets the footer text.
	 *
	 * @return the footer text
	 */
	public String getFooterText() {
		return footerText;
	}
	
	/**
	 * Sets the footer text.
	 *
	 * @param footerText the new footer text
	 */
	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	/**
	 * Gets the watermark string.
	 *
	 * @return the watermark string
	 */
	public String getWatermarkString() {
		return watermarkString;
	}

	/**
	 * Sets the watermark string.
	 *
	 * @param watermarkString the new watermark string
	 */
	public void setWatermarkString(String watermarkString) {
		this.watermarkString = watermarkString;
	}

	/**
	 * Gets the watermark point size.
	 *
	 * @return the watermark point size
	 */
	public String getWatermarkPointSize() {
		return watermarkPointSize;
	}

	/**
	 * Sets the watermark point size.
	 *
	 * @param watermarkPointSize the new watermark point size
	 */
	public void setWatermarkPointSize(String watermarkPointSize) {
		this.watermarkPointSize = watermarkPointSize;
	}

	/**
	 * Gets the watermark position.
	 *
	 * @return the watermark position
	 */
	public String getWatermarkPosition() {
		return watermarkPosition;
	}

	/**
	 * Sets the watermark position.
	 *
	 * @param watermarkPosition the new watermark position
	 */
	public void setWatermarkPosition(String watermarkPosition) {
		this.watermarkPosition = watermarkPosition;
	}

	/**
	 * Gets the watermark opacity.
	 *
	 * @return the watermark opacity
	 */
	public String getWatermarkOpacity() {
		return watermarkOpacity;
	}

	/**
	 * Sets the watermark opacity.
	 *
	 * @param watermarkOpacity the new watermark opacity
	 */
	public void setWatermarkOpacity(String watermarkOpacity) {
		this.watermarkOpacity = watermarkOpacity;
	}	
}
