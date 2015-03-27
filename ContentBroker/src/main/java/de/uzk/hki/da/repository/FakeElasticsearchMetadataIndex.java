package de.uzk.hki.da.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FakeElasticsearchMetadataIndex implements MetadataIndex{

	@Override
	public void indexMetadata(String indexName, String collection, String id,
			Map<String, Object> data) throws MetadataIndexException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareAndIndexMetadata(String indexName, String id,
			String edmContent) throws RepositoryException {
		
		FileOutputStream fop = null;
		File file;
 
		try {
 
			file = new File("/tmp/edmContent");
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = edmContent.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	MAP
	
	@Override
	public String getIndexedMetadata(String indexName, String objectId) {
		if (objectId.contains("Inventarnummer-1") || objectId.endsWith("-md801613") || objectId.endsWith("-1")) // lido, mets, xmp
			return "Nudelmaschine in Originalverpackung" + " Text Text// mahels///Titel" + " Dieser Brauch zum Sankt Martinstag";
		else 
//			ead
			return "\"dc:date\":[\"1938-01-01/1939-12-31\"]";
	}

	@Override
	public String getAllIndexedMetadataFromIdSubstring(String indexName,
			String objectId) {
		return null;
	}
	@Override
	public void deleteFromIndex(String indexName, String objectID)
			throws MetadataIndexException {
	}

}
