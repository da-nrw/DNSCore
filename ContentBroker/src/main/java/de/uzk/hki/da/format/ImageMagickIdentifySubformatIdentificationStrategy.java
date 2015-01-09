package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class ImageMagickIdentifySubformatIdentificationStrategy implements FormatIdentificationStrategy{

	@Override
	public String identify(File f) throws IOException {
	
		return getEncoding(f.getAbsolutePath());
	}
	
	/**
	 * TODO remove code duplication with TiffConversionStrategy.
	 * Gets the encoding.
	 *
	 * @param input the input
	 * @return the encoding
	 */
	private String getEncoding(String input) {
		
		String[] cmd = new String []{
					"identify","-format","'%C'",input};
		ProcessInformation pi;
		try {
			pi =  new CommandLineConnector().runCmdSynchronously(cmd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (pi.getExitValue()!=0){
			throw new RuntimeException("Stderr: "+pi.getStdErr());
		}
		String compression = pi.getStdOut().trim();
		if (compression.length()>0) 
		compression = compression.substring( 1, compression.length() - 1 );
		
		return compression;
		
	}

	@Override
	public boolean healthCheck() {
		String[] cmd = new String []{
					"identify","--version"};
		ProcessInformation pi;
		try {
			pi =  new CommandLineConnector().runCmdSynchronously(cmd);
		} catch (IOException e) {
			return false;
		}
		if (pi.getStdOut().split("Magick")[0].endsWith("Image")) return true;
		else
			return false;
	}

}
