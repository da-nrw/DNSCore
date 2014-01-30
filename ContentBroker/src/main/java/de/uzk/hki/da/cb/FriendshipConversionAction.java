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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FriendshipConversionAction takes a job that has been created by ScanAction on another node,
 * replicates the xIP to its local node, executes all of the jobs ConversionInstructions
 * and replicates all newly created files back to the node where the ScanAction has run.
 * @author Daniel M. de Oliveira
 */
public class FriendshipConversionAction extends AbstractAction{

	static final Logger logger = LoggerFactory.getLogger(FriendshipConversionAction.class);
	@Override
	boolean implementation() throws IOException {
		return true;
	}
	@Override
	void rollback() throws Exception {}
}
