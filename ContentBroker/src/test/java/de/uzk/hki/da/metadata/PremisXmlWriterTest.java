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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.AudioRestriction;
import de.uzk.hki.da.model.ObjectPremisXmlWriter;
import de.uzk.hki.da.model.PremisXmlValidator;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.ImageRestriction;
import de.uzk.hki.da.model.MigrationRight;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.RightsStatement;
import de.uzk.hki.da.model.TextRestriction;
import de.uzk.hki.da.model.VideoRestriction;
import de.uzk.hki.da.model.MigrationRight.Condition;
import de.uzk.hki.da.model.PublicationRight.Audience;


/**
 * The Class PremisXmlWriterTest.
 */
public class PremisXmlWriterTest {
	
	/**
	 * Clean up.
	 */
	@After
	public void cleanUp() {
		if (new File("src/test/resources/metadata/premis_test_xml_metadata_stream_writer_out.xml").exists())
			new File("src/test/resources/metadata/premis_test_xml_metadata_stream_writer_out.xml").delete();
	}
	
	/**
	 * Test write metadata.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWriteMetadata() throws ParserConfigurationException, SAXException, IOException {
		
		User c = new User();
		c.setShort_name("csn");
		
		Object object = new Object();
		object.setIdentifier("urn");
		object.setContractor(c);
		object.setOrig_name("orig_name");
		
		Package pkg = new Package();
		pkg.setName("7");
		object.getPackages().add(pkg);
		
		Event event = new Event();
		event.setIdentifier("23");
		event.setDate(new Date());
		event.setType("INGEST");
		event.setAgent_name("Stadtarchiv Castrop-Rauxel");
		event.setAgent_type("CONTRACTOR");
		event.setIdType(Event.IdType.INGEST_ID);
		object.getPackages().get(0).getEvents().add(event);
		
		RightsStatement rightsStatement = new RightsStatement();
		rightsStatement.setId("42");
		
		List<PublicationRight> publicationRights = new ArrayList<PublicationRight>();
		
		PublicationRight publicationRight = new PublicationRight();
		publicationRight.setAudience(Audience.PUBLIC);
		publicationRight.setLawID("EPFLICHT");
		publicationRight.setStartDate(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2023, 5, 23);
		publicationRight.setImageRestriction(new ImageRestriction("640","480","asdf"));
		publicationRight.setVideoRestriction(new VideoRestriction("640","480",30));
		publicationRight.setAudioRestriction(new AudioRestriction(30));
		publicationRight.setTextRestriction(new TextRestriction(15,new int[]{23,42}));
		publicationRights.add(publicationRight);
		
		PublicationRight publicationRight2 = new PublicationRight();
		publicationRight2.setAudience(Audience.INSTITUTION);
		publicationRight2.setStartDate(new Date());
		calendar.set(2023, 5, 23);
		publicationRight2.setImageRestriction(new ImageRestriction("640","480","fdsa"));
		publicationRight2.setVideoRestriction(new VideoRestriction("640","480",30));
		publicationRight2.setAudioRestriction(new AudioRestriction(30));
		publicationRight2.setTextRestriction(new TextRestriction(15));
		publicationRights.add(publicationRight2);
		
		rightsStatement.setPublicationRights(publicationRights);
		
		MigrationRight migrationRight = new MigrationRight();
		migrationRight.setCondition(Condition.NOTIFY);
		migrationRight.setStartDate(new Date());
		rightsStatement.setMigrationRight(migrationRight);
		
		object.setRights(rightsStatement);
		
		ObjectPremisXmlWriter writer = new ObjectPremisXmlWriter();

		writer.serialize(object, new File("src/test/resources/metadata/premis_test_xml_metadata_stream_writer_out.xml"));
		
		PremisXmlValidator.validatePremisFile(new File("src/test/resources/metadata/premis_test_xml_metadata_stream_writer_out.xml"));		
	}

}
