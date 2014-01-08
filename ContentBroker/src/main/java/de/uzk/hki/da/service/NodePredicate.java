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

package de.uzk.hki.da.service;

import org.apache.commons.collections.Predicate;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.Node;


/**
 * The Class NodePredicate.
 */
public class NodePredicate implements Predicate{
	
	/** The node. */
	private Node node = null;
	
	/**
	 * Instantiates a new node predicate.
	 *
	 * @param node the node
	 */
	public NodePredicate(Node node){
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
	 */
	@Override
	public boolean evaluate(Object arg0) {
		
		ConversionInstruction ci = (ConversionInstruction) arg0;
		if (ci.getNode().equals(node.getName())) return true;
		return false;
	}
}
