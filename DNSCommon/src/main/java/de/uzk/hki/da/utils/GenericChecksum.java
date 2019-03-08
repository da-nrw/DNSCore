package de.uzk.hki.da.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;


public class GenericChecksum {
	static final int BUFFER_SIZE=1024*4;
	public static final Algorithm DEFAULT_CHECKSUM_ALGO=Algorithm.MD5;//Algorithm.SHA256; 
	public static final Algorithm DEFAULT_CHECKSUM_ALGO_FOR_DAF=Algorithm.SHA512;//Algorithm.MD5;
	public static enum Algorithm{MD5("MD5"),SHA1("SHA-1"),SHA256("SHA-256"),SHA384("SHA-384"),SHA512("SHA-512");
		private final String algoName;
		private Algorithm(final String algo) {
	        this.algoName = algo;
	    }
	    @Override public String toString() { return algoName; }
	};
	
	public static String getChecksumForLocalFile(File file) throws IOException {
		return getChecksumForLocalFile(DEFAULT_CHECKSUM_ALGO,file);
	}

	
	/**
	 * Gets the checksum for a local file on Filesystem.
	 *
	 * @param file the file
	 * @return the m d5checksum for local file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see <a href="http://www.mkyong.com/java/java-md5-hashing-example">Source</a>
	 */
	public static String getChecksumForLocalFile(Algorithm algorithm,File file) throws IOException {
		if (!file.exists()) throw new IOException("file "+file+" does not exist");
		
		// Get an MD5 implementation of MessageDigest
		String md5sumHex = "";
		byte[] mdbytes=new byte[]{0x0};
		FileInputStream fis = new FileInputStream(file);
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance(algorithm.toString());
			// Open file and read contents
	
			byte[] dataBytes = new byte[BUFFER_SIZE];
			 
	        int nread = 0;	        
	        while ((nread = fis.read(dataBytes)) != -1) {
	          digest.update(dataBytes, 0, nread);
	        };
	        mdbytes = digest.digest();
	
	        md5sumHex=byteToHexString(mdbytes);
	       
		} catch (Exception e) {
			throw new IOException("Error while trying to generate hashsum("+algorithm+") for "+file.toString(),e);
		} finally {
			fis.close();
		}
		return md5sumHex;
	}

	  public static byte[] hexStringToByte(String s) {
	    byte[] b = new byte[s.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	      int index = i * 2;
	      int v = Integer.parseInt(s.substring(index, index + 2), 16);
	      b[i] = (byte) v;
	    }
	    return b;
	  }
	
	public static String byteToHexString(byte[] mdbytes){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
		
        return sb.toString();
	}
	
	public static String encodeBase64(String checksum){
		String checksumEncoded=Base64.getEncoder().encodeToString(hexStringToByte(checksum));
		return checksumEncoded;
	}
	public static String decodeBase64(String checksumEncoded){
		byte[] decodedBytes=Base64.getDecoder().decode(checksumEncoded);
		String checksumDecoded=byteToHexString(decodedBytes);
		return checksumDecoded;
	}
	
	public static Algorithm recognizeAlgorithmFromChecksum(String checksum){
		switch(checksum.length()){
			case 32: return Algorithm.MD5; 
			case 40: return Algorithm.SHA1; 
			//case 56: return Algorithm.SHA224; break;
			case 64: return Algorithm.SHA256; 
			case 96: return Algorithm.SHA384; 
			case 128: return Algorithm.SHA512; 
		}
		return null;
	}
}
