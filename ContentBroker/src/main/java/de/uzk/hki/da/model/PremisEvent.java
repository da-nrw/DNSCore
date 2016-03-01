package de.uzk.hki.da.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="premis_events")
public class PremisEvent {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The id type. */
	@Transient
	private IdType idType;

	/** The identifier. */
	
	private String identifier;
	
	/** The agent_type. */
	@Column(columnDefinition="varchar(50)")
	private String agent_type;
	
	/** The agent_name. */
	@Column(columnDefinition="varchar(50)")
	private String agent_name;
	
	
	/** The type. */
	@Column(columnDefinition="varchar(30)")
	private String type;
	
	/** The source_file. */
	@ManyToOne
    @PrimaryKeyJoinColumn(
        name="source_file_id")
	private DAFile source_file;
	
	/** The target_file. */
	@ManyToOne
    @PrimaryKeyJoinColumn(
        name="target_file_id")
	private DAFile target_file;
	
	/** The date. */
	private Date date;
	
	/** The detail. */
	@Column(length=1000)
	private String detail;
	
	/** The outcome. */
	@Transient
	private String outcome;
	
	private String test;
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
	
	/**
	 * The Enum IdType.
	 */
	public enum IdType {
		
		/** The target file path. */
		TARGET_FILE_PATH, 
 /** The ingest id. */
 INGEST_ID, 
 /** The sip creation id. */
 SIP_CREATION_ID,
 CONVERSION_QUEUE_ID,
 JOB_ID
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
		System.out.println("--- in setType von PremisEvent ---");
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String idt = "NULL-";
		if (idType!=null) idt=idType.name();
	
		return String.format("Event[type:%s,detail:%s,agentType:%s,agentName:%s,identifier:%s,idType:%s]",
				getType(),getDetail(),getAgent_type(),getAgent_name(),getIdentifier(),idt);
	}
	
	/**
	 * Gets the outcome.
	 *
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * Sets the outcome.
	 *
	 * @param outcome the new outcome
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	
	/**
	 * Gets the detail.
	 *
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	
	/**
	 * Sets the detail.
	 *
	 * @param detail the new detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	/**
	 * Gets the id type.
	 *
	 * @return the id type
	 */
	public IdType getIdType() {
		return idType;
	}
	
	/**
	 * Sets the id type.
	 *
	 * @param idType the new id type
	 */
	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param identifier the new identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Gets the source_file.
	 *
	 * @return the source_file
	 * @author Daniel M. de Oliveira
	 */
	public DAFile getSource_file() {
		if ((getType()==null)||getType().equals("")) throw new IllegalStateException("Type for Event not set");
		if (!getType().equals("CONVERT") && !getType().equals("COPY")) throw new RuntimeException("Operation not allowed for non CONVERT/COPY events");
		
		return source_file;
	}

	/**
	 * Sets the source_file.
	 *
	 * @param source_file the new source_file
	 */
	public void setSource_file(DAFile source_file) {
		this.source_file = source_file;
	}

	/**
	 * Gets the target_file.
	 *
	 * @return the target_file
	 * @author Daniel M. de Oliveira
	 * @throws IllegalStateException if called and event type not set 
	 * @throws IllegalStateException if called and event is not of type CONVERT or COPY
	 */
	public DAFile getTarget_file() {
		if ((getType()==null)||getType().equals("")) throw new IllegalStateException("Type for Event not set");
		if (!getType().equals("CONVERT") && !getType().equals("COPY") && !getType().equals("CREATE")) throw new IllegalStateException("Operation not allowed for non CONVERT/COPY/CREATE events");
		
		return target_file;
	}

	/**
	 * Sets the target_file.
	 *
	 * @param target_file the new target_file
	 */
	public void setTarget_file(DAFile target_file) {
		this.target_file = target_file;
	}

	/**
	 * Gets the agent_name.
	 *
	 * @return the agent_name
	 */
	public String getAgent_name() {
		return agent_name;
	}

	/**
	 * Sets the agent_name.
	 *
	 * @param agentName the new agent_name
	 */
	public void setAgent_name(String agentName) {
		this.agent_name = agentName;
	}
	

	/**
	 * Gets the agent_type.
	 *
	 * @return the agent_type
	 */
	public String getAgent_type() {
		return agent_type;
	}

	/**
	 * Sets the agent_type.
	 *
	 * @param agentType the new agent_type
	 */
	public void setAgent_type(String agentType) {
		this.agent_type = agentType;
	}

	
}
