package de.uzk.hki.da.ct;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.PasswordUtils;

public class CTFedora3RepositoryFacadeTest {

	private Fedora3RepositoryFacade fedora;

	@Test
	public void test() throws RepositoryException, IOException{
		
//		System.setProperty("http.proxySet","true");
//        System.setProperty("http.proxyHost","localhost");
//        System.setProperty("http.proxyPort","3126");   
//        System.setProperty("http.proxyPassword","J9i64o21It");   
//        System.setProperty("http.proxyUser","danrwproxy");   
		
		try {
			fedora = new Fedora3RepositoryFacade("http://localhost:8080/fedora", "fedoraAdmin", PasswordUtils.decryptPassword("BYi/MFjKDFd5Dpe52PSUoA=="));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		String content=null;
		FileInputStream fileInputStream = new FileInputStream(new File("src/test/resources/ct/Fedora3RepositoryFacadeTest/ead.xml"));
		content = IOUtils.toString(fileInputStream, "UTF-8");
		fileInputStream.close();
		
		fedora.purgeObjectIfExists("abc", "danrw");
		fedora.createObject("abc", "danrw", "TEST");
		
		fedora.createMetadataFile("abc", "danrw", "ead123.xml", content, "label", "text/xml");
	}
}
