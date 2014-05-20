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
	 * Select processing nodes.
	 *
	 * @param actualNode the actual node
	 * @param routines the routines
	 * @return the map
	 */
	public static Map<ConversionRoutine,Node> selectProcessingNodes(Node actualNode,Set<ConversionRoutine> routines) {
		Map<ConversionRoutine,Node> conversionRoutinesSelectedNodes= new HashMap<ConversionRoutine,Node>();
		Set<Node> selectedNodes= new HashSet<Node>();
		
		// select nodes for each of them
		for (ConversionRoutine conversionRoutine : routines){
			Set<Node> availabeNodesForActualRoutine= conversionRoutine.getNodes();	
			if (availabeNodesForActualRoutine.isEmpty()) throw new 
				IllegalStateException("ConversionRoutine "+ conversionRoutine.getName()+ "" +
						" exists but no node is defined which can handle it. " +
						"This shouldn't have happened because contractors cannot add routines to their " +
						"cart for which no nodes are defined.");
			logger.debug("Possible Machines for Conversion with \""+
					conversionRoutine.getName()+"\": "+conversionRoutine.getNodes());
			
			Node chosenNode=null;
			if (availabeNodesForActualRoutine.contains(actualNode)){
				logger.debug("availabeNodesForActualRoutine contain actualNode");
				
				chosenNode= actualNode;
				selectedNodes.add(actualNode);
				
			}else if (availabeNodesForActualRoutine.size()==1){
				
				chosenNode= availabeNodesForActualRoutine.iterator().next(); 
				selectedNodes.add(chosenNode);
				logger.debug("#1 node gets added to replNodes: "+chosenNode);
				
			}else{
				
				boolean oneOfTheAvailableNodesHasAlreadyBeenSelected=false;
				for (Node n : availabeNodesForActualRoutine){
					// is one of them already in the set
					if (selectedNodes.contains(n)){
						oneOfTheAvailableNodesHasAlreadyBeenSelected=true;
						chosenNode= n;
						break;
					}
				}
				if (!oneOfTheAvailableNodesHasAlreadyBeenSelected){
					chosenNode= availabeNodesForActualRoutine.iterator().next(); // here is a problem
					selectedNodes.add(chosenNode);
					logger.debug("#2 node gets added to replNodes: "+chosenNode);
				}
			}
			logger.debug(conversionRoutine.getName()+ " will be processed on "+chosenNode);
			conversionRoutinesSelectedNodes.put(conversionRoutine,chosenNode);
		}
		return conversionRoutinesSelectedNodes;
	}
	
	/**
	 * Determine processing nodes for conversion instructions.
	 *
	 * @param job the job
	 * @param localNode the local node
	 * @param routines the routines
	 * @return the map
	 */
	public static Map<ConversionRoutine, Node> determineProcessingNodesForConversionInstructions(Job job,Node localNode,Set<ConversionRoutine> routines){
		Map<ConversionRoutine, Node> conversionRoutinesSelectedNodes = 
				DistributedConversionHelper.selectProcessingNodes(localNode, routines);
		for (ConversionRoutine routine : conversionRoutinesSelectedNodes
				.keySet()){
			for (ConversionInstruction ci : job.getConversion_instructions()){
				if (ci.getConversion_routine().getName().equals(routine.getName())){
					ci.setNode(conversionRoutinesSelectedNodes.get(routine).getName());
				}
			}
		}
		return conversionRoutinesSelectedNodes;
	}
	
	/**
	 * Iterates over a collection of Nodes and creates a list of their working
	 * resources. Note that even if the collection contains the same Nodes more
	 * than once this routine will shrink the collection to a set first so each
	 * Node is considered only once.
	 *
	 * @param nodes the nodes
	 * @return comma separated list of working resources
	 */
	public static String listOfWorkingResources(Collection<Node> nodes) {

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
	
	/**
	 * Creates Jobs in the db which contain ConversionInstructions to be executed on different nodes.
	 * As a side effect the linkage to the ConversionInstructions which are not done locally get removed from
	 * the parent job object.
	 *
	 * @param parentJob the parent job
	 * @param nodes the nodes
	 * @param localNodeName the local node name
	 * @return List of Jobs.
	 * @author Daniel M. de Oliveira
	 * @param object 
	 */
	public static List<Job> createJobsWhichCantBeDoneLocally(Job parentJob,Set<Node> nodes,String localNodeName, Object object){
		
		ArrayList<ConversionInstruction> allCis = new ArrayList<ConversionInstruction>(parentJob.getConversion_instructions());
		
		List<Job> friendJobs = new ArrayList<Job>();
		for (Node node:nodes){
			
			if (node.getName().equals(localNodeName)) continue;
			
			@SuppressWarnings("unchecked")
			Collection <ConversionInstruction> cis = 
					CollectionUtils.select(allCis, new NodePredicate(node));
			if (cis.isEmpty()){
				logger.debug("not creating friend job, no ConversionInstructions found for node "+node.getName());
				continue;
			}
			
			logger.debug("Creating friend job for node \""+node+"\"");
			Job friendJob = new Job(parentJob,"580");
			friendJob.setResponsibleNodeName(node.getName());
			friendJob.setParent_id(parentJob.getId());
			friendJob.setDate_created(String.valueOf(new Date().getTime()/1000L));
			friendJob.setObject(object);
			// creating copies and linking them to the job
			logger.debug("The following conversions will not be done locally:");
			for (ConversionInstruction ci:cis){
				logger.debug("ci: "+ci);
				ConversionInstruction newCI = new ConversionInstruction(ci);
				newCI.setNode(node.getName());
				
				friendJob.getConversion_instructions().add(newCI);
				parentJob.getConversion_instructions().remove(ci);
			}
			friendJobs.add(friendJob);
		}
		return friendJobs;
	}
	
	
	
	
	
	
	
	
}
