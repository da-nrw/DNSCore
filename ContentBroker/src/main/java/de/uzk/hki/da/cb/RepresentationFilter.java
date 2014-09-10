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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The Class RepresentationFilter.
 */
public class RepresentationFilter implements FilenameFilter {
	  
  	/** The pattern. */
  	protected String pattern= "[0-9]{4}\\_[0-9]{2}\\_[0-9]{2}\\+[0-9]{2}\\_[0-9]{2}\\+[a-z]{1}";
	  
	  /* (non-Javadoc)
  	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
  	 */
  	@Override
	public boolean accept (File dir, String name) {
		  
	      Pattern p = Pattern.compile(pattern);
	      Matcher m = p.matcher(name);
		  
	      return m.matches();
	  }
}
