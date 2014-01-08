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

import javax.xml.bind.annotation.XmlList;


/**
 * The Class TextRestriction.
 */
public class TextRestriction {
	
	/** The pages. */
	private Integer pages;
	
	/** The certain pages. */
	private int[] certainPages;
	
	/**
	 * Instantiates a new text restriction.
	 */
	public TextRestriction() {
		
	}

	/**
	 * Instantiates a new text restriction.
	 *
	 * @param pages the pages
	 */
	public TextRestriction(Integer pages) {
		this.pages = pages;
	}

	/**
	 * Instantiates a new text restriction.
	 *
	 * @param pages the pages
	 * @param certainPages the certain pages
	 */
	public TextRestriction(Integer pages, int[] certainPages) {
		this.pages = pages;
		this.certainPages = certainPages;
	}

	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public Integer getPages() {
		return pages;
	}

	/**
	 * Sets the pages.
	 *
	 * @param pages the new pages
	 */
	public void setPages(Integer pages) {
		this.pages = pages;
	}

	/**
	 * Gets the certain pages.
	 *
	 * @return the certain pages
	 */
	@XmlList
	public int[] getCertainPages() {
		return certainPages;
	}

	/**
	 * Sets the certain pages.
	 *
	 * @param certainPages the new certain pages
	 */
	public void setCertainPages(int[] certainPages) {
		this.certainPages = certainPages;
	}

}
