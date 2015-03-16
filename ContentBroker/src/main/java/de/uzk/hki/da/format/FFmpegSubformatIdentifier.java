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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FFmpegSubformatIdentifier implements FormatIdentifier, Connector{

	@Override
	public String identify(File f) throws IOException {

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
		ProcessInformation pi;
		try {
			pi =  new CommandLineConnector().runCmdSynchronously(new String[] {"ffmpeg","-version"});
		} catch (IOException e) {
			return false;
		}
		String ffmpegOutput = pi.getStdOut();
		Pattern MY_PATTERN = Pattern.compile(".*(\\d+\\.\\d+\\.\\d+).*");
		Matcher m = MY_PATTERN.matcher(ffmpegOutput); m.find();
		String version = m.group(1);

		List<String> acceptedVersions=Arrays.asList(new String[] {"2.2.10","0.6.5","0.6.7","0.6.6","0.10.3","2.2.1"});
		if (acceptedVersions.contains(version)) 
			return true;
		else
			return false;
	}
}
