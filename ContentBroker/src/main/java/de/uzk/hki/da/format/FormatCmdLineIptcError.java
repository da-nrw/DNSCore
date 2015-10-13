/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
  Landschaftsverband Rheinland

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
/**
 * @author jens Peters
 * Known IPTC Tag Error which is being found in BigTiff Pictures by some customers.
 */
package de.uzk.hki.da.format;

import de.uzk.hki.da.core.UserException.UserExceptionId;

public class FormatCmdLineIptcError implements FormatCmdLineError{
	
	private String errorText = "Probleme mit RichTIFFIPTC.";
	
	private UserExceptionId errorId = UserExceptionId.WRONG_DATA_TYPE_IPTC;
	
	private String outContainsRegex = "(?s).*RichTIFFIPTC.*";

	public FormatCmdLineIptcError() {
	}

	@Override
	public String getSearchErrForRegex() {
		
		return outContainsRegex ;
	}

	@Override
	public void setErrorText(String errorText) {
		this.errorText = errorText;
		
	}

	@Override
	public String getErrorText() {
		return errorText;
	}

	@Override
	public UserExceptionId getUserExceptionId() {
		return errorId;
	}

	@Override
	public void setUserExceptionId(UserExceptionId userid) {
		errorId = userid;
	}
}
