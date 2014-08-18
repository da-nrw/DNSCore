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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;


/**
 * Selects the node for every ConversionRoutine referenced within on Job. Since every
 * of these Conversion Routines can get executed on possibly several nodes and every
 * node other than the initial node implies replication before conversion, the method
 * tries to minimize the nodes needed for conversion of all files within a given job.
 *
 * @return one node on which a ConversionRoutine actually will be processed for each of the ConversionRoutines
 * required to fulfill a Job. The result is held in a Map.
 * @author Daniel M. de Oliveira
 */

public class DistributedConversionHelper {

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(DistributedConversionHelper.class);
	

	
	
	
	/**
	 * Iterates over a collection of Nodes and creates a list of their working
	 * resources. Note that even if the collection contains the same Nodes more
	 * than once this routine will shrink the collection to a set first so each
	 * Node is considered only once.
	 *
	 * @param nodes the nodes
	 * @return comma separated list of working resources
	 */
	private static String listOfWorkingResources(Collection<Node> nodes) {

		Set<Node> nodesSet = new HashSet<Node>(nodes);

		String result = ""; 
		if (nodes.size() == 0)
			return "";

		for (Node n : nodesSet) {
			result += n.getWorkingResource() + ",";
		}
		result = result.substring(0, result.lastIndexOf(","));
		return result;
	}
}
