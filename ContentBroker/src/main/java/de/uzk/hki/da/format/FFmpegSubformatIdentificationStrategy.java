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
		
		Pattern MY_PATTERN = Pattern.compile("Stream.*Video:\\s([a-z]+)\\s.*");
		Matcher m = MY_PATTERN.matcher(ffmpegOutput); m.find();
		String codec=m.group(1);
		
		System.out.println("c:"+codec);
		
		return codec;
	}

	@Override
	public boolean healthCheck() {
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-version"});
		String ffmpegOutput = pi.getStdOut();
		System.out.println("healthCheck.pi.getStdOut"+ffmpegOutput);
		Pattern MY_PATTERN = Pattern.compile(".*(\\d+\\.\\d+\\.\\d+).*");
		Matcher m = MY_PATTERN.matcher(ffmpegOutput); m.find();
		String version = m.group(1);
		System.out.println(":"+version);
		
		
//		String ffmpegOutput2[] = ffmpegOutput.split("built");
//		System.out.println("ffmpegOutput2[0]:"+ffmpegOutput2[0]);
//		String ffmpegOutput3[] = ffmpegOutput2[0].split("version");
//		System.out.println("ffmpegOutput3[0]"+ffmpegOutput3[0]);
//		String version=ffmpegOutput3[1].trim();
		
		List<String> acceptedVersions=Arrays.asList(new String[] {"2.2.10","0.6.5","0.6.7","0.6.6","0.10.3","2.2.1"});
		if (acceptedVersions.contains(version)) 
			return true;
		else
			return false;
	}
}
