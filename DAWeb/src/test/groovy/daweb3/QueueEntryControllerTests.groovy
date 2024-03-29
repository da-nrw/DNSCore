/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln, 2014 LVR InfoKom

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

/**
 * @author jpeters
 */
package daweb3


import grails.test.mixin.TestFor
import grails.test.mixin.Mock


import geb.junit4.GebReportingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@TestFor(QueueEntryController)
@Mock(QueueEntry)
@RunWith(JUnit4)
class QueueEntryControllerTests{


    def populateValidParams(params) {
      assert params != null
	 	println "QueueEntryControllerTests"
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

	@Test
    void testIndex() {
        controller.index()
        assert "/queueEntry/list" == response.redirectedUrl
    }

}
