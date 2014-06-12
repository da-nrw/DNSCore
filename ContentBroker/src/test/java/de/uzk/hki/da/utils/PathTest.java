/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 

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



package de.uzk.hki.da.utils;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.utils.Path;

/**
 * Tests the Path and RelativePath classes.
 * 
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 *
 */

public class PathTest {
	
	static String resultTestString;
	static String fileSeparator;
	static String[] resultStringArray;
	static String testDirectory1;
	static String testDirectory2;
	static String testDirectory3;
	
	@BeforeClass
	public static void newTestPath() {
		testDirectory1 = "directory1";
		testDirectory2 = "directory2";
		testDirectory3 = "directory3";
		Path path = new Path (testDirectory1, testDirectory2, testDirectory3);
		resultTestString = path.toString();
		fileSeparator = File.separator;
		resultStringArray = resultTestString.split(fileSeparator);
	}
	
	@Test 
	public void compareTestAndResultString() {
		String manuelString = File.separator + testDirectory1 + File.separator + testDirectory2 + File.separator + testDirectory3;
		assertEquals(manuelString, resultTestString);
	}
	
	@Test
	public void testSize(){
		int numTestDirectories = 3;
		int sumLengthSubstrings = 0;
		for(int i=0; i<resultStringArray.length; i++) {
			sumLengthSubstrings = sumLengthSubstrings + resultStringArray[i].length();
		}
//		Add the file separators (=numTestDirectories) to sum
		sumLengthSubstrings = sumLengthSubstrings + numTestDirectories;
		assertTrue(resultTestString.length()==sumLengthSubstrings);
	}
	
	@Test
	public void testFirstFileSeparator(){
		assertTrue(resultTestString.startsWith(File.separator));
	}
	
	@Test
	public void testAbsenceOfFileSeparatorAtTheEndOfPath (){
		assertTrue(!resultTestString.endsWith(File.separator));
	}
	
	@Test
	public void testMergePaths (){
		Path testPath = new Path ("fir" + "st", "path");
		String testString = "///////string_part1///string_part2///";
		Path resultPath = new Path(testPath, testString);
		String resultString = resultPath.toString();
		assertTrue(!resultString.endsWith(File.separator));
		assertTrue(resultString.startsWith(File.separator));
		String manuelString = "/first/path/string_part1/string_part2";
		assertEquals(manuelString, resultString);
	}
	@Test
	public void testIllegalArgs (){
		Path testPath = new Path ("first", "path");
		String testString = "///////string_part1///string_part2///";
		try {
			new Path(testPath, testString, 7);
			fail();
		} catch (Exception e){
		}
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testEndingSlashIsOK(){
		Path testPath = new Path("/firstOne/secondOne/");
		assertEquals("/firstOne/secondOne",testPath.toString());
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test 
	public void testMakeWithRelativePathAndTwoArguments(){
		
		assertEquals("src/test",Path.make(new RelativePath("src"),new Path("test")).toString());
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testMakeWithAbsolutePathAndTwoArguments(){
		
		assertEquals("/src/test",Path.make(new Path("src"),new Path("test")).toString());
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testEqualityOfAbsolutePaths(){
		
		assertEquals(new Path("src","test"),new Path("src","test"));
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testNonEqualityOfAbsolutePaths(){
		
		assertFalse(new Path("src","test").equals(new Path("src","main")));
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test 
	public void testPathsHaveNotSameLength(){
		
		assertFalse(new Path("src").equals(new Path("src","main")));
		assertFalse(new Path("src","main").equals(new Path("src")));
	}

	@Test
	public void testEqualityOfRelativePaths(){

		assertEquals(new RelativePath("src","test"),new RelativePath("src","test"));
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testRelativeAndAbsolutePathCannotBeEqual(){
		
		assertFalse(new RelativePath("src").equals(new Path("src")));
		assertFalse(new Path("src").equals(new RelativePath("src")));
	}
	
	
	
}