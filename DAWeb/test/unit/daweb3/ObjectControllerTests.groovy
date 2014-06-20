package daweb3

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

    void testList() {
		controller.grailsApplication.config.userAreaRootPath = "/path_to_/userhome/SIP"
		def contractor = new Contractor(shortName: "rods", admin: 1)

	
		controller.session["contractor"] = contractor
        def model = controller.list()

        assert model.objectInstanceList.size() == 0
        assert model.objectInstanceTotal == 0
    }


    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/object/list'


        populateValidParams(params)
        def object = new Object(params)

        assert object.save() != null

        params.id = object.id

        def model = controller.show()

        assert model.objectInstance == object
    }

   
}
