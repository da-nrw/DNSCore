package de.uzk.hki.da.format;


public class QualityLevelException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public static enum Type{IDENTIFICATION,VALIDATION,CONVERSION}
	private Type type;

	public QualityLevelException(Type type, String msg) {
		super(msg);
		this.type=type;		
	}

	public Type getType() {
		return type;
	}
}
