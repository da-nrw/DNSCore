package de.uzk.hki.da.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="jhove_parameter_mapping")
public class JHoveParameterMapping {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="mime_type", columnDefinition="varchar(255)")
	private String mime_type;
	
	@Column(name="map_parameter", columnDefinition="varchar(255)")
	private String map_prameter;
	
	

	public JHoveParameterMapping() {
		super();
	}

	public JHoveParameterMapping(String mime_type, String map_prameter) {
		super();
		this.mime_type = mime_type;
		this.map_prameter = map_prameter;
	}

	public String getMap_prameter() {
		return map_prameter;
	}

	public void setMap_prameter(String map_prameter) {
		this.map_prameter = map_prameter;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	
	@Override
	public String toString() {
		return "JHoveParameterMapping [mime_type=" + mime_type + ", map_prameter=" + map_prameter + "]";
	}
}
