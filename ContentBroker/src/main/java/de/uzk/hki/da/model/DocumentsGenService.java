package de.uzk.hki.da.model;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Document.
 *
 * @author Polina Gubaidullina
 * 
 */

public class DocumentsGenService {
	
	protected static Logger logger = LoggerFactory.getLogger(DocumentsGenService.class);

	public void addDocumentsToObject(Object object) {
		
		logger.debug("Create & add documents to the object ...");
		for (Package pkg : object.getPackages()) {
			for (DAFile dafile : pkg.getFiles()) {
				Document doc = createDocument(object, dafile);
				object.addDocument(doc);
			}
		}
	}
	
	private Document createDocument(Object object, DAFile dafile) {
		Document doc = null;
		String filePath = FilenameUtils.removeExtension(dafile.getRelative_path());
		if(object.getDocument(filePath)!=null) {
			doc = object.getDocument(filePath);
			doc.addDAFile(dafile);
		} else {
			doc = new Document(dafile);
		}
		return doc;
	}
}
