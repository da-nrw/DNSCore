package de.uzk.hki.da.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="known_errors")
public class KnownError {

	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(columnDefinition="varchar(100)")
	private String error_name = "";
	
	@Column(columnDefinition="varchar(255)")
	private String std_err_contains_regex = "";
	
	@Column(columnDefinition="varchar(255)")
	private String description;
	
	@Column(columnDefinition="varchar(255)")
	private String question;
	
	@Column(columnDefinition="varchar(1000)")
	private String advice;
	
	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getError_name() {
		return error_name;
	}

	public void setError_name(String error_name) {
		this.error_name = error_name;
	}

	public String getStd_err_contains_regex() {
		return std_err_contains_regex;
	}

	public void setStd_err_contains_regex(String std_err_contains_regex) {
		this.std_err_contains_regex = std_err_contains_regex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

}
