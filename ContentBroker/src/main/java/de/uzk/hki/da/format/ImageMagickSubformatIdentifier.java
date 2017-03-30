package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class ImageMagickSubformatIdentifier implements FormatIdentifier, Connector{

	
	CommandLineConnector cli;
	
	KnownFormatCmdLineErrors knownErrors;
	
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
					"identify","-format","%C'",input};
		FormatCmdLineExecutor cle = new FormatCmdLineExecutor( getCliConnector(), knownErrors);
		cle.setPruneExceptions(pruneExceptions);
		try {
		cle.execute(cmd);
		} catch (UserFileFormatException ufe) {
			if (!ufe.isWasPruned()) {
				throw ufe;
			}
		}

		String stdOut = cle.getStdOut();
		String compression = "";

		if (stdOut.length()>0) {
			String[] subs = stdOut.split("'");
			TreeSet<String> subSet = new TreeSet<String>();
			for (int sss=0; sss<subs.length; sss++){
				subSet.add(subs[sss]);
			}
			for (String subbi: subSet){
				compression += subbi + " ";
			}
			compression = compression.trim();
		}
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

	@Override
	public void setKnownFormatCommandLineErrors(
		KnownFormatCmdLineErrors knownErrors) {
		this.knownErrors = knownErrors;
	}

	@Override
	public KnownFormatCmdLineErrors getKnownFormatCommandLineErrors() {
		return null;
	}

}
