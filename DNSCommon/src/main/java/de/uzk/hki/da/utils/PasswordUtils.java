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

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Decrypt password.
 *
 * @param password the password
 * @return the string
 * @author Thomas Kleinke
 */
public class PasswordUtils {

	public static String decryptPassword(String password) {
		
		byte key[] = "394z57f4".getBytes();
		byte decryptedPassword[];
		
		try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key));
			Cipher decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
			decrypt.init(Cipher.DECRYPT_MODE, secretKey,
					new IvParameterSpec(new byte[] { 0x01, 0x02, 0x04, 0x10, 0x01, 0x02, 0x04, 0x10 }));
			decryptedPassword = decrypt.doFinal(Base64.decodeBase64(password.getBytes()));
			
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Couldn't decrypt password " + password + e);
		}
		
		return new String(decryptedPassword);		
	}

}
