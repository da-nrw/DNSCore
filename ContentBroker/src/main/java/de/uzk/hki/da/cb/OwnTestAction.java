package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;

public class OwnTestAction extends AbstractAction {

	@Override
	public void checkConfiguration() {
		// TODO Auto-generated method stub
		Object o=getObject().getContractor();
		

	}

	@Override
	public void checkPreconditions() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException,
			SubsystemNotAvailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rollback() throws Exception {
		// TODO Auto-generated method stub

	}

}
