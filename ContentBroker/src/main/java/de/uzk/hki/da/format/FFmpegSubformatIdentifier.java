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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FFmpegSubformatIdentifier implements FormatIdentifier, Connector{

	private Logger logger = LoggerFactory.getLogger( FFmpegSubformatIdentifier.class );
	
	private String[] supportedVersions = new String[] {"2.2.10","0.6.5","0.6.7","0.6.6","0.10.3","2.2.1"};
	
	@Override
	public String identify(File f,boolean pruneExceptions) throws IOException {

		ProcessInformation pi = new CommandLineConnector().runCmdSynchronously(new String[] {"ffmpeg","-i",f.toString()});
		String ffmpegOutput = pi.getStdErr();
		System.out.println("ffmpegOutput:"+ffmpegOutput);
		Pattern MY_PATTERN = Pattern.compile(".*Stream.*Video:\\s([a-z0-9]+)[,\\s].*");
		Matcher m = MY_PATTERN.matcher(ffmpegOutput); m.find();
		String codec=m.group(1);
		
		System.out.println("c:"+codec);
		
		return codec;
	}

	@Override
	public boolean isConnectable() {
		
		String version=ffmpegVersion();
		if (isNotSet(version)) return false;
		
		return isSupported(version);
		
		

		
	}

	@SuppressWarnings("unused") // This is for if (pi!=null) which is marked as dead code by eclipse but really isn't because pi can be null.
	private String ffmpegCmd(String[] cmd) {
		ProcessInformation pi=null;
		try {
			pi =  new CommandLineConnector().runCmdSynchronously(cmd);
		} catch (IOException e) {
			if (pi!=null) 
				logger.error(pi.getStdErr());
			logger.error(e.getMessage());
			return null;
		}
		return pi.getStdOut();
	}
	
	
	private String ffmpegVersion() {
		String versionOutput = ffmpegCmd(new String[] {"ffmpeg","-version"});
		return parseVersionOutpu(versionOutput);
	}
	
	private String parseVersionOutpu(String ffmpegVersionStdout) {
		
		Pattern MY_PATTERN = Pattern.compile(".*(\\d+\\.\\d+\\.\\d+).*");
		Matcher m = MY_PATTERN.matcher(ffmpegVersionStdout); m.find();
		String version = m.group(1);
		
		return version;
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
	
	}

	@Override
	public CommandLineConnector getCliConnector() {
		return null;
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





