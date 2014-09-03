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

package de.uzk.hki.da.repository;

import java.util.Map;

/**
 * Decouples the repository logic used for indexing
 * metadata from specific implementations.
 * 
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public interface MetadataIndex {
	
	/**
	 * Indexes metadata.
	 * 
	 * @param indexName the name of the index
	 * @param collection (also called type) in the index
	 * @param id 
	 * @param data nested key value data to be indexed
	 * @throws MetadataIndexException if implementation cannot handle the request due to corrupt data or connection problems. 
	 */
	void indexMetadata(String indexName, String collection, String id, Map<String, Object> data)
			throws MetadataIndexException;
}
