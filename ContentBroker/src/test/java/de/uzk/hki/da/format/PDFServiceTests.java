package de.uzk.hki.da.format;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.format.PdfService;
import de.uzk.hki.da.test.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class PDFServiceTests {
	
	private static final String BASE_DIR = Path.make(TC.TEST_ROOT_FORMAT,"PDFServiceTests").toString();
	
	@After
	public void tearDown(){
		new File(BASE_DIR+"/trgt.pdf").delete();
	}
	
	@Test
	public void testNumberOfPagesExceedsDocumentsPages() throws IOException{
		PdfService service = new PdfService(new File(BASE_DIR+"/src.pdf"), 
				new File(BASE_DIR+"/trgt.pdf"));
		
		service.reduceToCertainPages("5", ""); // src doc only has one page
		PDDocument trgt = PDDocument.load(new File(BASE_DIR+"/trgt.pdf"));
		assertThat(trgt.getDocumentCatalog().getAllPages().size()).isEqualTo(1);
	}
	
	
	@Test
	public void testCertainPagesExceedsDocumentsPages() throws IOException{
		PdfService service = new PdfService(new File(BASE_DIR+"/src.pdf"), 
				new File(BASE_DIR+"/trgt.pdf"));
		
		service.reduceToCertainPages("", "1 3 5"); // src doc only has one page
		PDDocument trgt = PDDocument.load(new File(BASE_DIR+"/trgt.pdf"));
		assertThat(trgt.getDocumentCatalog().getAllPages().size()).isEqualTo(1);
	}
	

}
