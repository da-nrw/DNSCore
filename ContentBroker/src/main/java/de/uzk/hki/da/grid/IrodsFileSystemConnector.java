package de.uzk.hki.da.grid;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andres Quast
 * quast@hbz-nrw.de
 */

public class IrodsFileSystemConnector {
		/** The logger. */
		private static Logger logger = LoggerFactory
				.getLogger(IrodsSystemConnector.class);

		private volatile static IRODSFileSystem iFS;
		
		public IrodsFileSystemConnector() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * Lazy implementation for IRODSFileSystem returns Instance as Singleton
		 * @author Andres Quast
		 * @return IRODSFilesystem
		 */
		public static IRODSFileSystem getInstance(){
			if(iFS == null){
				synchronized(IRODSFileSystem.class) {
					try {
						iFS = new IRODSFileSystem();
					} catch (JargonException e) {
						logger.error("Can't create IRODSFileSystem-Instance");
						logger.debug(e.toString());
					}				
				}
			}
			return iFS;	
		}
}
