package de.uzk.hki.da.convert;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Test;

/**
 * @author Daniel M. de Oliveira
 */
public class PDFServiceTests {
	
	@After
	public void tearDown(){
		new File("src/test/resources/convert/PDFServiceTests/trgt.pdf").delete();
	}
	
	@Test
	public void testNumberOfPagesExceedsDocumentsPages() throws IOException{
		PdfService service = new PdfService(new File("src/test/resources/convert/PDFServiceTests/src.pdf"), 
				new File("src/test/resources/convert/PDFServiceTests/trgt.pdf"));
		
		service.reduceToCertainPages("5", ""); // src doc only has one page
		PDDocument trgt = PDDocument.load(new File("src/test/resources/convert/PDFServiceTests/trgt.pdf"));
		assertThat(trgt.getDocumentCatalog().getAllPages().size()).isEqualTo(1);
	}
	
	
	@Test
	public void testCertainPagesExceedsDocumentsPages() throws IOException{
		PdfService service = new PdfService(new File("src/test/resources/convert/PDFServiceTests/src.pdf"), 
				new File("src/test/resources/convert/PDFServiceTests/trgt.pdf"));
		
		service.reduceToCertainPages("", "1 3 5"); // src doc only has one page
		PDDocument trgt = PDDocument.load(new File("src/test/resources/convert/PDFServiceTests/trgt.pdf"));
		assertThat(trgt.getDocumentCatalog().getAllPages().size()).isEqualTo(1);
	}
	

}
