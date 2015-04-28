package de.uzk.hki.da.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="copyjob")
public class CopyJob {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String source;
	private Date last_tried;
	private int locked;
	private String source_node_identifier;
	private String dest_node_identifier;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getLast_tried() {
		return last_tried;
	}
	public void setLast_tried(Date last_tried) {
		this.last_tried = last_tried;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLocked() {
		return locked;
	}
	public void setLocked(int locked) {
		this.locked = locked;
	}
	public String getSource_name() {
		return source_node_identifier;
	}
	public void setSource_name(String source_node_identifier) {
		this.source_node_identifier = source_node_identifier;
	}
	public String getDest_name() {
		return dest_node_identifier;
	}
	public void setDest_name(String dest_node_identifier) {
		this.dest_node_identifier = dest_node_identifier;
	}
}
