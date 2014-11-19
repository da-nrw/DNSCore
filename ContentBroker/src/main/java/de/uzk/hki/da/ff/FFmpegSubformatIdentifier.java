package de.uzk.hki.da.ff;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FFmpegSubformatIdentifier implements SecondaryFormatIdentifier{

	@Override
	public String identify(File f) throws IOException {

		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {"ffmpeg","-i",f.toString()});
		String ffmpegOutput = pi.getStdErr();
		
		String ffmpegOutput2[] = ffmpegOutput.split("Stream.*Video: ");
		String ffmpegOutput3[] = ffmpegOutput2[1].split("\n");
		String ffmpegOutput4[] = ffmpegOutput3[0].split("\\s");
		
		return ffmpegOutput4[0];
	}

}
