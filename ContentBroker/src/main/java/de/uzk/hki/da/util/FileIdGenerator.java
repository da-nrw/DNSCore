package de.uzk.hki.da.util;


public class FileIdGenerator {
	
	protected FileIdGenerator() {
	}
	
	public static String getFileId(String path) {
		// replace slashes
		String dsID = path.replace("/", "-");
		
		// eliminate disallowed beginnings
		if (Character.isDigit(dsID.charAt(0))
				|| dsID.startsWith("xml")
				|| dsID.startsWith("XML")) {
			dsID = "_" + dsID;
		}
		
		// replace disallowed characters
		dsID = dsID.replaceAll("[^\\p{L}\\p{Digit}\\._-]","_");
		
		return dsID;
	}
}
