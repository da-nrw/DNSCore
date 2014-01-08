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

package de.uzk.hki.da.grid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.Utilities;

/**
 * For acceptance testing on developer machines
 * @author Daniel M. de Oliveira
 *
 */
public class FakeGridFacade implements GridFacade {

	private String gridCacheAreaRootPath;
	private boolean initialized=false;
	
	private void init(){
		Properties properties = null;
		InputStream in;
		try {
			in = new FileInputStream("conf/config.properties");
			properties = new Properties();
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gridCacheAreaRootPath = Utilities.slashize((String)properties.getProperty("localNode.gridCacheAreaRootPath"));
		initialized=true;
	}
	
	
	@Override
	public boolean put(File file, String address_dest, StoragePolicy sp) throws IOException {
		if (!initialized) init();

		System.out.println("putting: "+file+" to "+gridCacheAreaRootPath+address_dest);
		FileUtils.copyFile(file, new File(gridCacheAreaRootPath+address_dest));
		return true;
	}

	@Override
	public void get(File destination, String sourceFileAdress)
			throws IOException {
		if (!initialized) init();
		
		System.out.println("retrieving: "+gridCacheAreaRootPath+sourceFileAdress+" to "+destination);
		FileUtils.copyFile(new File(gridCacheAreaRootPath+sourceFileAdress),destination);
	}

	@Override
	public boolean isValid(String address_dest) {
		return true;
	}

	@Override
	public boolean storagePolicyAchieved(String address_dest, StoragePolicy sp) {
		return true;
	}

	@Override
	public long getFileSize(String address_dest) throws IOException {
		return 0;
	}

	
}
