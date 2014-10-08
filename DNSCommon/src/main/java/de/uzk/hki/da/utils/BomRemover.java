package de.uzk.hki.da.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * BOM RemovalTool
 * Removes Byte Order Marks from UTF-8 encoded files.
 * 
 * @author Jens Peters
 * 
 */
public class BomRemover {

	private final int[] BYTE_ORDER_MARK = { 239, 187, 191 };

	public boolean startsWithBOM(File aTextFile) throws IOException {
		boolean result = false;
		if (aTextFile.length() < BYTE_ORDER_MARK.length)
			return false;
		int[] firstFewBytes = new int[BYTE_ORDER_MARK.length];
		InputStream input = null;
		try {
			input = new FileInputStream(aTextFile);
			for (int index = 0; index < BYTE_ORDER_MARK.length; ++index) {
				firstFewBytes[index] = input.read();
			}
			result = Arrays.equals(firstFewBytes, BYTE_ORDER_MARK);
		} finally {
			input.close();
		}
		return result;
	}

	public void stripBomFrom(File bomFile) throws IOException {
		long initialSize = bomFile.length();
		long truncatedSize = initialSize - BYTE_ORDER_MARK.length;
		byte[] memory = new byte[(int) (truncatedSize)];
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(bomFile));
			input.skip(BYTE_ORDER_MARK.length);
			int totalBytesReadIntoMemory = 0;
			while (totalBytesReadIntoMemory < truncatedSize) {
				int bytesRemaining = (int) truncatedSize
						- totalBytesReadIntoMemory;
				int bytesRead = input.read(memory, totalBytesReadIntoMemory,
						bytesRemaining);
				if (bytesRead > 0) {
					totalBytesReadIntoMemory = totalBytesReadIntoMemory
							+ bytesRead;
				}
			}
			overwriteWithoutBOM(memory, bomFile);
		} finally {
			input.close();
		}
		File after = new File(bomFile.getAbsolutePath());
		long finalSize = after.length();
		long changeInSize = initialSize - finalSize;
		if (changeInSize != BYTE_ORDER_MARK.length) {
			throw new RuntimeException("Change in file size: " + changeInSize
					+ " Expected change: " + BYTE_ORDER_MARK.length);
		}
	}

	private void overwriteWithoutBOM(byte[] aBytesWithoutBOM, File aTextFile)
			throws IOException {
		OutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(aTextFile));
			output.write(aBytesWithoutBOM);
		} finally {
			output.close();
		}
	}
}