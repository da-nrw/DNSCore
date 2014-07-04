package daweb3

/**
 * 2014 LVR InfoKom
 * @author jpeters
 */
import org.junit.*
import grails.test.mixin.*

@TestFor(ObjectController)
@Mock(Object)
class ObjectControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/object/list" == response.redirectedUrl
    }


   
}
