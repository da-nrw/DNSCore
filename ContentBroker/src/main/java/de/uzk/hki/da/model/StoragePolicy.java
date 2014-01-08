package de.uzk.hki.da.model;

import java.util.ArrayList;


/**
 * The Class StoragePolicy.
 * @author Jens Peters
 */
public class StoragePolicy {

	@SuppressWarnings("unused")
	private Node node = null;
	
	private int min_nodes;
	
	public StoragePolicy(Node localnode) {
		this.node = localnode;
	}
	
	private  ArrayList<String>destinations;
	
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
	public void setDestinations(ArrayList<String> destinations) {
		this.destinations = destinations;
	}


	/**
	 * @return the destinations
	 */
	public ArrayList<String> getDestinations() {
		return destinations;
	}

	
	

		
}
