package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FFmpegSubformatIdentificationStrategy implements FormatIdentificationStrategy{

	@Override
	public String identify(File f) throws IOException {

		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-i",f.toString()});
		String ffmpegOutput = pi.getStdErr();
		
		String ffmpegOutput2[] = ffmpegOutput.split("Stream.*Video: ");
		String ffmpegOutput3[] = ffmpegOutput2[1].split("\n");
		String ffmpegOutput4[] = ffmpegOutput3[0].split("\\s");
		
		return ffmpegOutput4[0];
	}

	@Override
	public boolean healthCheck() {
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-version"});
		String ffmpegOutput = pi.getStdOut();
		String ffmpegOutput2[] = ffmpegOutput.split("built");
		System.out.println("ffmpegOutput2[0]:"+ffmpegOutput2[0]);
		String ffmpegOutput3[] = ffmpegOutput2[0].split("version");
		String version=ffmpegOutput3[1].trim();
		
		List<String> acceptedVersions=Arrays.asList(new String[] {"2.2.10","0.6.5","0.6.7","0.6.6","0.10.3","2.2.1"});
		if (acceptedVersions.contains(version)) 
			return true;
		else
			return false;
	}
}
