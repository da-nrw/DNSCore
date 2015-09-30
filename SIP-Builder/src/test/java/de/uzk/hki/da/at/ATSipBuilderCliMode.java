/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

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

public class ATSipBuilderCliMode {

	private static String nestedSip1 = "urn+nbn+de+hbz+42.tgz";
	private static String nestedSip2 = "urn+nbn+de+hbz+6+1-3602.tgz";
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/SIPFactoryTests/nestedFolders");
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FileUtils.deleteDirectory(new File("target/atTargetDir/"));
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+nestedSip1));
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+nestedSip2));
		FileUtils.deleteDirectory(targetDir);
		p.destroy();
	}
	
	@Test
	public void testNestedStructure() throws IOException {
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+sourceDir.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -nested -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	    }

	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+nestedSip1).exists());
	    assertTrue(new File("target/atTargetDir/"+nestedSip2).exists());
	}

}
