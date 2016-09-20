/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
  Landschaftsverband Rheinland

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.format;

import static de.uzk.hki.da.utils.StringUtilities.isNotSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

/**
 * @author Daniel M. de Oliveira
 */
public class SoxSubformatIdentifier implements FormatIdentifier, Connector {

	private Logger logger = LoggerFactory.getLogger( SoxSubformatIdentifier.class );
	// 14.2.0
	private String[] supportedVersions = new String[] {"14.2.0","14.3.2","14.4.1"};
	
	private CommandLineConnector cli;
	
	@Override
	public boolean isConnectable() {
		String version=soxVersion();
		if (isNotSet(version)) return false;
		
		return isSupported(version);
	}
	
	
	@Override
	public String identify(File f,boolean pruneExceptions) throws IOException {
		if (! f.exists()) throw new FileNotFoundException(f.toString());
		
		String codec = soxCodec(f);
		if (isNotSet(codec)) return "";

		return crunch(codec);
	}
	
	private String crunch(String codec) {
		if (codec.contains(" ")) logger.warn("codec \""+codec+"\" contains white spaces");
		if (codec.contains("PCM")) return "PCM";
		return codec;
	}
	
	
	private String soxCodec(File f) {
		String output = soxCMD(new String[] {"soxi",f.getAbsolutePath()});
		return parseCodecOutput(output);
	}

	
	private String soxVersion() {
		String output = soxCMD((new String[] {"sox","--version"}));
		return parseVersionOutput(output);
	}
	
	
	@SuppressWarnings("unused") // This is for if (pi!=null) which is marked as dead code by eclipse but really isn't because pi can be null.
	private String soxCMD(String[] cmd) {
		ProcessInformation pi=null;
		try {
			pi =  getCliConnector().runCmdSynchronously(cmd);
		} catch (IOException e) {
			if (pi!=null) 
				logger.error(pi.getStdErr());
			logger.error(e.getMessage());
			return null;
		}
		return pi.getStdOut();
	}
	
	
	private String parseVersionOutput(String soxVersionStdOut) {

		Pattern MY_PATTERN = Pattern.compile("sox.*(\\d{2}\\.\\d+\\.\\d+).*");
		Matcher m = MY_PATTERN.matcher(soxVersionStdOut); m.find();
		String version = m.group(1);
		return version;
	}

	private String parseCodecOutput(String soxCodecStdOut) {
		
		Pattern MY_PATTERN = Pattern.compile(".*Sample.*Encoding:\\s(.*)\n");
		Matcher m = MY_PATTERN.matcher(soxCodecStdOut); m.find();
		String codec=m.group(1);
		
		return codec;
	}
	
	
	private boolean isSupported(String version) {
		List<String> acceptedVersions=Arrays.asList(supportedVersions);
		if (acceptedVersions.contains(version)) 
			return true;
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
		
	}


	@Override
	public KnownFormatCmdLineErrors getKnownFormatCommandLineErrors() {
		return null;
	}
	
	

}
