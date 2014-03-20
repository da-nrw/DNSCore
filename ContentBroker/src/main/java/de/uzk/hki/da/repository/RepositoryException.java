package de.uzk.hki.da.repository;

public class RepositoryException extends Exception {

	private static final long serialVersionUID = -7332746132365206286L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
