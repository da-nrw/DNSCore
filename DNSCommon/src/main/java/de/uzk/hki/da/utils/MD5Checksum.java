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
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


/**
 * The Class MD5Checksum.
 *
 * @author Jens Peters
 */
public class MD5Checksum {
	
	/** The fis. */
	static InputStream fis;

   /**
    * Instantiates a new m d5 checksum.
    *
    * @param file the file
    * @throws FileNotFoundException the file not found exception
    */
   public MD5Checksum(File file) throws FileNotFoundException {
	   fis =  new FileInputStream(file);
	   
	}
   
   /**
    * Instantiates a new m d5 checksum.
    *
    * @param filename the filename
    * @throws FileNotFoundException the file not found exception
    */
   public MD5Checksum(String filename) throws FileNotFoundException {
	   fis =  new FileInputStream(filename);
	}

   /**
    * Creates the checksum.
    *
    * @return the byte[]
    */
   private static byte[] createChecksum()
   {
 
	try {
     byte[] buffer = new byte[1024];
     MessageDigest complete;
	
		complete = MessageDigest.getInstance("MD5");
	
     int numRead;
     do {
      numRead = fis.read(buffer);
      if (numRead > 0) {
        complete.update(buffer, 0, numRead);
        }
      } while (numRead != -1);
     fis.close();
     return complete.digest();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
   }

   // see this How-to for a faster way to convert
   // a byte array to a HEX string
   /**
    * Gets the m d5 checksum.
    *
    * @return the m d5 checksum
    * @throws Exception the exception
    */
   public static String getMD5Checksum() throws Exception {
     byte[] b = createChecksum();
     String result = "";
     for (int i=0; i < b.length; i++) {
       result +=
          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
      }
     return result;
   }
	
	/**
	 * Gets the md5checksum for a local on Filesystem.
	 *
	 * @param file the file
	 * @return the m d5checksum for local file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see <a href="http://www.mkyong.com/java/java-md5-hashing-example">Source</a>
	 */
	public static String getMD5checksumForLocalFile(File file) throws IOException {
		if (!file.exists()) throw new IOException("file "+file+" does not exist");
		
		// Get an MD5 implementation of MessageDigest
		String md5sumHex = "";
		FileInputStream fis = new FileInputStream(file);
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance("MD5");
			// Open file and read contents
	
			byte[] dataBytes = new byte[1024];
			 
	        int nread = 0;	        
	        while ((nread = fis.read(dataBytes)) != -1) {
	          digest.update(dataBytes, 0, nread);
	        };
	        byte[] mdbytes = digest.digest();
	
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < mdbytes.length; i++) {
	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
			
			// (Optionally) convert the MD5 byte array to a hex string
			md5sumHex = sb.toString();
		} catch (Exception e) {
			throw new IOException("Error while trying to generate md5sum for "+file.toString(),e);
		} finally {
			fis.close();
		}
		return md5sumHex;
	}
}
