package de.uzk.hki.da.format;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.KnownError;

public class UserFileFormatException extends UserException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 77L;
	
	private KnownError knownError;
	
	private boolean wasPruned = false;
	
	public UserFileFormatException(KnownError knownError, boolean wasPruned){
		this(knownError,knownError.getDescription()+ " " + knownError.getAdvice(),wasPruned);
	}
	public UserFileFormatException(KnownError knownError, String message, boolean wasPruned){
		super(UserExceptionId.INVALID_USER_FILE_FORMAT,message);
		this.knownError = knownError;
		this.wasPruned = wasPruned;
	}
	public KnownError getKnownError() {
		return knownError;
	}

	public void setKnownError(KnownError knownError) {
		this.knownError = knownError;
	}
	public boolean isWasPruned() {
		return wasPruned;
	}
	public void setWasPruned(boolean wasPruned) {
		this.wasPruned = wasPruned;
	}
	
}
