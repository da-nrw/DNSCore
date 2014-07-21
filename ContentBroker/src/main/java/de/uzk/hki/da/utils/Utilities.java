/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





/**
 * The Class Utilities.
 */
public class Utilities {
	
	/** The logger. */
	public static Logger logger = LoggerFactory.getLogger(Utilities.class);

	/**
	 * @param propertiesFile
	 * @return properties
	 * @throws IOException
	 * @throws FileNotFoundException if propertiesfile does not exist
	 * @author Daniel M. de Oliveira
	 */
	public static Properties read(File propertiesFile) throws IOException{
		if (!propertiesFile.exists()) throw new FileNotFoundException(propertiesFile+" does not exist");
		InputStream in = new FileInputStream(propertiesFile);
		Properties props = new Properties();
		props.load(in);
		return props;
	}
	
	/**
	 * Slashize.
	 *
	 * @param arg the arg
	 * @return the string
	 * @author Daniel M. de Oliveira
	 */
	public static String slashize(String arg) {
		
		if (!(arg == null || arg.isEmpty() || arg.endsWith("/"))) arg+="/";
		return arg;
	}
	
	/**
	 * Iterates over the items in operateOn and replaces any matches
	 * of from to to.
	 *
	 * @param operateOn the operate on
	 * @param from the from
	 * @param to the to
	 * @author Daniel M. de Oliveira
	 */
	public static void replace(String operateOn[],String from,String to){
		for (int i=0;i<operateOn.length;i++){
			
			Pattern patternOutput = Pattern.compile(from);
			Matcher matcherOutput = patternOutput.matcher(operateOn[i]);
			operateOn[i] = matcherOutput.replaceFirst(to);
		}
	}
	
	
	
	/**
	 * Parses the arguments.
	 *
	 * @param args the args
	 * @param props the props
	 */
	public static void parseArguments(String[] args,Properties props) {
		if (props==null){
			logger.error("props is null in parseArguments");
			return;
		}
		
		for (String arg : args) {
			if (arg.startsWith("--")) {
				arg = arg.substring(2);
				if (arg.contains("=")) {
					String[] split = arg.split("=");
					props.put(split[0], split[1]);
				} else {
					props.put(arg, arg);
				}
			}
		}
	}

	
	/**
	 * Today as simple iso date.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String todayAsSimpleIsoDate(Date date){
		
		Format formatter = new SimpleDateFormat("yyyyMMdd");
		  return formatter.format(date);
		
	}
	
	/**
	 * Checks if is sidecar file.
	 *
	 * @param filename the filename
	 * @param extensionList the extension list
	 * @return true, if is sidecar file
	 * @author Thomas Kleinke
	 */
	public static boolean isSidecarFile(String filename, String extensionList) {
		
		int position = 0;
		while (position < extensionList.length())
		{
			int index = extensionList.indexOf(';', position);
			if (index == -1)
				index = extensionList.length();
			String extension = extensionList.substring(position, index).toLowerCase();
			position = index + 1;
			
			if (filename.toLowerCase().endsWith("." + extension))
				return true;			
		}
		
		return false;
	}
	
	/**
	 * Check for whitespace.
	 *
	 * @param string the string
	 * @return true, if successful
	 */
	public static boolean checkForWhitespace(String string) {
		
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(string);
		return matcher.find();		
	}
	
	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the string
	 */
	public static String readFile(File file) {

		Reader reader;
		try {
			reader = new FileReader(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Couldn't create reader", e);
		}
		String modsText = "";

		try {
			for (int c; (c = reader.read() ) != -1; )
				modsText += (char) c;

			reader.close();								

		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + file.getAbsolutePath(), e);
		}

		return modsText;		
	}
		
	
	
	/**
	 * Creates the string.
	 *
	 * @param stringArray the string array
	 * @return the string
	 * @author Thomas Kleinke
	 */
	public static String createString(String[] stringArray) {
		StringBuilder result = new StringBuilder("");
		for (String s : stringArray) {
			if (!result.equals(""))
				result.append(" ");
			result.append(s);			
		}		
		return result.toString();
	}
	
	/**
	 * Creates the string.
	 *
	 * @param stringList the string list
	 * @return the string
	 * @author Thomas Kleinke
	 */
	public static String createString(List<String> stringList) {
		StringBuilder result = new StringBuilder("");
		for (String s : stringList) {
			if (!result.equals(""))
				result.append(" ");
			result.append(s);			
		}
		return result.toString();
	}
	
	public static String getHeapSpaceInformation() {
		
		String output;
		Runtime runtime = Runtime.getRuntime();
		
		output = "Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024*1024) + " MB\n";
        output += "Free Memory: " + runtime.freeMemory() / (1024*1024) + "MB \n";
        output += "Total available Memory:" + runtime.totalMemory() / (1024*1024) + "MB \n";
        output += "Max Memory:" + runtime.maxMemory() / (1024*1024) + "MB \n";
		
        return output;
	}
}
