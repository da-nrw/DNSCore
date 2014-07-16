package de.uzk.hki.da.format;

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.activation.FileDataSource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adds PDF Functions of Apache PDFBox to DA-NRW.
 * 
 * @author Jens Peters
 * @author Sebastian Cuy
 */

public class PdfService {

	/** The src pdf file. */
	File srcPdfFile;
	
	/** The target pdf file. */
	File targetPdfFile;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(PdfService.class);

	/**
	 * Instantiates a new pdf service.
	 *
	 * @param srcPdf the src pdf
	 * @param targetPdf the target pdf
	 */
	public PdfService(File srcPdf, File targetPdf) {
		this.srcPdfFile = srcPdf;
		this.targetPdfFile = targetPdf;
	}

	/**
	 * Instantiates a new pdf service.
	 */
	public PdfService() {

	}

	/**
	 * Generates a new PDF which only contains certainPages of the original document.
	 * Users can choose if they wish to reduce to a number of pages (beginning from the first page)
	 * or to a certain set of pages. Both options can be used at the same time. 
	 * By setting one of the parameters to either "" or null any of the options the according 
	 * option will not be used.
	 *
	 * @param numberOfPagesText null or empty if unused or "n" (e.g. "2" for two pages from the beginning of the document).
	 * @param certainPagesText white space separated list of numbers that mark pages which should be part of the target document.
	 * 
	 * @throws IOException 
	 * @author Jens Peters
	 * @author Sebastian Cuy
	 * @author Daniel M. de Oliveira
	 */
	public void reduceToCertainPages(
			String numberOfPagesText,
			String certainPagesText) throws IOException {

		PDDocument srcPdf = null;
		PDDocument targetPdf = null;
		if (srcPdfFile == null)
			throw new IllegalStateException("srcFile not set");

		srcPdf = PDDocument.load(srcPdfFile);
		targetPdf = new PDDocument();

		@SuppressWarnings("rawtypes")
		List srcPages = srcPdf.getDocumentCatalog().getAllPages();

		int numberOfPages = 0;
		if (numberOfPagesText != null && !numberOfPagesText.isEmpty()) {
			numberOfPages = Integer.parseInt(numberOfPagesText);
			for (int i = 0; i < Math.min(numberOfPages,srcPages.size()); i++) 
				targetPdf.addPage((PDPage) srcPages.get(i));
		}

		if (certainPagesText != null && !certainPagesText.isEmpty()) {
			String[] certainPagesTexts = certainPagesText.split(" ");
			int[] certainPages = new int[certainPagesTexts.length];
			for (int i = 0; i < certainPagesTexts.length; i++) {
				certainPages[i] = Integer.parseInt(certainPagesTexts[i]);
			}
			Arrays.sort(certainPages);
			for (int i = 0; i < certainPages.length; i++) {
				if (certainPages[i] > numberOfPages
						&& srcPages.size() > certainPages[i] - 1)
					targetPdf.addPage((PDPage) srcPages
							.get(certainPages[i] - 1));
			}
		}

		try {
			targetPdf.save(targetPdfFile);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create PDF!", e);
		} finally {
			targetPdf.close();
		}

	}

	/**
	 * The Apache Preflight library is a Java tool that implements a parser
	 * compliant with the ISO-19005 specification (aka PDF/A-1). Check
	 * Compliance with PDF/A-1b
	 *
	 * @param file the file
	 * @return true, if successful
	 * @Author: Jens Peters
	 */

	public static boolean validatePdfA(File file) {

		ValidationResult result = null;
		try {

			FileDataSource fd = new FileDataSource(file);
			PreflightParser parser;
			parser = new PreflightParser(fd);
			parser.parse();
			PreflightDocument document = parser.getPreflightDocument();
			document.validate();
			result = document.getResult();
			document.close();
		} catch (Exception e) {
			logger.error("Exception validating PDF/A compliance for " + file
					+ " " + e.getCause());
			return false;
		}
		if (result.isValid()) {
			logger.info("The file " + file + " is a valid PDF/A-1b file");
			return true;
		} else {
			logger.info("The file" + file
					+ " is not a valid PDF/A-1b, error(s) :");
			for (ValidationError error : result.getErrorsList()) {
				logger.info(error.getErrorCode() + " : " + error.getDetails());
			}
		}
		return false;
	}

}
