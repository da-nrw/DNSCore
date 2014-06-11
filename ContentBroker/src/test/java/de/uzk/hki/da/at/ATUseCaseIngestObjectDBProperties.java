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
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import de.uzk.hki.da.model.Object;

/**
 * @author Thomas KLeinke
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestObjectDBProperties extends Base{

	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		clearDB();
		cleanStorage();
	}
	
	/**
	 * @author Thomas Kleinke 
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	@Test
	public void testStartDateAndFormatsGetSaved() throws IOException, InterruptedException, ParseException{
		Object object = ingest("ATUseCaseIngestStartDateFormats");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));		
		assertEquals(dateFormat.parse("2028-09-12"), object.getStatic_nondisclosure_limit());
		assertEquals(null, object.getDynamic_nondisclosure_limit());
		
		assertThat(object.getOriginal_formats()).contains("fmt/101");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/101")).isEqualTo(1);
		assertThat(object.getOriginal_formats()).contains("fmt/116");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/116")).isEqualTo(1);
		
		assertThat(object.getMost_recent_formats()).contains("fmt/101");
		assertThat(StringUtils.countOccurrencesOf(object.getMost_recent_formats(), "fmt/101")).isEqualTo(1);
		assertThat(object.getMost_recent_formats()).contains("fmt/353");
		assertThat(StringUtils.countOccurrencesOf(object.getMost_recent_formats(), "fmt/353")).isEqualTo(1);
		
		assertEquals(100, object.getObject_state());
	}

	/**
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	@Test
	public void testLawGetsSaved() throws IOException, InterruptedException, ParseException{
		Object object = ingest("ATUseCaseIngestLaw");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
		assertEquals(dateFormat.parse("2013-09-25"), object.getStatic_nondisclosure_limit());
		assertEquals("URHG_DE", object.getDynamic_nondisclosure_limit());
		
		assertThat(object.getOriginal_formats()).contains("fmt/101");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/101")).isEqualTo(1);
		assertThat(object.getOriginal_formats()).contains("fmt/353");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/353")).isEqualTo(1);
		
		assertThat(object.getMost_recent_formats()).contains("fmt/101");
		assertThat(StringUtils.countOccurrencesOf(object.getMost_recent_formats(), "fmt/101")).isEqualTo(1);
		assertThat(object.getMost_recent_formats()).contains("fmt/353");
		assertThat(StringUtils.countOccurrencesOf(object.getMost_recent_formats(), "fmt/353")).isEqualTo(1);
		
		assertEquals(100, object.getObject_state());
		assertEquals(localNode.getName(),object.getInitial_node());
	}

	/**
	 * @author Daniel M. de Oliveira
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testCodecsGetSaved() throws IOException, InterruptedException{
		Object object = ingest("ATUseCaseIngestCodecs");
		
		assertThat(object.getOriginal_formats()).contains("x-fmt/384");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "x-fmt/384")).isEqualTo(1);
		assertThat(object.getOriginal_formats()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/5")).isEqualTo(1);
		assertThat(object.getOriginal_formats()).contains("fmt/200");
		assertThat(StringUtils.countOccurrencesOf(object.getOriginal_formats(), "fmt/200")).isEqualTo(1);
		
		assertThat(object.getMostRecentSecondaryAttributes()).contains("cinepak");
		assertThat(StringUtils.countOccurrencesOf(object.getMostRecentSecondaryAttributes(), "cinepak")).isEqualTo(1);
		assertThat(object.getMostRecentSecondaryAttributes()).contains("dvvideo");
		assertThat(StringUtils.countOccurrencesOf(object.getMostRecentSecondaryAttributes(), "dvvideo")).isEqualTo(1);
		assertThat(object.getMostRecentSecondaryAttributes()).contains("svq1");
		assertThat(StringUtils.countOccurrencesOf(object.getMostRecentSecondaryAttributes(), "svq1")).isEqualTo(1);
	}
	
	
}
