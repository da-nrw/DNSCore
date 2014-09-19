/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.cb;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;

/**
 * @author Daniel M. de Oliveira
 */
public class ProcessUserDecisionsActionTests {
	
	
	private Job job;
	private ProcessUserDecisionsAction action;

	@Before
	public void test(){
		
		job = new Job();
		ConversionInstruction ins = new ConversionInstruction();
		job.getConversion_instructions().add(ins);
		action = new ProcessUserDecisionsAction();
		action.setJob(job);
		
	}
	
	@Test
	public void testRemoveConversionInstructionsIfAnswerIsNegative() 
			throws FileNotFoundException, UserException, IOException, 
				RepositoryException, JDOMException, ParserConfigurationException, SAXException{
		
		job.setAnswer(C.ANSWER_NO);
		assertTrue(action.implementation());
		assertTrue(job.getConversion_instructions().isEmpty());
		assertEquals(C.INGEST_REGISTER_URN_ACTION_START_STATUS,action.getEndStatus());
	}
	
	@Test
	public void testContinueConversionIfAnswerIsPositive() 
			throws FileNotFoundException, UserException, IOException, 
				RepositoryException, JDOMException, ParserConfigurationException, SAXException{
		
		job.setAnswer(C.ANSWER_YO);
		assertTrue(action.implementation());
		assertFalse(job.getConversion_instructions().isEmpty());
		assertEquals(C.INGEST_REGISTER_URN_ACTION_START_STATUS,action.getEndStatus());
	}
}
