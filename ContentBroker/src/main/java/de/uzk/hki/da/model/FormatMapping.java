
package de.uzk.hki.da.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DNS FormatMapping
 * @author Gaby Bender
 *
 */
@Entity
@Table(name="format_mapping")
public class FormatMapping {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(Package.class);
	
	
	/** The fm_id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int fm_id;
	
	@Column(columnDefinition="varchar(100)")
	private String puid;
	
	@Column(columnDefinition="varchar(150)")
	private String extension;
	
	@Column(columnDefinition="varchar(255)")
	private String mime_type;
	
	@Column(columnDefinition="varchar(255)")
	private String format_name;
		
	private Date modified_date;

	public int getFm_id() {
		return fm_id;
	}

	public void setFm_id(int fm_id) {
		this.fm_id = fm_id;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	public String getFormat_name() {
		return format_name;
	}

	public void setFormat_name(String format_name) {
		this.format_name = format_name;
	}

	public Date getModified_date() {
		return modified_date;
	}

	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}


}
