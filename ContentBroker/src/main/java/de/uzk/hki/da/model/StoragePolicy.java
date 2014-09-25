package de.uzk.hki.da.model;

import java.util.List;


/**
 * The Class StoragePolicy.
 * @author Jens Peters
 */
public class StoragePolicy {

	@SuppressWarnings("unused")
	private Node node = null;
	
	private int min_nodes;
	
	private String forbiddenNodes;
	
	public StoragePolicy(Node localnode) {
		this.node = localnode;
	}
	
	private  List<String>destinations;
	
	public boolean isPolicyAchievable() {
		if (destinations==null) return false;
		if (destinations.size()>=min_nodes) return true;
		else return false;
	}


	public int getMinNodes() {
		return min_nodes;
	}


	public void setMinNodes(int min_nodes) {
		this.min_nodes = min_nodes;
	}


	/**
	 * @param destinations the destinations to set
	 */
	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}

	/**
	 * @return the destinations
	 */
	public List<String> getDestinations() {
		return destinations;
	}


	public String getForbiddenNodes() {
		return forbiddenNodes;
	}


	public void setForbiddenNodes(String forbiddenNodes) {
		this.forbiddenNodes = forbiddenNodes;
	}

	
	

		
}
