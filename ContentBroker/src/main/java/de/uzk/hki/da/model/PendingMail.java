package de.uzk.hki.da.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class PendingMail.
 *
 * @author Josef Hammer
 */
@Entity
@Table(name="pending_mail")
public class PendingMail {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    int id;
    
	@Column(name="from_address", columnDefinition="varchar(255)")
	String fromAddress;

	@Column(name="to_address", columnDefinition="varchar(255)")
	String toAddress;

	@Column(name="subject", columnDefinition="varchar(255)")
	String subject;

	@Column(name="message", columnDefinition="text")
	String message;

	Boolean pooled; 

	int retries;

	@Column(name="created", columnDefinition="timestamp without time zone")
	Date created;

	@Column(name="last_try", columnDefinition="timestamp without time zone")
	Date lastTry;

	@Column(name="node_name", columnDefinition="varchar(255)")
	String nodeName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isPooled() {
		return pooled;
	}

	public void setPooled(Boolean pooled) {
		this.pooled = pooled;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastTry() {
		return lastTry;
	}

	public void setLastTry(Date lastTry) {
		this.lastTry = lastTry;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
