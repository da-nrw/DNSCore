/**
  DA-NRW Software Suite | Password Encryptor
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

package de.uzk.hki.da.passwordEncryptor;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Thomas Kleinke
 *
 */
public class passwordEncryptor {

	private static String encryptPasswordForContentBroker(String password) {
		
		byte key[] = "394z57f4".getBytes();
		byte encryptedPassword[];
		
		try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key));
			
			Cipher encrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
			encrypt.init(Cipher.ENCRYPT_MODE, secretKey,
					new IvParameterSpec(new byte[] { 0x01, 0x02, 0x04, 0x10, 0x01, 0x02, 0x04, 0x10 }));
			encryptedPassword = encrypt.doFinal(password.getBytes());
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Couldn't encrypt password " + password + e);
		}
		
		return new String(Base64.encodeBase64(encryptedPassword));
	}
	
	private static String encryptPasswordForMicroservices(String password) {
		
		String result = "";

		int cryptTable[] = {
				1323, 1213, 4312, 3323, 2034, 3109, 3424, 1214,
				1134, 4454, 4599, 3646, 3534, 2523, 2435, 2870,
				4345, 3124, 3656, 2777, 3232, 1834, 1235, 1545,
				2742, 1099, 3631, 2243, 3173, 2421, 3129, 1037 };
		
		
		for (int i = 0; i < password.length() ; i++)
		{
			int c = password.charAt(i);
			c += cryptTable[i%32];
			String d = new String(Character.toChars(c/85 + 40));
		    String e = new String(Character.toChars(c%85 + 40));
			
			result += e;
			result += d;
		}

		return result;
	}
		
	public static void main(String[] args) {
	
		if (args.length <= 1 || args[0].equals("--help") || args[0].equals("-h")
							 || args[0].equals("help")   || args[0].equals("h")
							 || !(args[0].equals("-cb") || args[0].equals("-ms")))
		{
			System.out.println("Usage: java -jar passwordEncryptor.jar [-cb | -ms] [PASSWORD]");
			System.out.println("-cb: Encrypt password for ContentBroker configuration files");
			System.out.println("-ms: Encrypt password for microservices configuration file");
			return;
		}
		
		String encryptedPassword = "";
		if (args[0].equals("-cb"))
			encryptedPassword = encryptPasswordForContentBroker(args[1]);
		else if (args[0].equals("-ms"))
			encryptedPassword = encryptPasswordForMicroservices(args[1]);
		System.out.println(encryptedPassword);	
	}


}
