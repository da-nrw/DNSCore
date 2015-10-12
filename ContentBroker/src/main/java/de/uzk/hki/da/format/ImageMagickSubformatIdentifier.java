package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class ImageMagickSubformatIdentifier implements FormatIdentifier, Connector{

	
	CommandLineConnector cli;
	
	@Override
	public String identify(File f,boolean pruneExceptions) throws IOException {
	
		return getEncoding(f.getAbsolutePath(), pruneExceptions);
	}
	
	/**
	 * Gets the encoding.
	 *
	 * @param input the input
	 * @return the encoding
	 */
	private String getEncoding(String input, boolean pruneExceptions) {
		
		String[] cmd = new String []{
					"identify","-format","'%C'",input};
		FormatCmdLineExecutor cle = new FormatCmdLineExecutor( getCliConnector());
		cle.setPruneExceptions(pruneExceptions);
		cle.execute(cmd);
		String compression = cle.getStdOut();
		if (compression.length()>0) 
		compression = compression.substring( 1, compression.length() - 1 );
		return compression;
	}


	@Override
	public boolean isConnectable() {
		String[] cmd = new String []{
					"identify","--version"};
		ProcessInformation pi;
		try {
			pi =  getCliConnector().runCmdSynchronously(cmd);
		} catch (IOException e) {
			return false;
		}
		if (pi.getStdOut().split("Magick")[0].endsWith("Image")) return true;
		else
			return false;
	}

	@Override
	public void setCliConnector(CommandLineConnector cli) {
		this.cli = cli;
		
	}

	@Override
	public CommandLineConnector getCliConnector() {
		if (cli==null) this.cli = new CommandLineConnector();
		return cli;
	}

}
