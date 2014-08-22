package de.uzk.hki.da.core;

import java.util.List;

/**
 * Should be thrown only from ConcreteAction.implementation() and from nowhere else!
 * 
 * @author Thomas Kleinke
 */
public class UserException extends RuntimeException {

	public enum UserExceptionId {
		SIP_PREMIS_NOT_FOUND,
		INVALID_SIP_PREMIS,
		INVALID_OBJECT_DELTA,
		UNKNOWN_PACKAGE_TYPE,
		INCONSISTENT_PACKAGE,
		READ_SIP_PREMIS_ERROR,
		READ_METS_ERROR,
		WATERMARK_NO_GRAVITY,
		WATERMARK_NO_POINTSIZE,
		WATERMARK_NO_OPACITY,
		REPLACE_URLS_IN_METADATA_ERROR,
		RETRIEVAL_ERROR,
		DUPLICATE_DOCUMENT_NAMES,
		DUPLICATE_METADATA_FILE,
		INVALID_METADATA_FILE,
		DELTA_RECIEVED_BEFORE_ARCHIVED
		
	};
	
	private static final long serialVersionUID = -6346016039624940492L;
	
	private UserExceptionId id;
	private String errorInfo = "";

	public UserException() { }
	
	public UserException(UserExceptionId id, String exceptionMessage) {
		super(exceptionMessage);
		this.id = id;			
	}
	
	public UserException(UserExceptionId id, Exception exception) {
		super(exception);
		this.id = id;
	}
	
	public UserException(UserExceptionId id, String exceptionMessage, Exception exception) {
		super(exceptionMessage, exception);
		this.id = id;
	}
	
	public UserException(UserExceptionId id, String exceptionMessage, String errorInfo) {
		super(exceptionMessage);
		this.id = id;
		if (errorInfo == null)
			errorInfo = "";
		this.errorInfo = errorInfo;
	}
	
	public UserException(UserExceptionId id, String exceptionMessage, List<String> errorInfoLines) {
		super(exceptionMessage);
		this.id = id;
		
		String errorInfo = "";
		if (errorInfoLines != null) {
			for (String infoLine : errorInfoLines) {
				errorInfo += infoLine;
				errorInfo += "\n";
			}
		}
		
		this.errorInfo = errorInfo;
	}
		
	public UserException(UserExceptionId id, String exceptionMessage, String errorInfo, Exception exception) {
		super(exceptionMessage, exception);
		this.id = id;
		if (errorInfo == null)
			errorInfo = "";
		this.errorInfo = errorInfo;
	}

	/**
	 * Checks if the admin needs to be informed
	 */
	public boolean checkForAdminReport() {
		switch (id) {
		case RETRIEVAL_ERROR:
			return true;
		default:
			return false;
		}
	}
	
	public UserExceptionId getUserExceptionId() {
		return id;
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
}