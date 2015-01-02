package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FFmpegSubformatIdentificationStrategy implements FormatIdentificationStrategy{

	@Override
	public String identify(File f) throws IOException {

		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-i",f.toString()});
		String ffmpegOutput = pi.getStdErr();
		System.out.println("ffmpegOutput:"+ffmpegOutput);
		Pattern MY_PATTERN = Pattern.compile(".*Stream.*Video:\\s([a-z0-9]+)[,\\s].*");
		Matcher m = MY_PATTERN.matcher(ffmpegOutput); m.find();
		String codec=m.group(1);
		
		System.out.println("c:"+codec);
		
		return codec;
	}

	@Override
	public boolean healthCheck() {
		ProcessInformation pi;
		try {
			pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-version"});
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
