package de.uzk.hki.da.at;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATSipBuilderCliMets {

	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String singleSip = "ATBuildSingleMetsSip.tgz";
	private static Process p;

	@Before
	public void setUp() throws IOException {
		FileUtils.deleteDirectory(targetDir);
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(targetDir);
		p.destroy();
	}

	@Test
	public void testBuildSingleSipCorrectReferences() throws IOException {

		File source = new File(sourceDir, "ATBuildSingleMetsSip");

		String cmd = "./SipBuilder-Unix.sh -source=\""
				+ source.getAbsolutePath() + "/\" -destination=\""
				+ targetDir.getAbsolutePath() + "/\" -single -alwaysOverwrite";

		p = Runtime.getRuntime().exec(cmd, null,
				new File("target/installation"));

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		boolean identifiedMetadataType = false;
		String s = "";
		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
			if (s.contains("Identified metadata file")
					&& s.contains("DNSCore/SIP-Builder/src/test/resources/at/ATBuildSingleMetsSip/export_mets.xml=METS}")) {
				identifiedMetadataType = true;
			}
		}

		// read any errors from the attempted command
		System.out
				.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

		assertTrue(new File("target/atTargetDir/" + singleSip).exists());
		assertTrue(identifiedMetadataType);
	}

	@Test
	public void testBuildSingleSipErrorWrongReferences() throws IOException {

		File source = new File(sourceDir,
				"ATBuildSingleMetsSipWrongRefErrorCase/ATBuildSingleMetsSipWrongReferences");

		String cmd = "./SipBuilder-Unix.sh -source=\""
				+ source.getAbsolutePath() + "/\" -destination=\""
				+ targetDir.getAbsolutePath() + "/\" -single -alwaysOverwrite";

		p = Runtime.getRuntime().exec(cmd, null,
				new File("target/installation"));

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		boolean identifiedMetadataType = false;
		boolean missingFilesMsg = false;
		boolean missingFilesList = false;
		boolean noSipCreated = false;
		String s = "";
		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
			if (s.contains("Identified metadata file")
					&& s.contains("DNSCore/SIP-Builder/src/test/resources/at/ATBuildSingleMetsSipWrongRefErrorCase/"
							+ "ATBuildSingleMetsSipWrongReferences/export_mets.xml=METS")) {
				identifiedMetadataType = true;
			}
			if (s.contains("Folgende Dateien konnten nicht gefunden werden:")) {
				missingFilesMsg = true;
			}
			if (s.contains("[image/801616.bmp, image/801618.bmp]")) {
				missingFilesList = true;
			}
			if (s.contains("Das SIP wird nicht erstellt. Bitte korrigieren Sie Ihre Metadaten.")) {
				noSipCreated = true;
			}
		}

		// read any errors from the attempted command
		System.out
				.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

		assertTrue(identifiedMetadataType);
		assertTrue(missingFilesMsg);
		assertTrue(missingFilesList);
		assertTrue(noSipCreated);
		assertFalse(new File("target/atTargetDir/" + singleSip).exists());
	}
}
