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

package de.uzk.hki.da.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class URNCheckDigitGenerator.
 */
public class URNCheckDigitGenerator {
	
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(URNCheckDigitGenerator.class);

	/** The Constant CONSTANTS. */
	@SuppressWarnings("serial")
	public static final Map<String,String> CONSTANTS =
			new HashMap<String,String>(){
		{
			put("0", "1");	
			put("1", "2");	
			put("2", "3");	
			put("3", "4");
			put("4", "5");		
			put("5", "6");	
			put("6", "7");
			put("7", "8");		
			put("8", "9");		
			put("9", "41");	
			put("A", "18");	
			put("B", "14");
			put("C", "19");
			put("D", "15");
			put("E", "16");
			put("F", "21");		
			put("G", "22");		
			put("H", "23");		
			put("I", "24");	
			put("J", "25");             
			put("K", "42");             
			put("L", "26");             
			put("M", "27");            
			put("N", "13");            
			put("O", "28");            
			put("P", "29");             
			put("Q", "31");            
			put("R", "12");             
			put("S", "32");             
			put("T", "33");            
			put("U", "11");             
			put("V", "34");            
			put("W", "35");             
			put("X", "36");             
			put("Y", "37");           
			put("Z", "38");           
			put("+", "49");      
			put(":", "17");		
			put("_", "43");		
			put("-", "39");
			put(".", "47");		
			put("/", "45");
		}
	};
	
/**
 * Check digit.
 *
 * @param base the base
 * @return the string
 */
public String checkDigit( String base ){
		
		String string = numbersMappedAndConcatenated( base );
		logger.debug("mapped numbers as concatenated string: " + string);
		
		int productSum= 0;
		int i = 1;
		for (char character : string.toCharArray()){
			
			productSum += i*(character-'0');
			i++;
		}
		logger.debug("productSum: "+productSum);
		
		
		int lastNumberFromString= (string.toCharArray()[string.length()-1])-'0';
		float quotient= productSum/(float) lastNumberFromString;
		
		String quotientAsString= Float.toString( quotient );
		logger.debug("quotient: "+quotientAsString);
		
		String lastNumberBeforeDot = findLastNumberBeforeDot(quotientAsString);
		logger.debug("lastNumberBeforeDot: "+lastNumberBeforeDot);
		return lastNumberBeforeDot;
	}
	
	
	
	/**
	 * Find last number before dot.
	 *
	 * @param str the str
	 * @return the string
	 */
	private String findLastNumberBeforeDot(String str){
		
		for (int i=0;i<str.length();i++){
			if (str.charAt(i)=='.') return new String(""+str.charAt(i-1)); 
		}
		
		throw new RuntimeException("Error while parsing float value within URNGenerator.findLastNumberBeforeDot()");
	}
	
	
	
	/**
	 * Numbers mapped and concatenated.
	 *
	 * @param base the base
	 * @return the string
	 */
	private String numbersMappedAndConcatenated( String base ){
		
		String numbers = "";
		for (char character : base.toCharArray()){
			
			numbers+= CONSTANTS.get( new String(""+character).toUpperCase() );
		}
		
		return numbers;
		
	}
	
}
