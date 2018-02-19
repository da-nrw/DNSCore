package daweb3

/**
 * 2014 LVR InfoKom
 * @author jpeters
 */
import grails.test.mixin.Mock
import geb.junit4.GebReportingTest
import grails.test.mixin.TestFor

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4)
@Mock(Object)
@TestFor(ObjectController)
class ObjectControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

	@Test
    void testIndex() {
        controller.index()
        assert "/object/list" == response.redirectedUrl
    }


   
}
