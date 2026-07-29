package de.uzk.hki.da.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uzk.hki.da.sb.Feedback;

public class SimpleLicenseAppender extends ExistingSIPModifier {
	static final String DEFAULT_LICENSE="                <publicationLicense href=\"https://creativecommons.org/licenses/by-sa/4.0/\" displayLabel=\"CC-BY-SA-Lizenz (v4.0)\">CC v4.0 International Lizenz: Namensnennung - Weitergabe unter gleichen Bedingungen</publicationLicense>";
	static final String TMP_FILE=".tmp";
	private static Logger logger = LogManager.getLogger(SimpleLicenseAppender.class);
	
	protected Feedback doModificationOnSIPData(File unpackedSIPDir) throws IOException{
		File premisFileIN=new File(unpackedSIPDir,C.PREMIS_XML);
		File premisFileOUT=new File(unpackedSIPDir,C.PREMIS_XML+TMP_FILE);
		
		Scanner scanner = new Scanner(premisFileIN);
		BufferedWriter out = new BufferedWriter(new FileWriter(premisFileOUT, true));
		String line =null;
		boolean inrightsExtension=false,hasAlreadyLicense=false;
		while(scanner.hasNextLine()){
			if(line!=null)
				out.newLine();
			line = scanner.nextLine();
			
			if(line.trim().startsWith("<rightsExtension") ){
				inrightsExtension=true;
			}else if(line.trim().startsWith("</rightsExtension>") ){
				inrightsExtension=false;
			}
			
			if(line.trim().startsWith("<publicationLicense ") && inrightsExtension){
				hasAlreadyLicense=true;
				break;
			}
			if(!hasAlreadyLicense && inrightsExtension && line.trim().startsWith("</rightsGranted>")){ //Lizenz einmfügen, vor dem schliesenden rightsGranted-Tag im rightsExtension-Block
				out.write(DEFAULT_LICENSE);
				out.newLine();
			}
			out.write(line);
		}
		scanner.close();
		out.close();
		if(!hasAlreadyLicense){
			Files.copy(Paths.get(premisFileOUT.toURI()), Paths.get(premisFileIN.toURI()),StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
		}
		if(!premisFileOUT.delete()){
			logger.error("Undeletable TMP-File : "+premisFileOUT);
			return Feedback.ABORT;
		}
		return Feedback.SUCCESS;
	}
	
}
